package domain.model;

import domain.enums.CustomOrderStatus;
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
@Table(name = "custom_orders")

public class CustomOrder extends BaseEntity {

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "manager_id", nullable = false)
    private UUID managerId;

    @Column(name = "car_model", nullable = false)
    private String carModel;

    @OneToOne(optional = false)
    @JoinColumn(name = "configuration_id")
    private CarConfiguration carConfiguration;

    @Enumerated(EnumType.STRING)
    private CustomOrderStatus status;

  public CustomOrder(UUID orderId, UUID clientId, UUID managerId, String carModel, CarConfiguration carConfiguration, CustomOrderStatus status) {
        setId(orderId);
        this.clientId = clientId;
        this.managerId = managerId;
        this.carModel = carModel;
        this.carConfiguration = carConfiguration;
        this.status = status;
    }

    public UUID getOrderId() {
        return getId();
    }

    public void setOrderId(UUID orderId) {
        setId(orderId);
    }


}