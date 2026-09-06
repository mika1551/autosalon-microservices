import application.inmemory.InMemoryCarRepository;
import application.inmemory.InMemoryTestDriveRequestRepository;
import application.service.TestDriveService;
import domain.exception.DomainValidationException;
import domain.exception.EntityNotFoundException;
import domain.model.Car;
import domain.model.TestDriveRequest;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestDriveServiceTest {
    @Test
    void shouldCreateTestDriveRequest() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryTestDriveRequestRepository reqRepo = new InMemoryTestDriveRequestRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForTestDrive(true);
        carRepo.save(car);

        TestDriveService service = new TestDriveService(reqRepo, carRepo);

        UUID clientId = UUID.randomUUID();
        Date date = new Date();
        TestDriveRequest request = service.createTestDriveRequest(clientId, car.getId(), date);

        assertNotNull(request.getId());
        assertEquals(car.getId(), request.getCarId());
        assertEquals(clientId, request.getClientId());
        assertEquals(date, request.getDate());
    }

    @Test
    void shouldThrowIfCarNotAvailableForTestDrive() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryTestDriveRequestRepository reqRepo = new InMemoryTestDriveRequestRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForTestDrive(false);
        carRepo.save(car);

        TestDriveService service = new TestDriveService(reqRepo, carRepo);

        assertThrows(DomainValidationException.class,
                () -> service.createTestDriveRequest(UUID.randomUUID(), car.getId(), new Date()));
    }

    @Test
    void shouldThrowIfCarNotFound() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryTestDriveRequestRepository reqRepo = new InMemoryTestDriveRequestRepository();

        TestDriveService service = new TestDriveService(reqRepo, carRepo);

        assertThrows(EntityNotFoundException.class,
                () -> service.createTestDriveRequest(UUID.randomUUID(), UUID.randomUUID(), new Date()));
    }

    @Test
    void shouldGetRequestById() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryTestDriveRequestRepository reqRepo = new InMemoryTestDriveRequestRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForTestDrive(true);
        carRepo.save(car);

        TestDriveService service = new TestDriveService(reqRepo, carRepo);

        TestDriveRequest request = service.createTestDriveRequest(UUID.randomUUID(), car.getId(), new Date());
        TestDriveRequest found = service.getById(request.getId());

        assertEquals(request.getId(), found.getId());
    }

    @Test
    void shouldGetRequestsByClientId() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryTestDriveRequestRepository reqRepo = new InMemoryTestDriveRequestRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForTestDrive(true);
        carRepo.save(car);

        TestDriveService service = new TestDriveService(reqRepo, carRepo);

        UUID clientId = UUID.randomUUID();
        service.createTestDriveRequest(clientId, car.getId(), new Date());
        service.createTestDriveRequest(UUID.randomUUID(), car.getId(), new Date());

        List<TestDriveRequest> requests = service.getByClientId(clientId);

        assertEquals(1, requests.size());
        assertEquals(clientId, requests.getFirst().getClientId());
    }

    @Test
    void shouldGetRequestsByCarId() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryTestDriveRequestRepository reqRepo = new InMemoryTestDriveRequestRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForTestDrive(true);
        carRepo.save(car);

        TestDriveService service = new TestDriveService(reqRepo, carRepo);

        service.createTestDriveRequest(UUID.randomUUID(), car.getId(), new Date());
        service.createTestDriveRequest(UUID.randomUUID(), car.getId(), new Date());

        List<TestDriveRequest> requests = service.getByCarId(car.getId());

        assertEquals(2, requests.size());
        assertTrue(requests.stream().allMatch(r -> r.getCarId().equals(car.getId())));
    }

    @Test
    void shouldDeleteRequest() {
        InMemoryCarRepository carRepo = new InMemoryCarRepository();
        InMemoryTestDriveRequestRepository reqRepo = new InMemoryTestDriveRequestRepository();

        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setAvailableForTestDrive(true);
        carRepo.save(car);

        TestDriveService service = new TestDriveService(reqRepo, carRepo);

        TestDriveRequest request = service.createTestDriveRequest(UUID.randomUUID(), car.getId(), new Date());
        assertEquals(1, service.getAll().size());

        service.deleteById(request.getId());
        assertEquals(0, service.getAll().size());
    }
}