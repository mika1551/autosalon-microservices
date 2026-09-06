package domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderSentForApprovalEvent(

        UUID orderId,
        UUID traceId,
        UUID clientId,
        UUID managerId,
        String orderType,
        String carModel,
        Instant createdAt
) {
}
