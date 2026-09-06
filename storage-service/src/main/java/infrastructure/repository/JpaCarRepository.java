package infrastructure.repository;

import domain.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCarRepository extends JpaRepository<Car, UUID> {

    List<Car> findByAvailableForSaleTrue();
    List<Car> findByAvailableForTestDriveTrue();
}
