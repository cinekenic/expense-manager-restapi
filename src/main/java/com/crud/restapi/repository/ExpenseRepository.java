package com.crud.restapi.repository;

import com.crud.restapi.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for Expense resource
 * */

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    /**
     * It will find the single expenses from database
     * @param expenseId
     * @return Optional
     * */
    Optional<ExpenseEntity> findByExpenseId(String expenseId);

    List<ExpenseEntity> findByOwnerId(Long id);

    Optional<ExpenseEntity> findByOwnerIdAndExpenseId(Long id, String expenseId);
}
