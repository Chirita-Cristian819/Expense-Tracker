package com.cristian.expensetracker.paymentmethods;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public List<PaymentMethodDTO> getPaymentMethods() {
        return paymentMethodRepository.findAllByIsActiveTrueOrderByPaymentMethodNameAsc()
                .stream()
                .map(pm -> new PaymentMethodDTO(
                        pm.getPaymentMethodId(),
                        pm.getPaymentMethodName()
                ))
                .toList();
    }
}
