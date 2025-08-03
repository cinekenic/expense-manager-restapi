package com.crud.restapi.service;

import com.crud.restapi.dto.ExpenseDTO;
import com.crud.restapi.entity.ExpenseEntity;
import com.crud.restapi.entity.ProfileEntity;
import com.crud.restapi.repository.ExpenseRepository;
import com.crud.restapi.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {

    private ExpenseRepository expenseRepository;
    private AuthService authService;
    private ModelMapper modelMapper;
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        authService = mock(AuthService.class);
        modelMapper = new ModelMapper();
        expenseService = new ExpenseServiceImpl(expenseRepository, modelMapper, authService);
    }

    @Test
    void shouldReturnListOfExpenseDTOs() {
        // given
        ProfileEntity user = new ProfileEntity();
        user.setId(1L);

        ExpenseEntity entity = new ExpenseEntity();
        entity.setExpenseId("exp123");
        entity.setName("Lunch");
        entity.setAmount(BigDecimal.valueOf(20.00));
        entity.setDate(Timestamp.valueOf(LocalDateTime.now()));
        entity.setOwner(user);

        when(authService.getLoggedProfile()).thenReturn(user);
        when(expenseRepository.findByOwnerId(1L)).thenReturn(List.of(entity));

        // when
        List<ExpenseDTO> result = expenseService.getAllExpenses();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Lunch");
        assertThat(result.get(0).getAmount()).isEqualTo(BigDecimal.valueOf(20.00));
    }
}
