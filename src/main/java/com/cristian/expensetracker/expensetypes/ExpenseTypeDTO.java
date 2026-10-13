package com.cristian.expensetracker.expensetypes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Expense Type Response")
public record ExpenseTypeDTO(Long TypeId, String typeName, boolean isCustom) {
}
