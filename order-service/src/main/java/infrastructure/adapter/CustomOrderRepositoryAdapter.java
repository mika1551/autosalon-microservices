package infrastructure.adapter;

import domain.model.CustomOrder;
import domain.repository.CustomOrderRepository;
import infrastructure.repository.JpaCustomOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public class CustomOrderRepositoryAdapter implements CustomOrderRepository {

    private final JpaCustomOrderRepository repository;

    public CustomOrderRepositoryAdapter(JpaCustomOrderRepository repository) {
        this.repository = repository;
    }

    public CustomOrder save(CustomOrder entity) {
        return repository.save(entity);
    }

    public Optional<CustomOrder> findById(UUID id) {
        return repository.findById(id);
    }

    public List<CustomOrder> findAll() {
        return repository.findAll();
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public List<CustomOrder> findByClientId(UUID clientId) {
        return repository.findByClientId(clientId);
    }

    public List<CustomOrder> findByManagerId(UUID managerId) {
        return repository.findByManagerId(managerId);
    }
}
