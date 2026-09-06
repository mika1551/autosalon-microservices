package infrastructure.adapter;

import domain.enums.Role;
import domain.model.User;
import domain.repository.UserRepository;
import infrastructure.repository.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository repository;

    public UserRepositoryAdapter(JpaUserRepository repository) {
        this.repository = repository;
    }

    public User save(User entity) {
        return repository.save(entity);
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public List<User> findByRole(Role role) {
        return repository.findByRole(role);
    }

    public Optional<User> findAnyManager() {
        return repository.findFirstByRole(Role.MANAGER);
    }
}

