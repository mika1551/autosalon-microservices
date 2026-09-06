package infrastructure.repository;

import domain.model.StockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaStockOrderRepository extends JpaRepository<StockOrder, UUID> {

    List<StockOrder> findByClientId(UUID clientId);
    List<StockOrder> findByManagerId(UUID managerId);
    List<StockOrder> findByCarId(UUID carId);

}
