package infrastructure.adapter;

import domain.model.Car;
import domain.repository.CarRepository;
import infrastructure.repository.JpaCarRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CarRepositoryAdapter implements CarRepository {

    private final JpaCarRepository repository;

    public CarRepositoryAdapter(JpaCarRepository repository) {
        this.repository = repository;
    }

    @Override
    public Car save(Car entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Car> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Car> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<Car> findAvailableForSale() {
        return repository.findByAvailableForSaleTrue();
    }

    @Override
    public List<Car> findAvailableForTestDrive() {
        return repository.findByAvailableForTestDriveTrue();
    }
}
