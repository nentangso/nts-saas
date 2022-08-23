package org.nentangso.core.repository;

import org.nentangso.core.domain.OutboxEventEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(
    prefix = "nts.helper.outbox-event",
    name = "enabled",
    havingValue = "true"
)
@Repository
public interface OutboxEventRepository extends CrudRepository<OutboxEventEntity, Long> {
}
