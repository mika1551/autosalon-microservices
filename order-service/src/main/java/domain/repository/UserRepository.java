package domain.repository;

import domain.enums.Role;
import domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseRepository<User>{
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    void deleteById(UUID id);

    List<User> findByRole(Role role);
    Optional<User> findAnyManager();

}
