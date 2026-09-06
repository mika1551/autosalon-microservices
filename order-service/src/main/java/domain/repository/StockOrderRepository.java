package domain.repository;

import domain.model.StockOrder;

import java.util.List;
import java.util.UUID;

public interface StockOrderRepository extends  BaseRepository<StockOrder> {
    List<StockOrder> findByClientId(UUID clientId);
    List<StockOrder> findByManagerId(UUID managerId);
    List<StockOrder> findByCarId(UUID carId);

}
