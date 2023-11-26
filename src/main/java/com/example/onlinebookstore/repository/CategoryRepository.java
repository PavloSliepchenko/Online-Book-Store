package com.example.onlinebookstore.repository;

import com.example.onlinebookstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
