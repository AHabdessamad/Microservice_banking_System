package com.Bank.Banking.System.repository;

import com.Bank.Banking.System.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findUserByEmail(String email);
}
