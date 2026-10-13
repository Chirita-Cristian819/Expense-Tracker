package com.cristian.expensetracker.expensetypes;
import com.cristian.expensetracker.expensecategories.ExpenseCategoryEntity;
import com.cristian.expensetracker.expenses.ExpenseEntity;
import jakarta.persistence.*;
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
@Table(name = "expense_types")
public class ExpenseTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_type_id")
    private Long expenseTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "expense_category_id", nullable = false)
    private ExpenseCategoryEntity expenseCategory;

    @OneToMany(mappedBy = "expenseType")
    private List<ExpenseEntity> expenses;

    @Column(name = "expense_type_name", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String expenseTypeName;

    @Column(name = "is_custom", nullable = false)
    private boolean isCustom = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

}
