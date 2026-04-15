package com.quangBE.backend_order_platform.Entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "password_hash")
    private String passwordHash;
    
}
