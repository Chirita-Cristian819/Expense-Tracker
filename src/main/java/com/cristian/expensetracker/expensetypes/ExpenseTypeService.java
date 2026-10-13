package com.cristian.expensetracker.expensetypes;

import com.cristian.expensetracker.expensecategories.ExpenseCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
         //Unknown or inactive category -> 404
        if (!expenseCategoryRepository.existsByExpenseCategoryIdAndIsActiveTrue(expenseCategoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense category not found");
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
