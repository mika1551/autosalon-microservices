package domain.repository;

import domain.model.AssemblyOrder;

import java.util.Optional;
import java.util.UUID;

public interface AssemblyOrderRepository extends BaseRepository<AssemblyOrder> {
    Optional<AssemblyOrder> findBySourceOrderId(UUID sourceOrderId);

    Optional<AssemblyOrder> findBySourceOrderIdAndSourceOrderType(UUID sourceOrderId, String sourceOrderType);
}
