package application.service;

import domain.exception.EntityNotFoundException;
import domain.model.SparePart;
import domain.repository.SparePartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public class SparePartService {
    private final SparePartRepository sparePartRepository;

    public SparePartService(SparePartRepository sparePartRepository) {
        this.sparePartRepository = sparePartRepository;
    }

    public SparePart add(SparePart sparePart) {
        return sparePartRepository.save(sparePart);
    }

    public SparePart getById(UUID id) {
        return sparePartRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("SparePart not found: " + id));
    }

    public List<SparePart> getAll() {
        return sparePartRepository.findAll();
    }

    public void deleteById(UUID id) {
        sparePartRepository.deleteById(id);
    }

    public List<SparePart> getByCompatibleCarModel(String carModel) {
        return sparePartRepository.findByCompatibleCarModel(carModel);
    }
}
