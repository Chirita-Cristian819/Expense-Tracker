package com.cristian.expensetracker.expensecategories;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseCategoryService(ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public List<ExpenseCategoryDTO> getAllActiveExpenseCategories() {
        return expenseCategoryRepository.findAllByIsActiveTrueOrderByExpenseCategoryNameAsc()
                                        .stream()
                                        .map(ec -> new ExpenseCategoryDTO(
                                            ec.getExpenseCategoryId(),
                                            ec.getExpenseCategoryName()
                                        ))
                                        .toList();
    }
}
