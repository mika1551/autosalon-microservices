import application.inmemory.InMemorySparePartRepository;
import application.service.SparePartService;
import domain.exception.EntityNotFoundException;
import domain.model.SparePart;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SparePartServiceTest {
    @Test
    void shouldFindByCompatibleCarModel() {
        InMemorySparePartRepository repo = new InMemorySparePartRepository();
        SparePartService service = new SparePartService(repo);

        SparePart p1 = new SparePart(
                UUID.randomUUID(), "Black", "Brembo", "Brake",
                new BigDecimal("50000"), Set.of("BMW 320i")
        );
        SparePart p2 = new SparePart(
                UUID.randomUUID(), "Silver", "Bosch", "Filter",
                new BigDecimal("5000"), Set.of("Audi A4")
        );

        repo.save(p1);
        repo.save(p2);

        List<SparePart> result = service.getByCompatibleCarModel("BMW 320i");

        assertEquals(1, result.size());
        assertEquals("Brembo", result.getFirst().getBrand());
    }

    @Test
    void shouldAddSparePart() {
        InMemorySparePartRepository repo = new InMemorySparePartRepository();
        SparePartService service = new SparePartService(repo);

        SparePart part = new SparePart(
                UUID.randomUUID(), "Black", "Brembo", "Brake",
                new BigDecimal("50000"), Set.of("BMW 320i")
        );

        SparePart saved = service.add(part);

        assertNotNull(saved.getId());
        assertEquals(part.getId(), saved.getId());
    }

    @Test
    void shouldGetSparePartById() {
        InMemorySparePartRepository repo = new InMemorySparePartRepository();
        SparePartService service = new SparePartService(repo);

        SparePart part = new SparePart(
                UUID.randomUUID(), "Black", "Brembo", "Brake",
                new BigDecimal("50000"), Set.of("BMW 320i")
        );

        service.add(part);
        SparePart found = service.getById(part.getId());

        assertEquals(part.getId(), found.getId());
        assertEquals("Brembo", found.getBrand());
    }

    @Test
    void shouldThrowWhenSparePartNotFound() {
        InMemorySparePartRepository repo = new InMemorySparePartRepository();
        SparePartService service = new SparePartService(repo);

        assertThrows(EntityNotFoundException.class,
                () -> service.getById(UUID.randomUUID()));
    }

    @Test
    void shouldGetAllSpareParts() {
        InMemorySparePartRepository repo = new InMemorySparePartRepository();
        SparePartService service = new SparePartService(repo);

        SparePart p1 = new SparePart(
                UUID.randomUUID(), "Black", "Brembo", "Brake",
                new BigDecimal("50000"), Set.of("BMW 320i")
        );
        SparePart p2 = new SparePart(
                UUID.randomUUID(), "Silver", "Bosch", "Filter",
                new BigDecimal("5000"), Set.of("Audi A4")
        );

        service.add(p1);
        service.add(p2);

        List<SparePart> all = service.getAll();

        assertEquals(2, all.size());
    }

    @Test
    void shouldDeleteSparePart() {
        InMemorySparePartRepository repo = new InMemorySparePartRepository();
        SparePartService service = new SparePartService(repo);

        SparePart part = new SparePart(
                UUID.randomUUID(), "Black", "Brembo", "Brake",
                new BigDecimal("50000"), Set.of("BMW 320i")
        );

        service.add(part);
        assertEquals(1, service.getAll().size());

        service.deleteById(part.getId());
        assertEquals(0, service.getAll().size());
    }
}