package com.crud.restapi.service;

import com.crud.restapi.entity.ExpenseEntity;
import com.crud.restapi.entity.ProfileEntity;
import com.crud.restapi.repository.ExpenseRepository;
import com.crud.restapi.repository.ProfileRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyExpenseSummaryScheduler {

    private final ExpenseRepository expenseRepository;
    private final ProfileRepository profileRepository;
    private final JavaMailSender mailSender;

    @PostConstruct
    public void testMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("piotr.trzcinski01@gmail.com");
        message.setTo("cinekenic@go2.pl");
        message.setSubject("🧪 Test e-maila");
        message.setText("To jest testowy e-mail wysłany z aplikacji.");
        mailSender.send(message);
        log.info("📧 Wysłano testowy e-mail!");
    }

    @Scheduled(cron = "0 0 12 ? * MON")
    public void sendDailySummariesToAllUsers() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // Pobierz wszystkich użytkowników
        List<ProfileEntity> users = profileRepository.findAll();

        for (ProfileEntity user : users) {
            // Pobierz wydatki użytkownika z danego dnia
            List<ExpenseEntity> userExpenses = expenseRepository
                    .findAllByDateBetween(Timestamp.valueOf(startOfDay), Timestamp.valueOf(endOfDay))
                    .stream()
                    .filter(expense -> expense.getOwner().getId().equals(user.getId()))
                    .collect(Collectors.toList());

            if (userExpenses.isEmpty()) {
                continue; // pomiń użytkowników bez wydatków
            }

            BigDecimal total = userExpenses.stream()
                    .map(ExpenseEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // log do konsoli
            log.info("🧾 SUMMARY for {}: {} PLN in {} expenses",
                    user.getEmail(), total, userExpenses.size());

            // Wyślij e-mail
            sendSummaryEmail(user.getEmail(), today, total, userExpenses.size());
        }
    }

    private void sendSummaryEmail(String email, LocalDate date, BigDecimal total, int count) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("🧾 Podsumowanie wydatków za " + date);
        message.setText("Dzisiejsza suma wydatków: " + total + " PLN\n"
                + "Liczba wydatków: " + count);
        log.info("📧 Wysyłanie maila do {} z podsumowaniem: {} PLN / {} wydatków",
                email, total, count);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("❌ Błąd podczas wysyłania maila do {}: {}", email, e.getMessage(), e);
        }
    }
}