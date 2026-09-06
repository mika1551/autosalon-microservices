package application.service;

import domain.enums.Role;
import domain.exception.EntityNotFoundException;
import domain.model.User;
import domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User add(User user) {
        return userRepository.save(user);
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    public List<User> getByRole(Role role) {
        return userRepository.findByRole(role);
    }
}
