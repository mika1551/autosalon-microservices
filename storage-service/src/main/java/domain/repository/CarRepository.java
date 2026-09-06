package domain.repository;

import domain.model.Car;

import java.util.List;

public interface CarRepository extends BaseRepository<Car>{
    List<Car> findAvailableForSale();
    List<Car> findAvailableForTestDrive();

}
