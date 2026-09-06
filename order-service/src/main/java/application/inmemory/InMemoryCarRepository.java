package application.inmemory;

import domain.model.Car;
import domain.repository.CarRepository;

import java.util.List;

public class InMemoryCarRepository extends AbstractInMemoryRepository<Car> implements CarRepository {
    @Override
    public List<Car> findAvailableForSale(){
        return storage.values().stream()
                .filter(Car::isAvailableForSale)
                .toList();
    }
    @Override
    public List<Car> findAvailableForTestDrive(){
        return storage.values().stream()
                .filter(Car::isAvailableForTestDrive)
                .toList();
    }
}
