package com.cristian.expensetracker.expensetypes;

import com.cristian.expensetracker.expensecategories.ExpenseCategoryRepository;
import com.cristian.expensetracker.exceptions.ExpenseCategoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseTypeService {

    private final ExpenseTypeRepository expenseTypeRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseTypeService(ExpenseTypeRepository expenseTypeRepository,
                              ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseTypeRepository = expenseTypeRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public List<ExpenseTypeDTO> getExpenseTypes(Long expenseCategoryId) {
        if (!expenseCategoryRepository.existsByExpenseCategoryIdAndIsActiveTrue(expenseCategoryId)) {
            throw new ExpenseCategoryNotFoundException("Expense category %d not found".formatted(expenseCategoryId));
        }

        return expenseTypeRepository
                .findActiveByCategoryId(expenseCategoryId)
                .stream()
                .map(et -> new ExpenseTypeDTO(
                        et.getExpenseTypeId(),
                        et.getExpenseTypeName(),
                        et.isCustom()
                ))
                .toList();
    }
}
