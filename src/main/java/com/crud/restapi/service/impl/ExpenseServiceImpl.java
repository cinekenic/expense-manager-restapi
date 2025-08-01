package com.crud.restapi.service.impl;

import com.crud.restapi.dto.ExpenseDTO;
import com.crud.restapi.entity.ExpenseEntity;
import com.crud.restapi.entity.ProfileEntity;
import com.crud.restapi.exceptions.ResourceNotFoundException;
import com.crud.restapi.repository.ExpenseRepository;
import com.crud.restapi.service.AuthService;
import com.crud.restapi.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for Expense module
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    /**
     * It will fetch the expenses from database
     * @return list
     * */
    @Override
    public List<ExpenseDTO> getAllExpenses() {
        //Call the repository method
        Long loggedInProfileId = authService.getLoggedProfile().getId();
        List<ExpenseEntity> list = expenseRepository.findByOwnerId(loggedInProfileId);
        log.info("Printing the data from repository: {}", list);

        //convert the Entity object to DTO object
       List<ExpenseDTO> listOfExpenses = list.stream().map(expenseEntity -> mapToExpenseDTO(expenseEntity)).collect(Collectors.toList());
        //Return the list
    return listOfExpenses;
    }

    /**
     * It will fetch the expenses details from database
     * @param expenseId
     * @return ExpenseDTO
     * */

    @Override
    public ExpenseDTO getExpenseByExpenseId(String expenseId) {
        ExpenseEntity expenseEntity = getExpenseEntity(expenseId);
        log.info("Printing the expense entity details: {}", expenseEntity);
       return mapToExpenseDTO(expenseEntity);
    }

    /**
     * It will delete the  expense from database
     * @param expenseId
     * @return void
     * */
    @Override
    public void deleteExpenseByExpenseId(String expenseId) {
       ExpenseEntity expenseEntity =  getExpenseEntity(expenseId);
       log.info("Printing the expense entity: {}", expenseEntity);
       expenseRepository.delete(expenseEntity);
    }

    /**
     * It will save the expense details to database
     * @param expenseDTO the DTO containing expense data
     *  * @return ExpenseDTO the saved expense DTO
     */
    @Override
    public ExpenseDTO saveExpenseDetails(ExpenseDTO expenseDTO) {
        ProfileEntity profileEntity = authService.getLoggedProfile();
        ExpenseEntity newExpenseEntity = mapToExpenseEntity(expenseDTO);
//        newExpenseEntity.setExpenseId(UUID.randomUUID().toString());
        newExpenseEntity.setOwner(profileEntity);
        newExpenseEntity = expenseRepository.save(newExpenseEntity);
        log.info("Printing the new expense entity details {}", newExpenseEntity);
        return mapToExpenseDTO(newExpenseEntity);
    }

    @Override
    public ExpenseDTO updateExpenseDetails(ExpenseDTO expenseDTO, String expenseId) {
       ExpenseEntity existingExpense = getExpenseEntity(expenseId);
        ExpenseEntity updateExpenseEntity = mapToExpenseEntity(expenseDTO);
        updateExpenseEntity.setExpenseId(existingExpense.getExpenseId());
        updateExpenseEntity.setCreatedAt(existingExpense.getCreatedAt());
        updateExpenseEntity.setUpdatedAt(existingExpense.getUpdatedAt());
        updateExpenseEntity.setOwner(authService.getLoggedProfile());
        updateExpenseEntity = expenseRepository.save(updateExpenseEntity);
        log.info("Printing the new expense entity details {}", updateExpenseEntity);
        return mapToExpenseDTO(updateExpenseEntity);
    }

    /**
     * Mapper method to map values from Expense request to expense dto
     * @param expenseDTO the source DTO
     * @return mapped ExpenseEntity
     */
    private ExpenseEntity mapToExpenseEntity(ExpenseDTO expenseDTO) {
        return modelMapper.map(expenseDTO, ExpenseEntity.class);
    }

    /**
     * Mapper method to convert expense entity to expense DTO
     * @param expenseEntity
     * @return ExpenseDTO
     * */
    private ExpenseDTO mapToExpenseDTO(ExpenseEntity expenseEntity) {
        return modelMapper.map(expenseEntity, ExpenseDTO.class);
    }

    /**
     * Fetch the expense by expense id from database
     * @param expenseId
     * @return ExpenseEntity
     * */
    private ExpenseEntity getExpenseEntity(String expenseId) {
        Long id = authService.getLoggedProfile().getId();
        return expenseRepository.findByOwnerIdAndExpenseId(id, expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found for id: " + expenseId));
    }
}
