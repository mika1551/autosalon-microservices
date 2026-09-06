package infrastructure.repository;

import domain.enums.Role;
import domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<User, UUID> {

    List<User> findByRole(Role role);
    Optional<User> findFirstByRole(Role role);
}
