package infrastructure.adapter;

import domain.model.StockOrder;
import domain.repository.StockOrderRepository;
import infrastructure.repository.JpaStockOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public class StockOrderRepositoryAdapter implements StockOrderRepository {

    private final JpaStockOrderRepository repository;

    public StockOrderRepositoryAdapter(JpaStockOrderRepository repository) {
        this.repository = repository;
    }

    public StockOrder save(StockOrder entity) {
        return repository.save(entity);
    }

    public Optional<StockOrder> findById(UUID id) {
        return repository.findById(id);
    }

    public List<StockOrder> findAll() {
        return repository.findAll();
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public List<StockOrder> findByClientId(UUID clientId) {
        return repository.findByClientId(clientId);
    }

    public List<StockOrder> findByManagerId(UUID managerId) {
        return repository.findByManagerId(managerId);
    }

    public List<StockOrder> findByCarId(UUID carId) {
        return repository.findByCarId(carId);
    }
}

