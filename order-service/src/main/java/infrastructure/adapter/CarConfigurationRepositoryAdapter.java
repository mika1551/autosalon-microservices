package infrastructure.adapter;

import domain.model.CarConfiguration;
import domain.repository.CarConfigurationRepository;
import infrastructure.repository.JpaCarConfigurationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public class CarConfigurationRepositoryAdapter implements CarConfigurationRepository {
    private final JpaCarConfigurationRepository repository;

    public CarConfigurationRepositoryAdapter(JpaCarConfigurationRepository repository) {
        this.repository = repository;
    }

    @Override
    public CarConfiguration save(CarConfiguration entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<CarConfiguration> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<CarConfiguration> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
