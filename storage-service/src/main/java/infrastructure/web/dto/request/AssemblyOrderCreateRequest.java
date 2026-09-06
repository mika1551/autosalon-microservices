package infrastructure.web.dto.request;

import java.util.Set;
import java.util.UUID;

public record AssemblyOrderCreateRequest(
        UUID sourceOrderId,
        String sourceOrderType,
        String carModel,
        Set<UUID> requiredComponentIds,
        UUID warehouseAdminId
) {
}
