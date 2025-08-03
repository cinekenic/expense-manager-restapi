package com.crud.restapi.service;

import com.crud.restapi.entity.ExpenseEntity;
import com.crud.restapi.entity.ProfileEntity;
import com.crud.restapi.repository.ExpenseRepository;
import com.crud.restapi.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DailyExpenseSummarySchedulerTest {

    private ExpenseRepository expenseRepository;
    private ProfileRepository profileRepository;
    private JavaMailSender mailSender;
    private DailyExpenseSummaryScheduler scheduler;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        profileRepository = mock(ProfileRepository.class);
        mailSender = mock(JavaMailSender.class);
        scheduler = new DailyExpenseSummaryScheduler(expenseRepository, profileRepository, mailSender);
    }

    @Test
    void shouldSendSummaryEmailToUserWithExpenses() {
        // given
        ProfileEntity user = new ProfileEntity();
        user.setId(1L);
        user.setEmail("user@example.com");

        ExpenseEntity expense = new ExpenseEntity();
        expense.setAmount(BigDecimal.valueOf(99.99));
        expense.setOwner(user);
        expense.setDate(Timestamp.valueOf(LocalDateTime.now()));

        when(profileRepository.findAll()).thenReturn(List.of(user));
        when(expenseRepository.findAllByDateBetween(any(), any())).thenReturn(List.of(expense));

        // when
        scheduler.sendDailySummariesToAllUsers();

        // then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getTo()).containsExactly("user@example.com");
        assertThat(sentMessage.getText()).contains("Dzisiejsza suma wydatków: 99.99 PLN");
    }

    @Test
    void shouldNotSendEmailWhenNoExpenses() {
        // given
        ProfileEntity user = new ProfileEntity();
        user.setId(1L);
        user.setEmail("user@example.com");

        when(profileRepository.findAll()).thenReturn(List.of(user));
        when(expenseRepository.findAllByDateBetween(any(), any())).thenReturn(Collections.emptyList());

        // when
        scheduler.sendDailySummariesToAllUsers();

        // then
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}
