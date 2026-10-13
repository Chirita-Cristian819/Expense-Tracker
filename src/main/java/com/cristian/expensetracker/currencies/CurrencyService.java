package com.cristian.expensetracker.currencies;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<CurrencyDTO> getCurrencies() {
        List<CurrencyEntity> currencies = currencyRepository.findAllByOrderByCurrencyNameAsc();
        return currencies.stream()
                         .map(c -> new CurrencyDTO(
                                                           c.getCurrencyCode(),
                                                           c.getCurrencyName(),
                                                           c.getCurrencySymbol()
                                                   ))
                         .toList();
    }



}
