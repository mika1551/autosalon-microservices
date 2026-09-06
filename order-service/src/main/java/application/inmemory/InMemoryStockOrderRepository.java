package application.inmemory;

import domain.model.StockOrder;
import domain.repository.StockOrderRepository;

import java.util.List;
import java.util.UUID;

public class InMemoryStockOrderRepository extends AbstractInMemoryRepository<StockOrder> implements StockOrderRepository {
    @Override
    public List<StockOrder> findByClientId(UUID clientId) {
        return storage.values().stream()
                .filter(u -> u.getClientId().equals(clientId))
                .toList();
    }
    @Override
    public List<StockOrder> findByManagerId(UUID managerId) {
        return storage.values().stream()
                .filter(u -> u.getManagerId().equals(managerId))
                .toList();
    }
    @Override
    public List<StockOrder> findByCarId(UUID carId) {
        return storage.values().stream()
                .filter(o -> o.getCarId() != null && carId.equals(o.getCarId()))
                .toList();
    }
}
