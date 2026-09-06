package application.service;

import domain.exception.DomainValidationException;
import domain.exception.EntityNotFoundException;
import domain.model.Car;
import domain.model.TestDriveRequest;
import domain.repository.CarRepository;
import domain.repository.TestDriveRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Date;

@Service

public class TestDriveService {
    private final TestDriveRequestRepository testDriveRequestRepository;
    private final CarRepository carRepository;

    public TestDriveService(TestDriveRequestRepository testDriveRequestRepository, CarRepository carRepository) {
        this.testDriveRequestRepository = testDriveRequestRepository;
        this.carRepository = carRepository;
    }

    public TestDriveRequest  createTestDriveRequest(UUID clientId, UUID carId, Date date){
        Car car = carRepository.findById(carId).orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));
        if (!car.isAvailableForTestDrive()){
            throw new DomainValidationException("Car is not available for test drive");
        }
        TestDriveRequest request = new TestDriveRequest(
                UUID.randomUUID(),
                clientId,
                carId,
                date
        );
        return testDriveRequestRepository.save(request);
    }

    public TestDriveRequest  getById(UUID id){
        return testDriveRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Test drive request not found: " + id));
    }

    public List<TestDriveRequest> getAll(){
        return testDriveRequestRepository.findAll();
    }

    public List<TestDriveRequest> getByClientId(UUID clientId){
        return testDriveRequestRepository.findByClientId(clientId);
    }

    public List<TestDriveRequest> getByCarId(UUID carId){
        return testDriveRequestRepository.findByCarId(carId);
    }
    public void deleteById(UUID Id){
        testDriveRequestRepository.deleteById(Id);
    }

}
