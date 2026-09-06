package infrastructure.repository;

import domain.model.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaSparePartRepository extends JpaRepository<SparePart, UUID> {

    List<SparePart> findByCompatibleCarModelsContains(String carModel);
}
