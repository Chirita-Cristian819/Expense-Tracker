package com.cristian.expensetracker.paymentmethods;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {

    List<PaymentMethodEntity> findAllByIsActiveTrueOrderByPaymentMethodNameAsc();
}
