package domain.repository;

import domain.model.TestDriveRequest;

import java.util.List;
import java.util.UUID;

public interface TestDriveRequestRepository  extends  BaseRepository<TestDriveRequest> {
    List<TestDriveRequest> findByClientId(UUID clientId);
    List<TestDriveRequest> findByCarId(UUID carId);
}
