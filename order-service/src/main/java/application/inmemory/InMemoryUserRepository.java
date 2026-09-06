package application.inmemory;

import domain.enums.Role;
import domain.model.User;
import domain.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class InMemoryUserRepository extends AbstractInMemoryRepository<User> implements UserRepository {
    @Override
    public List<User> findByRole(Role role) {
        return storage.values().stream()
                .filter(u -> u.getRole() == role)
                .toList();
    }
    @Override
    public Optional<User> findAnyManager(){
        return storage.values().stream()
                .filter(u -> u.getRole() == Role.MANAGER)
                .findAny();
    }
}
