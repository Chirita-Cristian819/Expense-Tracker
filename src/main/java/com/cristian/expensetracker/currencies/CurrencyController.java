package com.cristian.expensetracker.currencies;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Currencies", description = "Reference data: currencies a user can pick")
@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Operation(summary = "List all currencies", description = "European Central Bank Currencies")
    @GetMapping
    public ResponseEntity<List<CurrencyDTO>> getCurrencies() {
        return ResponseEntity.ok(currencyService.getCurrencies());
    }
}
