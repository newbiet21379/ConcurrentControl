package com.tim.concurrentcontrol.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private Set<Product> products;

    @OneToMany(mappedBy = "user")
    private List<BankAccount> accounts;
}
