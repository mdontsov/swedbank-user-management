package com.example.usermanagement.user.persistence;

import com.example.usermanagement.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

