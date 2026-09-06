package infrastructure.adapter;

import domain.model.AssemblyOrder;
import domain.repository.AssemblyOrderRepository;
import infrastructure.repository.JpaAssemblyOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AssemblyOrderRepositoryAdapter implements AssemblyOrderRepository {

    private final JpaAssemblyOrderRepository repository;

    public AssemblyOrderRepositoryAdapter(JpaAssemblyOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssemblyOrder save(AssemblyOrder entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<AssemblyOrder> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<AssemblyOrder> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<AssemblyOrder> findBySourceOrderId(UUID sourceOrderId) {
        return repository.findBySourceOrderId(sourceOrderId);
    }

    @Override
    public Optional<AssemblyOrder> findBySourceOrderIdAndSourceOrderType(UUID sourceOrderId, String sourceOrderType) {
        return repository.findBySourceOrderIdAndSourceOrderType(sourceOrderId, sourceOrderType);
    }
}
