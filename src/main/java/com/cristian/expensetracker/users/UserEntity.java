package com.cristian.expensetracker.users;
import com.cristian.expensetracker.currencies.CurrencyEntity;
import com.cristian.expensetracker.expenses.ExpenseEntity;
import com.cristian.expensetracker.incomes.IncomeEntity;
import com.cristian.expensetracker.vendors.VendorEntity;
import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @OneToMany(mappedBy = "user")
    private List<IncomeEntity> incomes;

    @OneToMany(mappedBy = "user")
    private List<VendorEntity> vendors;

    @OneToMany(mappedBy = "user")
    private List<ExpenseEntity> expenses;

    @Column(name = "display_name", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String displayName;

    @Column(name = "email", nullable = false, length = 320)
    @NotBlank
    @Size(max = 320)
    @Email
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "preferred_currency_code", nullable = false)
    private CurrencyEntity preferredCurrency;

    @Column(name = "timezone", nullable = false, length = 64)
    @NotBlank
    @Size(max = 64)
    private String timezone = "UTC";

    @Column(name = "email_verified_at")
    private OffsetDateTime emailVerifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;


}
