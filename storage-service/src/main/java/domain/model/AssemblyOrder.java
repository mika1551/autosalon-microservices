package domain.model;

import domain.enums.AssemblyOrderStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "assembly_orders")
public class AssemblyOrder extends BaseEntity {

    private UUID sourceOrderId;

    private String sourceOrderType;

    private String carModel;

    private UUID warehouseAdminId;

    @ElementCollection
    @CollectionTable(name = "assembly_order_components", joinColumns = @JoinColumn(name = "assembly_order_id"))
    @Column(name = "component_id")
    private Set<UUID> requiredComponentIds = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private AssemblyOrderStatus status = AssemblyOrderStatus.CREATED;
}
