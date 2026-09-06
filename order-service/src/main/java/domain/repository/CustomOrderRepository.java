package domain.repository;

import domain.model.CustomOrder;

import java.util.List;
import java.util.UUID;

public interface CustomOrderRepository extends BaseRepository <CustomOrder>{
    List<CustomOrder> findByClientId(UUID clientId);
    List<CustomOrder> findByManagerId(UUID managerId);
}
