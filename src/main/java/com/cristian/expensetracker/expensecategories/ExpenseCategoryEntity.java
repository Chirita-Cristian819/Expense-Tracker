package com.cristian.expensetracker.expensecategories;
import com.cristian.expensetracker.expensetypes.ExpenseTypeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "expense_categories")
public class ExpenseCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_category_id")
    private Long expenseCategoryId;

    @OneToMany(mappedBy = "expenseCategory")
    private List<ExpenseTypeEntity> expenseTypes;

    @Column(name = "category_code", nullable = false, unique = true, length = 50)
    @NotBlank
    @Size(max = 50)
    private String categoryCode;

    @Column(name = "expense_category_name", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String expenseCategoryName;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
