package domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderApprovedEvent(
        UUID orderId,
        UUID traceId,
        String orderType,
        Instant approvedAt
) {
}
