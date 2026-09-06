package infrastructure.adapter;

import domain.model.SparePart;
import domain.repository.SparePartRepository;
import infrastructure.repository.JpaSparePartRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public class SparePartRepositoryAdapter implements SparePartRepository {

    private final JpaSparePartRepository repository;

    public SparePartRepositoryAdapter(JpaSparePartRepository repository) {
        this.repository = repository;
    }

    public SparePart save(SparePart entity) {
        return repository.save(entity);
    }

    public Optional<SparePart> findById(UUID id) {
        return repository.findById(id);
    }

    public List<SparePart> findAll() {
        return repository.findAll();
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public List<SparePart> findByCompatibleCarModel(String carModel) {
        return repository.findByCompatibleCarModelsContains(carModel);
    }
}
