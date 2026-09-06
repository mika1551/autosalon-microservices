package domain.model;

import domain.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")

public class User extends BaseEntity {

    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User(UUID id, String username, Role role) {
        setId(id);
        this.username = username;
        this.role = role;
    }
}