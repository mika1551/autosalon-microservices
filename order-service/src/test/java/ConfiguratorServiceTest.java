import application.service.ConfiguratorService;
import domain.enums.ComponentType;
import domain.exception.DomainValidationException;
import domain.exception.IncompatibleComponentException;
import domain.model.CarConfiguration;
import domain.model.ComponentOption;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConfiguratorServiceTest {

    @Test
    void calculateTotalPrice() {
        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("BMW 320i");
        configuration.setBasePrice(new BigDecimal("3000000"));

        ComponentOption wheels = new ComponentOption(
                UUID.randomUUID(),
                ComponentType.WHEELS,
                "19 M-Sport",
                new BigDecimal("95000"),
                Set.of("BMW 320i", "BMW 330i")
        );
        ComponentOption interior = new ComponentOption(
                UUID.randomUUID(),
                ComponentType.INTERIOR,
                "Dakota",
                new BigDecimal("110000"),
                Set.of("BMW 320i")
        );

        Map<ComponentType, ComponentOption> options = new EnumMap<>(ComponentType.class);
        options.put(ComponentType.WHEELS, wheels);
        options.put(ComponentType.INTERIOR, interior);
        configuration.setSelectedOptions(options);

        BigDecimal total = configuration.calculateTotalPrice();
        assertEquals(new BigDecimal("3205000"), total);
    }

    @Test
    void shouldThrowWhenIncompatible() {
        ConfiguratorService service = new ConfiguratorService();
        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("BMW 320i");
        configuration.setBasePrice(new BigDecimal("3000000"));

        ComponentOption interior = new ComponentOption(
                UUID.randomUUID(),
                ComponentType.INTERIOR,
                "Performance",
                new BigDecimal("160000"),
                Set.of("BMW 330i")
        );
        Map<ComponentType, ComponentOption> options = new EnumMap<>(ComponentType.class);
        options.put(ComponentType.INTERIOR, interior);
        configuration.setSelectedOptions(options);

        assertThrows(IncompatibleComponentException.class,
                () -> service.validateConfiguration(configuration, Set.of(ComponentType.INTERIOR)));
    }

    @Test
    void shouldThrowWhenMissingRequiredComponent() {
        ConfiguratorService service = new ConfiguratorService();

        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("BMW 320i");
        configuration.setBasePrice(new BigDecimal("3000000"));

        Map<ComponentType, ComponentOption> options = new EnumMap<>(ComponentType.class);
        configuration.setSelectedOptions(options);

        assertThrows(DomainValidationException.class,
                () -> service.validateConfiguration(configuration, Set.of(ComponentType.INTERIOR)));
    }

    @Test
    void shouldThrowWhenConfigurationNull() {
        ConfiguratorService service = new ConfiguratorService();

        assertThrows(DomainValidationException.class,
                () -> service.validateConfiguration(null, Set.of(ComponentType.INTERIOR)));
    }

    @Test
    void shouldThrowWhenCarModelEmpty() {
        ConfiguratorService service = new ConfiguratorService();

        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("");
        configuration.setBasePrice(new BigDecimal("3000000"));

        assertThrows(DomainValidationException.class,
                () -> service.validateConfiguration(configuration, Set.of()));
    }

    @Test
    void shouldValidateCorrectConfiguration() {
        ConfiguratorService service = new ConfiguratorService();

        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("BMW 320i");
        configuration.setBasePrice(new BigDecimal("3000000"));

        ComponentOption wheels = new ComponentOption(
                UUID.randomUUID(),
                ComponentType.WHEELS,
                "19 M-Sport",
                new BigDecimal("95000"),
                Set.of("BMW 320i", "BMW 330i")
        );
        ComponentOption interior = new ComponentOption(
                UUID.randomUUID(),
                ComponentType.INTERIOR,
                "Dakota",
                new BigDecimal("110000"),
                Set.of("BMW 320i")
        );

        Map<ComponentType, ComponentOption> options = new EnumMap<>(ComponentType.class);
        options.put(ComponentType.WHEELS, wheels);
        options.put(ComponentType.INTERIOR, interior);
        configuration.setSelectedOptions(options);

        assertDoesNotThrow(() -> service.validateConfiguration(configuration, Set.of(ComponentType.WHEELS, ComponentType.INTERIOR)));
    }

    @Test
    void shouldThrowWhenNissanIncompatible() {
        ConfiguratorService service = new ConfiguratorService();

        CarConfiguration configuration = new CarConfiguration();
        configuration.setId(UUID.randomUUID());
        configuration.setCarModel("Nissan Juke");
        configuration.setBasePrice(new BigDecimal("2000000"));

        ComponentOption interior = new ComponentOption(
                UUID.randomUUID(),
                ComponentType.INTERIOR,
                "Premium",
                new BigDecimal("100000"),
                Set.of("BMW 320i")
        );

        Map<ComponentType, ComponentOption> options = new EnumMap<>(ComponentType.class);
        options.put(ComponentType.INTERIOR, interior);
        configuration.setSelectedOptions(options);

        assertThrows(IncompatibleComponentException.class,
                () -> service.validateConfiguration(configuration, Set.of(ComponentType.INTERIOR)));
    }
}