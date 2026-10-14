package com.cristian.expensetracker.currencies;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Currency Response")
public record CurrencyDTO(String currencyCode, String currencyName, String currencySymbol) {
}
