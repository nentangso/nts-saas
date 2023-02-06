package org.nentangso.core.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Options
 */
@ConditionalOnProperty(
    prefix = "nts.helper.option",
    name = "enabled",
    havingValue = "true"
)
@Entity
@Table(name = "nts_options")
@Where(clause = "deleted = false")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NtsOptionEntity extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Option key
     */
    @NotBlank
    @Size(max = 50)
    @Column(name = "option_key", length = 50, nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$")
    private String optionKey;

    /**
     * Option value
     */
    @Size(max = 255)
    @Column(name = "option_value")
    private String optionValue;

    /**
     * Soft delete
     */
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public NtsOptionEntity() {
    }

    public NtsOptionEntity(String optionKey, String optionValue) {
        this.optionKey = optionKey;
        this.optionValue = optionValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
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
        if (!(o instanceof NtsOptionEntity)) {
            return false;
        }
        return id != null && id.equals(((NtsOptionEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OptionEntity{" +
            "id=" + id +
            ", optionKey='" + optionKey + '\'' +
            ", optionValue='" + optionValue + '\'' +
            ", deleted=" + deleted +
            ", createdBy='" + getCreatedBy() + '\'' +
            ", createdAt=" + getCreatedAt() +
            ", updatedBy='" + getUpdatedBy() + '\'' +
            ", updatedAt=" + getUpdatedAt() +
            '}';
    }

}
