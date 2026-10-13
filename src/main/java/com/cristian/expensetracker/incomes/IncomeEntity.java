package com.cristian.expensetracker.incomes;

import com.cristian.expensetracker.currencies.CurrencyEntity;
import com.cristian.expensetracker.users.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "incomes")
public class IncomeEntity {

    public enum IncomeType { SALARY, BONUS, RENT, PENSION }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_id")
    private Long incomeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "income_date", nullable = false)
    @NotNull
    private LocalDate incomeDate;

    @Column(name = "income_type", nullable = false, length = 50)
    @NotNull
    @Enumerated(EnumType.STRING)
    private IncomeType incomeType;

    @Column(name = "income_amount", nullable = false, precision = 19, scale = 4)
    @Positive
    @NotNull
    private BigDecimal incomeAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "currency_code", nullable = false)
    private CurrencyEntity currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}