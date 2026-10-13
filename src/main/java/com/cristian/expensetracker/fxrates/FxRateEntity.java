package com.cristian.expensetracker.fxrates;

import com.cristian.expensetracker.currencies.CurrencyEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "fx_rates")
public class FxRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fx_rate_id")
    private Long fxRateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "currency_code", nullable = false)
    private CurrencyEntity currency;

    @Column(name = "rate_date", nullable = false)
    @NotNull
    private LocalDate rateDate;

    @Column(name = "rate", nullable = false, precision = 18, scale = 8)
    @Positive
    @NotNull
    private BigDecimal rate;

    @Column(name = "source", length = 20, nullable = false)
    @Size(max = 20)
    @NotNull
    private String source;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;


}