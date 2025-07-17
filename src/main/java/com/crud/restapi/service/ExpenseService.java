package com.crud.restapi.service;

import com.crud.restapi.dto.ExpenseDTO;

import java.util.List;
/**
 * Service interface for Expense module
 * */
public interface ExpenseService {
/**
 * It will fetch the expenses from database
 * @return list
 * */
    List<ExpenseDTO> getAllExpenses();

    /**
     * It will fetch the single expenses details from database
     * @param expenseId
     * @return ExpenseDTO
     * */
    ExpenseDTO getExpenseByExpenseId(String expenseId);

    /**
     * It will delete the  expense from database
     * @param expenseId
     * @return void
     * */

    void deleteExpenseByExpenseId(String expenseId);
}
