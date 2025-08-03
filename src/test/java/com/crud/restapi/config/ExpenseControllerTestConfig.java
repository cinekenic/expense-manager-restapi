package com.crud.restapi.config;

import com.crud.restapi.service.DailyExpenseSummaryScheduler;
import com.crud.restapi.service.ExpenseService;
import org.modelmapper.ModelMapper;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ExpenseControllerTestConfig {

    @Bean
    public ExpenseService expenseService() {
        return Mockito.mock(ExpenseService.class);
    }

//    @Bean
//    public ModelMapper modelMapper() {
//        return new ModelMapper(); // można też zmockować
//    }

    @Bean
    public DailyExpenseSummaryScheduler dailyExpenseSummaryScheduler() {
        return Mockito.mock(DailyExpenseSummaryScheduler.class);
    }
}
