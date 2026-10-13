package com.cristian.expensetracker.paymentmethods;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Payment methods", description = "Reference data: active payment methods")
@RestController
@RequestMapping("/api/v1/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @Operation(summary = "List active payment methods", description = "Sorted by name.")
    @GetMapping
    public ResponseEntity<List<PaymentMethodDTO>> getAllPaymentMethods() {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethods());
    }
}
