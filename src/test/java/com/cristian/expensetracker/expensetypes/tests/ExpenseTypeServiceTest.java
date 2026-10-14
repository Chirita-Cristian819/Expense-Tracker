package com.cristian.expensetracker.expensetypes.tests;

import com.cristian.expensetracker.exceptions.ExpenseCategoryNotFoundException;
import com.cristian.expensetracker.expensecategories.ExpenseCategoryRepository;
import com.cristian.expensetracker.expensetypes.ExpenseTypeRepository;
import com.cristian.expensetracker.expensetypes.ExpenseTypeService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class ExpenseTypeServiceTest {

    @Test
    void rejectsNonexistentCategory() {
        ExpenseTypeRepository typeRepository =
                mock(ExpenseTypeRepository.class);

        ExpenseCategoryRepository categoryRepository =
                mock(ExpenseCategoryRepository.class);

        ExpenseTypeService service =
                new ExpenseTypeService(typeRepository, categoryRepository);

        when(categoryRepository
                .existsByExpenseCategoryIdAndIsActiveTrue(999L))
                .thenReturn(false);

        assertThrows(
                ExpenseCategoryNotFoundException.class,
                () -> service.getExpenseTypes(999L)
        );
    }
}
