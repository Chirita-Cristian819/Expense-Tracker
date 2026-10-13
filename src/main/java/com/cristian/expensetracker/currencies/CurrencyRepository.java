package com.cristian.expensetracker.currencies;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String> {

    List<CurrencyEntity> findAllByOrderByCurrencyNameAsc();
}
