package domain.model;

import domain.enums.StockOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock_orders")

public class StockOrder extends BaseEntity {

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Column(name = "manager_id", nullable = false)
    private UUID managerId;

    @Enumerated(EnumType.STRING)
    private StockOrderStatus status;

    public StockOrder(UUID orderId, UUID clientId, UUID carId, UUID managerId, StockOrderStatus status) {
        setId(orderId);
        this.clientId = clientId;
        this.carId = carId;
        this.managerId = managerId;
        this.status = status;
    }

    public UUID getOrderId() {
        return getId();
    }

    public void setOrderId(UUID orderId) {
        setId(orderId);
    }

}