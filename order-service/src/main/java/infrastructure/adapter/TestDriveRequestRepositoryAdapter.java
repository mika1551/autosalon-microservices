package infrastructure.adapter;

import domain.model.TestDriveRequest;
import domain.repository.TestDriveRequestRepository;
import infrastructure.repository.JpaTestDriveRequestRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public class TestDriveRequestRepositoryAdapter implements TestDriveRequestRepository {

    private final JpaTestDriveRequestRepository repository;

    public TestDriveRequestRepositoryAdapter(JpaTestDriveRequestRepository repository) {
        this.repository = repository;
    }

    public TestDriveRequest save(TestDriveRequest entity) {
        return repository.save(entity);
    }

    public Optional<TestDriveRequest> findById(UUID id) {
        return repository.findById(id);
    }

    public List<TestDriveRequest> findAll() {
        return repository.findAll();
    }
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public List<TestDriveRequest> findByClientId(UUID clientId) {
        return repository.findByClientId(clientId);
    }

    public List<TestDriveRequest> findByCarId(UUID carId) {
        return repository.findByCarId(carId);
    }
}
