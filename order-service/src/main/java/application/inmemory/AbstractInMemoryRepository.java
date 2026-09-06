package application.inmemory;

import domain.model.HasId;
import domain.repository.BaseRepository;

import java.util.*;
import java.util.List;

public abstract class AbstractInMemoryRepository <T extends HasId> implements BaseRepository<T> {
    protected final Map<UUID, T> storage =  new HashMap<>();

    @Override
    public T save(T entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}
