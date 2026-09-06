package infrastructure.web.dto.request;

import domain.enums.StockOrderStatus;

public record StockOrderStatusRequest(StockOrderStatus status) { }
