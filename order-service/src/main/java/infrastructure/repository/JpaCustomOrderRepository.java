package infrastructure.repository;

import domain.model.CustomOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCustomOrderRepository extends JpaRepository<CustomOrder, UUID> {

    List<CustomOrder> findByClientId(UUID clientId);
    List<CustomOrder> findByManagerId(UUID managerId);
}
