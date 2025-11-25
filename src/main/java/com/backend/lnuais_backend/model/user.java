package com.backend.lnuais_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String name;
    String password;
    String email;
    String program;

    private String verificationCode;
    private boolean enabled;

    public enum Experience {
        LOW,
        MID,
        HIGH
    }

    @Enumerated(EnumType.STRING)
    Experience level;

    @ManyToMany
    @JoinTable(
        name = "user_events",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> registeredEvents = new HashSet<>();

    public User(String name, String password, String email, String program, Experience level) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.program = program;
        this.level = level;
        this.enabled = false;
    }
}
