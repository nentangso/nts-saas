package org.nentangso.core.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Note
 */
@ConditionalOnProperty(
    prefix = "nts.helper.note",
    name = "enabled",
    havingValue = "true"
)
@Entity
@Table(name = "nts_notes")
@Where(clause = "deleted = false")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NtsNoteEntity extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Note
     */
    @Lob
    @NotNull
    @Size(max = 5000)
    @Column(name = "note", length = 5000, nullable = false)
    private String note;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
        if (!(o instanceof NtsNoteEntity)) {
            return false;
        }
        return id != null && id.equals(((NtsNoteEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NoteEntity{" +
            "id=" + id +
            ", note='" + note + '\'' +
            ", deleted=" + deleted +
            ", createdBy='" + getCreatedBy() + '\'' +
            ", createdAt=" + getCreatedAt() +
            ", updatedBy='" + getUpdatedBy() + '\'' +
            ", updatedAt=" + getUpdatedAt() +
            '}';
    }
}
