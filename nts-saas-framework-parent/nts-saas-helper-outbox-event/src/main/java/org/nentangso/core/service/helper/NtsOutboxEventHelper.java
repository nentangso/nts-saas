package org.nentangso.core.service.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nentangso.core.config.NtsConstants;
import org.nentangso.core.domain.NtsOutboxEventEntity;
import org.nentangso.core.repository.NtsOutboxEventRepository;
import org.nentangso.core.security.NtsSecurityUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@ConditionalOnProperty(
    prefix = "nts.helper.outbox-event",
    name = "enabled",
    havingValue = "true"
)
@Service
public class NtsOutboxEventHelper {
    private final NtsOutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public NtsOutboxEventHelper(NtsOutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void batchQueue(String aggregateType, String eventType, List<?> payloads) throws IOException {
        if (payloads == null || payloads.isEmpty()) return;
        for (Object payload : payloads) {
            queue(aggregateType, UUID.randomUUID().toString(), eventType, payload, NtsConstants.DEFAULT_VERSION);
        }
    }

    @Transactional
    public void queue(String aggregateType, String eventType, Object payload) throws IOException {
        queue(aggregateType, UUID.randomUUID().toString(), eventType, payload, NtsConstants.DEFAULT_VERSION);
    }

    @Transactional
    public void queue(String aggregateType, String aggregateId, String eventType, Object payload, int aggregateVersion) throws IOException {
        String actor = NtsSecurityUtils.getCurrentUserLogin().orElse(NtsConstants.SYSTEM);
        queue(aggregateType, aggregateId, eventType, payload, actor, aggregateVersion, NtsConstants.DEFAULT_BUSINESS_VERSION);
    }

    @Transactional
    public void queue(String aggregateType, String aggregateId, String eventType, Object payload, String actor, int aggregateVersion, int businessVersion) throws IOException {
        NtsOutboxEventEntity outboxEvent = new NtsOutboxEventEntity();
        String payloadJson = payload instanceof String ? (String) payload : objectMapper.writeValueAsString(payload);
        outboxEvent.setAggregateType(aggregateType);
        outboxEvent.setAggregateId(aggregateId);
        outboxEvent.setEventType(eventType);
        outboxEvent.setPayload(payloadJson);
        outboxEvent.setActor(actor);
        outboxEvent.setAggregateVersion(aggregateVersion);
        outboxEvent.setBusinessVersion(businessVersion);
        outboxEventRepository.save(outboxEvent);
    }
}
