package application.service;

import domain.enums.ComponentType;
import domain.exception.EntityNotFoundException;
import domain.model.CarConfiguration;
import infrastructure.repository.JpaCarConfigurationRepository;
import infrastructure.spec.CarConfigurationSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public class CarConfigurationService {

    private final JpaCarConfigurationRepository repository;

    public CarConfigurationService(JpaCarConfigurationRepository repository) {
        this.repository = repository;
    }

    public CarConfiguration getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CarConfiguration not found: " + id));
    }

    public List<CarConfiguration> findAll(String brand, List<UUID> optionIds, List<ComponentType> types) {
        Specification<CarConfiguration> spec = CarConfigurationSpecification.filter(brand, optionIds, types);
        return repository.findAll(spec);
    }

}
