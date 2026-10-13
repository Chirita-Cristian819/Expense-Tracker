package com.cristian.expensetracker.expensecategories;

import com.cristian.expensetracker.expensetypes.ExpenseTypeDTO;
import com.cristian.expensetracker.expensetypes.ExpenseTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Expense categories", description = "Reference data: categories and their types")
@RestController
@RequestMapping("/api/v1/expense-categories")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;
    private final ExpenseTypeService expenseTypeService;

    public ExpenseCategoryController(ExpenseCategoryService expenseCategoryService,
                                     ExpenseTypeService expenseTypeService) {
        this.expenseCategoryService = expenseCategoryService;
        this.expenseTypeService = expenseTypeService;
    }

    @Operation(summary = "List active categories", description = "Sorted by name.")
    @GetMapping
    public ResponseEntity<List<ExpenseCategoryDTO>> getAllActiveExpenseCategories() {
        return ResponseEntity.ok(expenseCategoryService.getAllActiveExpenseCategories());
    }

    @Operation(summary = "List active types of a category", description = "Sorted by name.")
    @ApiResponse(responseCode = "200", description = "Active types of the category")
    @ApiResponse(responseCode = "404", description = "Category does not exist or is inactive")
    @GetMapping("/{expenseCategoryId}/expense-types")
    public ResponseEntity<List<ExpenseTypeDTO>>
                    getExpenseTypes(@PathVariable Long expenseCategoryId) {
        return ResponseEntity.ok(expenseTypeService.getExpenseTypes(expenseCategoryId));
    }

}
