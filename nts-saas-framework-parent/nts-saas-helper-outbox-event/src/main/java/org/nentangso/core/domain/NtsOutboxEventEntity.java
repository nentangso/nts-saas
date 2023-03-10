package org.nentangso.core.domain;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * Outbox events
 */
@ConditionalOnProperty(
    prefix = "nts.helper.outbox-event",
    name = "enabled",
    havingValue = "true"
)
@Entity
@Table(name = "nts_outbox_events")
public class NtsOutboxEventEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Aggregate type
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    /**
     * Aggregate id
     */
    @NotNull
    @Size(max = 36)
    @Column(name = "aggregate_id", length = 36, nullable = false)
    private String aggregateId;

    /**
     * Event type
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "event_type", nullable = false)
    private String eventType;

    /**
     * Payload
     */
    @Lob
    @NotNull
    @Size(max = 65535)
    @Column(name = "payload", length = 65535, nullable = false)
    private String payload;

    /**
     * Aggregate version
     */
    @Column(name = "aggregate_version", nullable = false)
    private int aggregateVersion = 0;

    /**
     * Aggregate business version
     */
    @Column(name = "business_version", nullable = false)
    private int businessVersion = 1;

    /**
     * Actor
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "actor", nullable = false)
    private String actor;

    /**
     * Created date
     */
    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public int getAggregateVersion() {
        return aggregateVersion;
    }

    public void setAggregateVersion(int aggregateVersion) {
        this.aggregateVersion = aggregateVersion;
    }

    public int getBusinessVersion() {
        return businessVersion;
    }

    public void setBusinessVersion(int businessVersion) {
        this.businessVersion = businessVersion;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdDate) {
        this.createdAt = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NtsOutboxEventEntity)) {
            return false;
        }
        return id != null && id.equals(((NtsOutboxEventEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OutboxEventEntity{" +
            "id=" + id +
            ", aggregateType='" + aggregateType + '\'' +
            ", aggregateId='" + aggregateId + '\'' +
            ", eventType='" + eventType + '\'' +
            ", payload='" + payload + '\'' +
            ", aggregateVersion=" + aggregateVersion +
            ", businessVersion=" + businessVersion +
            ", actor='" + actor + '\'' +
            ", createdDate=" + createdAt +
            '}';
    }

}
