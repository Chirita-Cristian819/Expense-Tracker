package com.cristian.expensetracker.currencies;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Currency Response")
public record CurrencyDTO(String code, String name, String symbol) {
}
