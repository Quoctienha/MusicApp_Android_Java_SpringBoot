package com.example.MusicApp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Data @NoArgsConstructor @AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String phone;

    // mapping với Account
    @OneToOne(mappedBy = "user")
    private Account account;
}
