package application.service;

import domain.enums.AssemblyOrderStatus;
import domain.exception.EntityNotFoundException;
import domain.model.AssemblyOrder;
import domain.repository.AssemblyOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AssemblyOrderService {

    private final AssemblyOrderRepository assemblyOrderRepository;

    public AssemblyOrderService(AssemblyOrderRepository assemblyOrderRepository) {
        this.assemblyOrderRepository = assemblyOrderRepository;
    }

    public AssemblyOrder create(
            UUID sourceOrderId,
            String sourceOrderType,
            String carModel,
            Set<UUID> requiredComponentIds,
            UUID warehouseAdminId
    ) {
        AssemblyOrder order = new AssemblyOrder();
        order.setSourceOrderId(sourceOrderId);
        order.setSourceOrderType(sourceOrderType);
        order.setCarModel(carModel);
        order.setWarehouseAdminId(warehouseAdminId);
        order.setRequiredComponentIds(requiredComponentIds == null ? Set.of() : requiredComponentIds);
        order.setStatus(AssemblyOrderStatus.CREATED);
        return assemblyOrderRepository.save(order);
    }

    public AssemblyOrder create(UUID sourceOrderId) {
        return create(sourceOrderId, null, null, Set.of(), null);
    }

    public AssemblyOrder getById(UUID id) {
        return assemblyOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AssemblyOrder not found: " + id));
    }

    public Optional<AssemblyOrder> findBySourceOrderIdAndSourceOrderType(UUID sourceOrderId, String sourceOrderType) {
        return assemblyOrderRepository.findBySourceOrderIdAndSourceOrderType(sourceOrderId, sourceOrderType);
    }

    public List<AssemblyOrder> getAll() {
        return assemblyOrderRepository.findAll();
    }

    public AssemblyOrder updateStatus(UUID id, AssemblyOrderStatus status) {
        AssemblyOrder order = getById(id);
        order.setStatus(status);
        return assemblyOrderRepository.save(order);
    }

    public AssemblyOrder assignWarehouseAdmin(UUID id, UUID warehouseAdminId) {
        AssemblyOrder order = getById(id);
        order.setWarehouseAdminId(warehouseAdminId);
        return assemblyOrderRepository.save(order);
    }

    public void deleteById(UUID id) {
        assemblyOrderRepository.deleteById(id);
    }
}
