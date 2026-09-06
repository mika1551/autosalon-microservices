package infrastructure.security;

import domain.repository.CustomOrderRepository;
import domain.repository.StockOrderRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("security")

public class SecurityExpression  {

    private  final CurrentUserProvider currentUserProvider;
    private final CustomOrderRepository customOrderRepository;
    private final StockOrderRepository stockOrderRepository;

    public SecurityExpression(CurrentUserProvider currentUserProvider, CustomOrderRepository customOrderRepository, StockOrderRepository stockOrderRepository) {
    this.currentUserProvider = currentUserProvider;
    this.customOrderRepository = customOrderRepository;
    this.stockOrderRepository = stockOrderRepository;
    }

    public boolean isCustomOrderOwner(UUID orderId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        return customOrderRepository.findById(orderId)
                .map(order -> order.getClientId().equals(currentUserId))
                .orElse(false);
    }

    public boolean isStockOrderOwner(UUID orderId) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        return stockOrderRepository.findById(orderId)
                .map(stockOrder -> stockOrder.getClientId().equals(currentUserId))
                .orElse(false);
    }
}


