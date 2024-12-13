package org.example.doctorai.model.entity;

import org.example.doctorai.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "login", nullable = false)
    public String login;

    @Column(name = "password", nullable = false)
    public String password;

    @Column(name = "email", nullable = false)
    public String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {
        this.role = Role.ROLE_USER;
    }
}
