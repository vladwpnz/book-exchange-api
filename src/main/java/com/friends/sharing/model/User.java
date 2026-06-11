package com.friends.sharing.model;

import com.friends.sharing.configuration.security.Authorities;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;
    private String name;
    private String email;
    private String password;
    private Authorities authority;
    @Column(name = "avatar_url")
    private String avatarUrl;
}
