package application.inmemory;

import domain.model.TestDriveRequest;
import domain.repository.TestDriveRequestRepository;

import java.util.List;
import java.util.UUID;

public class InMemoryTestDriveRequestRepository extends AbstractInMemoryRepository<TestDriveRequest> implements TestDriveRequestRepository {
    @Override
    public List<TestDriveRequest> findByClientId(UUID clientId) {
        return storage.values().stream()
                .filter(o -> o.getClientId().equals(clientId))
                .toList();
    }
    @Override
    public List<TestDriveRequest> findByCarId(UUID carId) {
        return storage.values().stream()
                .filter(o -> o.getCarId().equals(carId))
                .toList();
    }
}
