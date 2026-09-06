package domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderRejectedEvent(
        UUID orderId,
        UUID traceId,
        String orderType,
        String reason,
        Instant rejectedAt
) {
}
