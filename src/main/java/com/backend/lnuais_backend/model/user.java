package com.backend.lnuais_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String name;
    String password;
    String email;
    String program;

    private String verificationCode;
    private boolean enabled;
    
    public enum Experience{
        LOW,
        MID,
        HIGH
    };

    @Enumerated(EnumType.STRING)
    Experience level;

    public User(String name, String password, String email, String program, Experience level){
        this.name = name;
        this.password = password;
        this.email = email;
        this.program = program; 
        this.level = level;
        this.enabled = false;
    }   
}