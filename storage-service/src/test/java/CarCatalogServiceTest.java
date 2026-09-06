import application.inmemory.InMemoryCarRepository;
import application.service.CarCatalogService;
import domain.model.Car;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class CarCatalogServiceTest {
    @Test
    void shouldFilterByBrandAndPrice() {
        InMemoryCarRepository repo = new InMemoryCarRepository();
        CarCatalogService service = new CarCatalogService(repo);

        Car bmw = new Car();
        bmw.setId(UUID.randomUUID());
        bmw.setBrand("BMW");
        bmw.setModel("320i");
        bmw.setBasePrice(new BigDecimal("3500000"));
        bmw.setAvailableForSale(true);

        Car audi = new Car();
        audi.setId(UUID.randomUUID());
        audi.setBrand("Audi");
        audi.setModel("A4");
        audi.setBasePrice(new BigDecimal("3300000"));
        audi.setAvailableForSale(true);

        repo.save(bmw);
        repo.save(audi);

        List<Car> result = service.filterCars(
                new BigDecimal("3400000"),
                new BigDecimal("3600000"),
                "BMW",
                null, null, null,
                null, null,
                null, null,
                null, null, null
        );

        assertEquals(1, result.size());
        assertEquals("BMW", result.getFirst().getBrand());
    }

    @Test
    void shouldFilterByNissanAndColor() {
        InMemoryCarRepository repo = new InMemoryCarRepository();
        CarCatalogService service = new CarCatalogService(repo);

        Car nissan = new Car();
        nissan.setId(UUID.randomUUID());
        nissan.setBrand("Nissan");
        nissan.setModel("Qashqai");
        nissan.setColor("Red");
        nissan.setBasePrice(new BigDecimal("2500000"));
        nissan.setAvailableForSale(true);

        Car toyota = new Car();
        toyota.setId(UUID.randomUUID());
        toyota.setBrand("Toyota");
        toyota.setModel("RAV4");
        toyota.setColor("Red");
        toyota.setBasePrice(new BigDecimal("2600000"));
        toyota.setAvailableForSale(true);

        repo.save(nissan);
        repo.save(toyota);

        List<Car> result = service.filterCars(
                null, null,
                "Nissan",
                null, null, null,
                null, null,
                null, null,
                null, null,
                "Red"
        );

        assertEquals(1, result.size());
        assertEquals("Nissan", result.getFirst().getBrand());
    }
}
