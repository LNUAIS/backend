package com.backend.lnuais_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.lnuais_backend.model.User;

// The interface extends JpaRepository<Entity, IDType>
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    
}
