package application.service;

import domain.exception.EntityNotFoundException;
import domain.model.Car;
import domain.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service

public class CarCatalogService {
    private final CarRepository carRepository;
    public CarCatalogService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car getById(UUID id){
        return carRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Car not found: " + id));
    }
    public List<Car> getAll(){
        return carRepository.findAll();
    }
    public void save(Car car){
        carRepository.save(car);
    }
    public void delete(UUID id){
        carRepository.deleteById(id);
    }

    public List<Car> getAvailableCars(UUID id){
        return carRepository.findAvailableForSale();
    }

    public List<Car> filterCars(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String brand,
            String model,
            String bodyType,
            String fuelType,
            Integer minPower,
            Integer maxPower,
            Integer minEngineVolume,
            Integer maxEngineVolume,
            String transmissionType,
            String driveType,
            String color
    ) {
        return carRepository.findAvailableForSale().stream()
                .filter(c -> minPrice == null || c.getBasePrice().compareTo(minPrice) >= 0)
                .filter(c -> maxPrice == null || c.getBasePrice().compareTo(maxPrice) <= 0)
                .filter(c -> brand == null || brand.equalsIgnoreCase(c.getBrand()))
                .filter(c -> model == null || model.equalsIgnoreCase(c.getModel()))
                .filter(c -> bodyType == null || bodyType.equalsIgnoreCase(c.getBodyType()))
                .filter(c -> fuelType == null || fuelType.equalsIgnoreCase(c.getFuelType()))
                .filter(c -> minPower == null || c.getHorsePower() >= minPower)
                .filter(c -> maxPower == null || c.getHorsePower() <= maxPower)
                .filter(c -> minEngineVolume == null || c.getEngineVolumeL() >= minEngineVolume)
                .filter(c -> maxEngineVolume == null || c.getEngineVolumeL() <= maxEngineVolume)
                .filter(c -> transmissionType == null || transmissionType.equalsIgnoreCase(c.getTransmissionType()))
                .filter(c -> driveType == null || driveType.equalsIgnoreCase(c.getDriveType()))
                .filter(c -> color == null || color.equalsIgnoreCase(c.getColor()))
                .toList();
    }

}
