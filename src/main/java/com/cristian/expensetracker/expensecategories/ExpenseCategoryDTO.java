package com.cristian.expensetracker.expensecategories;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Expense Category Response")
public record ExpenseCategoryDTO(Long expenseCategoryId, String expenseCategoryName) {
}
