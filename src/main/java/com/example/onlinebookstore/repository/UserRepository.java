package com.example.onlinebookstore.repository;

import com.example.onlinebookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
