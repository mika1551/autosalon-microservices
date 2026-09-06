package application.inmemory;

import domain.model.CarConfiguration;
import domain.repository.CarConfigurationRepository;

public class InMemoryCarConfigurationRepository extends AbstractInMemoryRepository<CarConfiguration> implements CarConfigurationRepository
{
}
