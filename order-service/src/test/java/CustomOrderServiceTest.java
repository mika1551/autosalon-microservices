import application.inmemory.InMemoryCarConfigurationRepository;
import application.inmemory.InMemoryCustomOrderRepository;
import application.inmemory.InMemoryUserRepository;
import application.service.CustomOrderService;
import domain.enums.CustomOrderStatus;
import domain.enums.Role;
import domain.exception.DomainValidationException;
import domain.exception.EntityNotFoundException;
import domain.model.CarConfiguration;
import domain.model.CustomOrder;
import domain.model.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CustomOrderServiceTest {

    @Test
    void shouldCreateCustomOrder() {
        InMemoryCustomOrderRepository repo = new InMemoryCustomOrderRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryCarConfigurationRepository configRepo = new InMemoryCarConfigurationRepository();

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("BMW 320i");
        configuration.setBasePrice(new BigDecimal("3000000"));
        configRepo.save(configuration);

        CustomOrderService service = new CustomOrderService(repo, userRepo, configRepo);

        UUID clientId = UUID.randomUUID();
        CustomOrder order = service.createOrder(clientId, "BMW 320i", configuration.getId());

        assertNotNull(order.getId());
        assertEquals(CustomOrderStatus.CREATED, order.getStatus());
        assertEquals(manager.getId(), order.getManagerId());
        assertEquals(clientId, order.getClientId());
        assertEquals("BMW 320i", order.getCarModel());
        assertEquals(configuration.getId(), order.getCarConfiguration().getId());
    }

    @Test
    void shouldThrowWhenConfigurationNull() {
        InMemoryCustomOrderRepository repo = new InMemoryCustomOrderRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryCarConfigurationRepository configRepo = new InMemoryCarConfigurationRepository();

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        CustomOrderService service = new CustomOrderService(repo, userRepo, configRepo);

        assertThrows(EntityNotFoundException.class,
                () -> service.createOrder(UUID.randomUUID(), "BMW 320i", null));
    }

    @Test
    void shouldThrowWhenNoManagerAvailable() {
        InMemoryCustomOrderRepository repo = new InMemoryCustomOrderRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryCarConfigurationRepository configRepo = new InMemoryCarConfigurationRepository();

        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("BMW 320i");
        configuration.setBasePrice(new BigDecimal("3000000"));
        configRepo.save(configuration);

        CustomOrderService service = new CustomOrderService(repo, userRepo, configRepo);

        assertThrows(DomainValidationException.class,
                () -> service.createOrder(UUID.randomUUID(), "BMW 320i", configuration.getId()));
    }

    @Test
    void shouldGetOrderById() {
        InMemoryCustomOrderRepository repo = new InMemoryCustomOrderRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryCarConfigurationRepository configRepo = new InMemoryCarConfigurationRepository();

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("BMW 320i");
        configuration.setBasePrice(new BigDecimal("3000000"));
        configRepo.save(configuration);

        CustomOrderService service = new CustomOrderService(repo, userRepo, configRepo);

        CustomOrder order = service.createOrder(UUID.randomUUID(), "BMW 320i", configuration.getId());
        CustomOrder found = service.getById(order.getId());

        assertEquals(order.getId(), found.getId());
    }

    @Test
    void shouldUpdateOrderStatus() {
        InMemoryCustomOrderRepository repo = new InMemoryCustomOrderRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryCarConfigurationRepository configRepo = new InMemoryCarConfigurationRepository();

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("BMW 320i");
        configuration.setBasePrice(new BigDecimal("3000000"));
        configRepo.save(configuration);

        CustomOrderService service = new CustomOrderService(repo, userRepo, configRepo);

        CustomOrder order = service.createOrder(UUID.randomUUID(), "BMW 320i", configuration.getId());
        CustomOrder updated = service.updateStatus(order.getId(), CustomOrderStatus.PAID);

        assertEquals(CustomOrderStatus.PAID, updated.getStatus());
    }

    @Test
    void shouldGetAllOrdersFromRepo() {
        InMemoryCustomOrderRepository repo = new InMemoryCustomOrderRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryCarConfigurationRepository configRepo = new InMemoryCarConfigurationRepository();

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        CarConfiguration config1 = new CarConfiguration();
        config1.setId(UUID.randomUUID());
        config1.setCarModel("BMW 320i");
        config1.setBasePrice(new BigDecimal("3000000"));
        configRepo.save(config1);

        CarConfiguration config2 = new CarConfiguration();
        config2.setId(UUID.randomUUID());
        config2.setCarModel("Audi A4");
        config2.setBasePrice(new BigDecimal("2800000"));
        configRepo.save(config2);

        CustomOrderService service = new CustomOrderService(repo, userRepo, configRepo);

        service.createOrder(UUID.randomUUID(), "BMW 320i", config1.getId());
        service.createOrder(UUID.randomUUID(), "Audi A4", config2.getId());

        assertEquals(2, repo.findAll().size());
    }

    @Test
    void shouldDeleteOrder() {
        InMemoryCustomOrderRepository repo = new InMemoryCustomOrderRepository();
        InMemoryUserRepository userRepo = new InMemoryUserRepository();
        InMemoryCarConfigurationRepository configRepo = new InMemoryCarConfigurationRepository();

        User manager = new User(UUID.randomUUID(), "manager", Role.MANAGER);
        userRepo.save(manager);

        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("BMW 320i");
        configuration.setBasePrice(new BigDecimal("3000000"));
        configRepo.save(configuration);

        CustomOrderService service = new CustomOrderService(repo, userRepo, configRepo);

        CustomOrder order = service.createOrder(UUID.randomUUID(), "BMW 320i", configuration.getId());
        assertEquals(1, repo.findAll().size());

        service.deleteById(order.getId());
        assertEquals(0, repo.findAll().size());
    }
}
