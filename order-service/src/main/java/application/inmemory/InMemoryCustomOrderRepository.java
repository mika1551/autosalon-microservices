package application.inmemory;

import domain.model.CustomOrder;
import domain.repository.CustomOrderRepository;

import java.util.List;
import java.util.UUID;

public class InMemoryCustomOrderRepository extends AbstractInMemoryRepository<CustomOrder> implements CustomOrderRepository {
    @Override
    public List<CustomOrder> findByClientId(UUID clientId) {
        return storage.values().stream()
                .filter(customOrder -> customOrder.getClientId().equals(clientId))
                .toList();
    }

    @Override
    public List<CustomOrder> findByManagerId(UUID managerId) {
        return storage.values().stream()
                .filter(customOrder -> customOrder.getManagerId().equals(managerId))
                .toList();
    }
}