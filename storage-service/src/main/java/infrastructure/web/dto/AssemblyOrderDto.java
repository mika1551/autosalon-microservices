package infrastructure.web.dto;

import domain.enums.AssemblyOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyOrderDto {

    private UUID id;
    private UUID sourceOrderId;
    private String sourceOrderType;
    private String carModel;
    private UUID warehouseAdminId;
    private Set<UUID> requiredComponentIds;
    private Instant createdAt;
    private Instant updatedAt;
    private AssemblyOrderStatus status;
    private boolean removed;
}
