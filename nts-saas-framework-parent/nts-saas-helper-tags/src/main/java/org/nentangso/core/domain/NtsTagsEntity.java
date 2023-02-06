package org.nentangso.core.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Tags
 */
@ConditionalOnProperty(
    prefix = "nts.helper.tag",
    name = "enabled",
    havingValue = "true"
)
@Entity
@Table(name = "nts_tags")
@Where(clause = "deleted = false")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NtsTagsEntity extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Tags
     */
    @Lob
    @NotNull
    @Size(max = 65535)
    @Column(name = "tags", length = 65535, nullable = false)
    private String tags;

    /**
     * Soft delete
     */
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String note) {
        this.tags = note;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NtsTagsEntity)) {
            return false;
        }
        return id != null && id.equals(((NtsTagsEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagsEntity{" +
            "id=" + id +
            ", tags='" + tags + '\'' +
            ", deleted=" + deleted +
            ", createdBy='" + getCreatedBy() + '\'' +
            ", createdAt=" + getCreatedAt() +
            ", updatedBy='" + getUpdatedBy() + '\'' +
            ", updatedAt=" + getUpdatedAt() +
            '}';
    }

}
