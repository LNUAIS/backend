package com.backend.lnuais_backend.model;

import lombok.Data;

//@Entity
@Data
public class User{
    int id;
    String name;
    String email;
    String program;
    
    public enum Experience{
        LOW,
        MID,
        HIGH
    };

    Experience level;

    public User(int id, String name, String email, String program, Experience level){
       this.id = id;
       this.name = name;
       this.email = email;
       this.program = program; 
       this.level = level;
    }   
}