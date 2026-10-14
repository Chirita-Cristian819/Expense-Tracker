package com.cristian.expensetracker.paymentmethods;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Payment Method Response")
public record PaymentMethodDTO(Long paymentMethodId, String paymentMethodName) {
}
