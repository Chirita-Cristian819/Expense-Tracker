package com.cristian.expensetracker.currencies;

import com.cristian.expensetracker.expenses.ExpenseEntity;
import com.cristian.expensetracker.users.UserEntity;
import com.cristian.expensetracker.incomes.IncomeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "currencies")
public class CurrencyEntity {

    @Id
    @Column(name = "currency_code", nullable = false, length = 3)
    @JdbcTypeCode(SqlTypes.CHAR)
    @NotNull
    @Pattern(regexp = "[A-Z]{3}")
    private String currencyCode;

    @OneToMany(mappedBy = "preferredCurrency")
    private List<UserEntity> users;

    @OneToMany(mappedBy = "currency")
    private List<IncomeEntity> incomes;

    @OneToMany(mappedBy = "currency")
    private List<ExpenseEntity> expenses;

    @Column(name = "currency_name", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String currencyName;

    @Column(name = "currency_symbol", length = 16)
    @Size(max = 16)
    private String currencySymbol;
}