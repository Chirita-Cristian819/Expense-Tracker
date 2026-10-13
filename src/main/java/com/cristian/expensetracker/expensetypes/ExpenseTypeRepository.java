package com.cristian.expensetracker.expensetypes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseTypeEntity, Long> {

    @Query("""
            SELECT t FROM ExpenseTypeEntity t
            WHERE t.expenseCategory.expenseCategoryId = :categoryId
              AND t.isActive = true
            ORDER BY t.expenseTypeName ASC
            """)
    List<ExpenseTypeEntity> findActiveByCategoryId(@Param("categoryId") Long categoryId);
}
