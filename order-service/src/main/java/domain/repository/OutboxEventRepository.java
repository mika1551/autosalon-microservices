package domain.repository;

import domain.enums.OutboxStatus;
import domain.model.OutboxEvent;

import java.util.List;

public interface OutboxEventRepository extends BaseRepository<OutboxEvent> {
    List<OutboxEvent> findByStatus(OutboxStatus status);
}
