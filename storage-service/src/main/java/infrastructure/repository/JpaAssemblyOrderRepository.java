package infrastructure.repository;

import domain.model.AssemblyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAssemblyOrderRepository extends JpaRepository<AssemblyOrder, UUID> {
    Optional<AssemblyOrder> findBySourceOrderId(UUID sourceOrderId);

    Optional<AssemblyOrder> findBySourceOrderIdAndSourceOrderType(UUID sourceOrderId, String sourceOrderType);
}
