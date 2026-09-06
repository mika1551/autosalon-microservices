package infrastructure.adapter;

import domain.enums.OutboxStatus;
import domain.model.OutboxEvent;
import domain.repository.OutboxEventRepository;
import infrastructure.repository.JpaOutboxEventRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public class OutboxEventRepositoryAdapter implements OutboxEventRepository {

    private final JpaOutboxEventRepository repository;

    public OutboxEventRepositoryAdapter(JpaOutboxEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public OutboxEvent save(OutboxEvent entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<OutboxEvent> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<OutboxEvent> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<OutboxEvent> findByStatus(OutboxStatus status) {
        return repository.findByStatus(status);
    }
}
