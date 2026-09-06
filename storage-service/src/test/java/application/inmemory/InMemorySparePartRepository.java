package application.inmemory;

import domain.model.SparePart;
import domain.repository.SparePartRepository;

import java.util.List;

public class InMemorySparePartRepository extends AbstractInMemoryRepository<SparePart> implements SparePartRepository {

    @Override
    public List<SparePart> findByCompatibleCarModel(String carModel) {
        return storage.values().stream()
                .filter(part -> part.getCompatibleCarModels() != null)
                .filter(part -> part.getCompatibleCarModels().contains(carModel))
                .toList();
    }
}
