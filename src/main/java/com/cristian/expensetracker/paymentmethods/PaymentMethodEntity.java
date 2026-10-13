package com.cristian.expensetracker.paymentmethods;
import com.cristian.expensetracker.expenses.ExpenseEntity;
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
@Table(name = "payment_methods")
public class PaymentMethodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_method_id")
    private Long paymentMethodId;

    @OneToMany(mappedBy = "paymentMethod")
    private List<ExpenseEntity> expenses;

    @Column(name = "payment_method_code", nullable = false, unique = true, length = 50)
    @NotBlank
    @Size(max = 50)
    private String paymentMethodCode;

    @Column(name = "payment_method_name", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String paymentMethodName;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
