package com.crud.restapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class DailySummaryDTO {
    private BigDecimal total;
    private int numberOfExpenses;
    private LocalDate date;
//    private List<ExpenseDTO> expenses;
}
