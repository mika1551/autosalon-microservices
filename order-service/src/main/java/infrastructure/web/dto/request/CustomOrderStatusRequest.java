package infrastructure.web.dto.request;

import domain.enums.CustomOrderStatus;

public record CustomOrderStatusRequest(CustomOrderStatus status) {
}
