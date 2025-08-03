package com.crud.restapi.controller;

import com.crud.restapi.config.ExpenseControllerTestConfig;
import com.crud.restapi.dto.ExpenseDTO;
import com.crud.restapi.io.ExpenseRequest;
import com.crud.restapi.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import(ExpenseControllerTestConfig.class)
class ExpenseControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ExpenseService expenseService;
    @Autowired private ModelMapper modelMapper;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void shouldCreateExpense() throws Exception {
        ExpenseRequest request = ExpenseRequest.builder()
                .name("Bus ticket")
                .note("To city center")
                .category("Transport")
                .amount(BigDecimal.valueOf(50.0))
                .date(new Date())
                .build();

        ExpenseDTO dto = modelMapper.map(request, ExpenseDTO.class);
        dto.setExpenseId(UUID.randomUUID().toString());

        when(expenseService.saveExpenseDetails(any(ExpenseDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnAllExpenses() throws Exception {
        ExpenseDTO dto = ExpenseDTO.builder()
                .expenseId(UUID.randomUUID().toString())
                .name("Lunch")
                .category("Food")
                .amount(BigDecimal.valueOf(20.0))
                .date(new Date())
                .build();

        when(expenseService.getAllExpenses()).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Lunch"));
    }

    @Test
    void shouldReturnExpenseById() throws Exception {
        String expenseId = UUID.randomUUID().toString();
        ExpenseDTO dto = ExpenseDTO.builder()
                .expenseId(expenseId)
                .name("Dinner")
                .category("Food")
                .amount(BigDecimal.valueOf(30.0))
                .date(new Date())
                .build();

        when(expenseService.getExpenseByExpenseId(expenseId)).thenReturn(dto);

        mockMvc.perform(get("/expenses/" + expenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dinner"));
    }

    @Test
    void shouldUpdateExpense() throws Exception {
        String expenseId = UUID.randomUUID().toString();
        ExpenseRequest updateRequest = ExpenseRequest.builder()
                .name("Updated Expense")
                .note("Updated note")
                .category("Utilities")
                .amount(BigDecimal.valueOf(75.0))
                .date(new Date())
                .build();

        ExpenseDTO updatedDto = modelMapper.map(updateRequest, ExpenseDTO.class);
        updatedDto.setExpenseId(expenseId);

        when(expenseService.updateExpenseDetails(any(ExpenseDTO.class), eq(expenseId))).thenReturn(updatedDto);

        mockMvc.perform(put("/expenses/" + expenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Expense"));
    }

    @Test
    void shouldDeleteExpense() throws Exception {
        String expenseId = UUID.randomUUID().toString();

        mockMvc.perform(delete("/expenses/" + expenseId))
                .andExpect(status().isNoContent());
    }
}
