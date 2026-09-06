import application.inmemory.InMemoryCarRepository;
import application.inmemory.InMemoryStockOrderRepository;
import application.inmemory.InMemoryUserRepository;
import application.service.StockOrderService;
import domain.enums.Role;
import domain.enums.StockOrderStatus;
import domain.exception.DomainValidationException;
import domain.exception.EntityNotFoundException;
import domain.model.Car;
import domain.model.StockOrder;
import domain.model.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StockOrderServiceTest {
    @Test
    void shouldCreateStockOrderAndAssignManager() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryStockOrderRepository orderRepo = new InMemoryStockOrderRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setBrand("BMW");
        car.setModel("320i");
        car.setBasePrice(new BigDecimal("3500000"));
        car.setAvailableForSale(true);
        carRepo.save(car);

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        StockOrderService service = new StockOrderService(orderRepo, userRepo, carRepo);

        UUID clientId = UUID.randomUUID();
        StockOrder order = service.createOrder(car.getId(), clientId);

        assertNotNull(order.getId());
        assertEquals(StockOrderStatus.CREATED, order.getStatus());
        assertNotNull(order.getManagerId());
        assertEquals(manager.getId(), order.getManagerId());
        assertEquals(clientId, order.getClientId());
        assertEquals(car.getId(), order.getCarId());
    }

    @Test
    void shouldThrowIfCarNotAvailableForSale() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryStockOrderRepository orderRepo = new InMemoryStockOrderRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForSale(false);
        carRepo.save(car);

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        StockOrderService service = new StockOrderService(orderRepo, userRepo, carRepo);

        assertThrows(DomainValidationException.class,
                () -> service.createOrder(car.getId(), UUID.randomUUID()));
    }

    @Test
    void shouldThrowIfCarNotFound() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryStockOrderRepository orderRepo = new InMemoryStockOrderRepository();

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        StockOrderService service = new StockOrderService(orderRepo, userRepo, carRepo);

        assertThrows(EntityNotFoundException.class,
                () -> service.createOrder(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void shouldThrowIfNoManagerAvailable() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryStockOrderRepository orderRepo = new InMemoryStockOrderRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForSale(true);
        carRepo.save(car);

        StockOrderService service = new StockOrderService(orderRepo, userRepo, carRepo);

        assertThrows(DomainValidationException.class,
                () -> service.createOrder(car.getId(), UUID.randomUUID()));
    }

    @Test
    void shouldUpdateOrderStatus() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryStockOrderRepository orderRepo = new InMemoryStockOrderRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForSale(true);
        carRepo.save(car);

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        StockOrderService service = new StockOrderService(orderRepo, userRepo, carRepo);

        StockOrder order = service.createOrder(car.getId(), UUID.randomUUID());
        StockOrder updated = service.updateStatus(order.getId(), StockOrderStatus.PAID);

        assertEquals(StockOrderStatus.PAID, updated.getStatus());
    }

    @Test
    void shouldGetOrderById() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryStockOrderRepository orderRepo = new InMemoryStockOrderRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForSale(true);
        carRepo.save(car);

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        StockOrderService service = new StockOrderService(orderRepo, userRepo, carRepo);

        StockOrder order = service.createOrder(car.getId(), UUID.randomUUID());
        StockOrder found = service.getById(order.getId());

        assertEquals(order.getId(), found.getId());
    }

    @Test
    void shouldFindOrdersByClientId() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryStockOrderRepository orderRepo = new InMemoryStockOrderRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForSale(true);
        carRepo.save(car);

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        StockOrderService service = new StockOrderService(orderRepo, userRepo, carRepo);

        UUID clientId = UUID.randomUUID();
        service.createOrder(car.getId(), clientId);
        service.createOrder(car.getId(), UUID.randomUUID());

        List<StockOrder> orders = service.getByClientId(clientId);

        assertEquals(1, orders.size());
        assertEquals(clientId, orders.getFirst().getClientId());
    }

    @Test
    void shouldFindOrdersByManagerId() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryStockOrderRepository orderRepo = new InMemoryStockOrderRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForSale(true);
        carRepo.save(car);

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        StockOrderService service = new StockOrderService(orderRepo, userRepo, carRepo);

        service.createOrder(car.getId(), UUID.randomUUID());
        service.createOrder(car.getId(), UUID.randomUUID());

        List<StockOrder> orders = service.getManagerId(manager.getId());

        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(o -> o.getManagerId().equals(manager.getId())));
    }

    @Test
    void shouldDeleteOrder() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryStockOrderRepository orderRepo = new InMemoryStockOrderRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForSale(true);
        carRepo.save(car);

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        StockOrderService service = new StockOrderService(orderRepo, userRepo, carRepo);

        StockOrder order = service.createOrder(car.getId(), UUID.randomUUID());
        assertEquals(1, orderRepo.findAll().size());

        service.deleteById(order.getId());
        assertEquals(0, orderRepo.findAll().size());    }
}