package com.cristian.expensetracker.expensecategories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Long> {
    List<ExpenseCategoryEntity> findAllByIsActiveTrueOrderByExpenseCategoryNameAsc();
    boolean existsByExpenseCategoryIdAndIsActiveTrue(Long expenseCategoryId);
}
