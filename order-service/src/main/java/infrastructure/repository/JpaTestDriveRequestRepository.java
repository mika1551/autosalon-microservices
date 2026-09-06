package infrastructure.repository;

import domain.model.TestDriveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaTestDriveRequestRepository extends JpaRepository<TestDriveRequest, UUID> {

    List<TestDriveRequest> findByClientId(UUID clientId);
    List<TestDriveRequest> findByCarId(UUID carId);
}
