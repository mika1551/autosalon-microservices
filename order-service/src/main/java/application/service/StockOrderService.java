package application.service;

import domain.enums.StockOrderStatus;
import domain.exception.DomainValidationException;
import domain.exception.EntityNotFoundException;
import domain.model.Car;
import domain.model.StockOrder;
import domain.repository.CarRepository;
import domain.repository.StockOrderRepository;
import domain.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StockOrderService {
    private final StockOrderRepository stockOrderRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public StockOrderService(StockOrderRepository stockOrderRepository, UserRepository userRepository, CarRepository carRepository) {
        this.stockOrderRepository = stockOrderRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public StockOrder createOrder(UUID carId, UUID clientId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));
        if (!car.isAvailableForSale()) {
            throw new DomainValidationException("Car is not available for sale");
        }
        UUID managerId = userRepository.findAnyManager()
                .orElseThrow(() -> new DomainValidationException("No manager available"))
                .getId();
        StockOrder stockOrder = new StockOrder(
                UUID.randomUUID(),
                clientId,
                carId,
                managerId,
                StockOrderStatus.CREATED
        );
        return stockOrderRepository.save(stockOrder);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or @security.isStockOrderOwner(#id)")
    public StockOrder getById(UUID id) {
        return stockOrderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Stock order not found: " + id));
    }

    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    public List<StockOrder> getVisibleOrders(UUID currentUserId) {
        if (hasRole("ADMIN") || hasRole("MANAGER")) {
            return stockOrderRepository.findAll();
        }
        return stockOrderRepository.findByClientId(currentUserId);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<StockOrder> getByClientId(UUID clientId){
        return stockOrderRepository.findByClientId(clientId);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<StockOrder> getManagerId(UUID managerId){
        return stockOrderRepository.findByManagerId(managerId);
    }

    @PreAuthorize("hasAnyRole('MANAGER','WAREHOUSE_ADMIN','ADMIN')")
    public List<StockOrder> getByCarModel(UUID carModelId){
        return stockOrderRepository.findByCarId(carModelId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(UUID id) {
        stockOrderRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('MANAGER','WAREHOUSE_ADMIN','ADMIN')")
    public StockOrder updateStatus(UUID orderId, StockOrderStatus status) {
        StockOrder order = getById(orderId);
        order.setStatus(status);
        return stockOrderRepository.save(order);
    }

    @PreAuthorize("hasRole('ADMIN') or @security.isStockOrderOwner(#orderId)")
    public StockOrder cancelOrder(UUID orderId) {
        StockOrder order = getById(orderId);
        order.setStatus(StockOrderStatus.CANCELED);
        return stockOrderRepository.save(order);
    }

    private boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        String authority = "ROLE_" + roleName;
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
    }
}
