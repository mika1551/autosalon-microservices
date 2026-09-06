package infrastructure.web.dto.request;

import domain.enums.AssemblyOrderStatus;

import java.util.UUID;

public record AssemblyOrderUpdateRequest(
        AssemblyOrderStatus status,
        UUID warehouseAdminId
) {
}
