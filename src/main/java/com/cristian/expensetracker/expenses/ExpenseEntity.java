package com.cristian.expensetracker.expenses;

import com.cristian.expensetracker.users.UserEntity;
import com.cristian.expensetracker.expensetypes.ExpenseTypeEntity;
import com.cristian.expensetracker.paymentmethods.PaymentMethodEntity;
import com.cristian.expensetracker.vendors.VendorEntity;
import com.cristian.expensetracker.currencies.CurrencyEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "expenses")
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "idempotency_key")
    private UUID idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "expense_type_id", nullable = false)
    private ExpenseTypeEntity expenseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethodEntity paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private VendorEntity vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "currency_code", nullable = false)
    private CurrencyEntity currency;

    @Column(name = "expense_name", nullable = false, length = 150)
    @NotBlank
    @Size(max = 150)
    private String expenseName;

    @Column(name = "expense_date", nullable = false)
    @NotNull
    private LocalDate expenseDate;

    @Column(name = "expense_amount", nullable = false, precision = 19, scale = 4)
    @Positive
    @NotNull
    private BigDecimal expenseAmount;

    @Column(name = "expense_description", length = 1000)
    @Size(max = 1000)
    @Pattern(regexp = "(?s).*\\S.*")
    private String expenseDescription;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
