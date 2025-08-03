package com.crud.restapi.service.impl;

import com.crud.restapi.dto.ExpenseDTO;
import com.crud.restapi.entity.ExpenseEntity;
import com.crud.restapi.entity.ProfileEntity;
import com.crud.restapi.exceptions.ResourceNotFoundException;
import com.crud.restapi.repository.ExpenseRepository;
import com.crud.restapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseServiceImplTest {

    private ExpenseRepository expenseRepository;
    private ModelMapper modelMapper;
    private AuthService authService;
    private ExpenseServiceImpl expenseService;

    private final ProfileEntity mockProfile = new ProfileEntity();

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        modelMapper = new ModelMapper();
        authService = mock(AuthService.class);
        expenseService = new ExpenseServiceImpl(expenseRepository, modelMapper, authService);

        mockProfile.setId(1L);
        when(authService.getLoggedProfile()).thenReturn(mockProfile);
    }

    @Test
    void shouldReturnListOfExpensesForLoggedUser() {
        ExpenseEntity entity = new ExpenseEntity();
        entity.setAmount(BigDecimal.TEN);
        entity.setCategory("Food");

        when(expenseRepository.findByOwnerId(1L)).thenReturn(List.of(entity));

        List<ExpenseDTO> result = expenseService.getAllExpenses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Food");
    }

    @Test
    void shouldGetExpenseByExpenseId() {
        ExpenseEntity entity = new ExpenseEntity();
        entity.setExpenseId("abc123");
        entity.setAmount(BigDecimal.ONE);

        when(expenseRepository.findByOwnerIdAndExpenseId(1L, "abc123"))
                .thenReturn(Optional.of(entity));

        ExpenseDTO dto = expenseService.getExpenseByExpenseId("abc123");

        assertThat(dto.getAmount()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void shouldThrowIfExpenseNotFound() {
        when(expenseRepository.findByOwnerIdAndExpenseId(1L, "not_found"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.getExpenseByExpenseId("not_found"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Expense not found");
    }

    @Test
    void shouldDeleteExpenseById() {
        ExpenseEntity entity = new ExpenseEntity();
        entity.setExpenseId("toDelete");

        when(expenseRepository.findByOwnerIdAndExpenseId(1L, "toDelete"))
                .thenReturn(Optional.of(entity));

        expenseService.deleteExpenseByExpenseId("toDelete");

        verify(expenseRepository).delete(entity);
    }

    @Test
    void shouldSaveExpense() {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setAmount(BigDecimal.valueOf(99));
        dto.setCategory("Transport");

        ExpenseEntity entity = modelMapper.map(dto, ExpenseEntity.class);
        entity.setOwner(mockProfile);
        entity.setExpenseId("savedId");

        when(expenseRepository.save(any(ExpenseEntity.class))).thenAnswer(inv -> {
            ExpenseEntity saved = inv.getArgument(0);
            saved.setExpenseId("savedId");
            return saved;
        });

        ExpenseDTO saved = expenseService.saveExpenseDetails(dto);

        assertThat(saved.getExpenseId()).isEqualTo("savedId");
        assertThat(saved.getAmount()).isEqualTo(BigDecimal.valueOf(99));
    }

    @Test
    void shouldUpdateExpense() {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setAmount(BigDecimal.TEN);
        dto.setCategory("Updated");

        ExpenseEntity existing = new ExpenseEntity();
        existing.setExpenseId("upd123");
        existing.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        existing.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        when(expenseRepository.findByOwnerIdAndExpenseId(1L, "upd123")).thenReturn(Optional.of(existing));
        when(expenseRepository.save(any(ExpenseEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ExpenseDTO updated = expenseService.updateExpenseDetails(dto, "upd123");

        assertThat(updated.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(updated.getCategory()).isEqualTo("Updated");
    }
}
