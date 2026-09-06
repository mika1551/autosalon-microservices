package domain.repository;

import domain.model.HasId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseRepository <T extends HasId> {
    T save(T entity);
    Optional<T> findById(UUID id);
    List<T> findAll();
    void deleteById(UUID id);
}
