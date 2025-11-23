package com.backend.lnuais_backend.model;

import lombok.Data;

//@Entity
@Data
public class User{
    String name;
    String password;
    String email;
    String program;
    
    public enum Experience{
        LOW,
        MID,
        HIGH
    };

    Experience level;

    public User(String name, String password, String email, String program, Experience level){
        this.name = name;
        this.password = password;
        this.email = email;
        this.program = program; 
        this.level = level;
    }   
}