package org.nentangso.core.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Metafields represent custom metadata attached to a resource. Metafields can be sorted into namespaces and are
 * composed of keys, values, and value types.
 */
@ConditionalOnProperty(
    prefix = "nts.helper.metafield",
    name = "enabled",
    havingValue = "true"
)
@Entity
@Table(name = "nts_metafields")
@Where(clause = "deleted = false")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NtsMetafieldEntity extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The unique ID of the metafield.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The type of resource that the metafield is attached to.
     */
    @NotNull
    @Size(max = 20)
    @Column(name = "owner_resource", length = 20, nullable = false)
    private String ownerResource;

    /**
     * The unique ID of the resource that the metafield is attached to.
     */
    @NotNull
    @Min(1L)
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    /**
     * A container for a set of metafields. You need to define a custom namespace for your metafields to distinguish them from the metafields used by other apps. Minimum length: 2 characters. Maximum length: 20 characters.
     */
    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "namespace", length = 20)
    private String namespace;

    /**
     * The name of the metafield. Minimum length: 3 characters. Maximum length: 30 characters.
     */
    @NotNull
    @Size(min = 3, max = 30)
    @Column(name = "nts_key", length = 30)
    private String key;

    /**
     * The information to be stored as metadata. Maximum length: 512 characters when metafield namespace is equal to tags and key is equal to alt.
     * When using type, see this list of validations.
     * <p>
     * When using the deprecated value_type, the maximum length of value varies:
     * If value_type is a string, then maximum length: 5,000,000 characters.
     * If value_type is an integer, then maximum length: 100,000 characters.
     * If value_type is a json_string, then maximum length: 100,000 characters.
     */
    @Lob
    @Size(max = 65535)
    @Column(name = "nts_value", length = 65535)
    private String value;

    /**
     * The metafield's information type.
     * <p>
     * See the list of [supported types](https://shop.dev/apps/metafields/definitions/types).
     */
    @NotNull
    @Column(name = "nts_type", length = 50, nullable = false)
    private String type;

    /**
     * A description of the information that the metafield contains.
     */
    @Column(name = "description")
    private String description;

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

    public String getOwnerResource() {
        return ownerResource;
    }

    public void setOwnerResource(String ownerResource) {
        this.ownerResource = ownerResource;
    }

    public NtsMetafieldEntity ownerResource(String ownerResource) {
        this.setOwnerResource(ownerResource);
        return this;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public NtsMetafieldEntity ownerId(Long ownerId) {
        this.setOwnerId(ownerId);
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public NtsMetafieldEntity namespace(String namespace) {
        this.setNamespace(namespace);
        return this;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public NtsMetafieldEntity key(String key) {
        this.setKey(key);
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public NtsMetafieldEntity value(String value) {
        this.setValue(value);
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NtsMetafieldEntity type(String type) {
        this.setType(type);
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NtsMetafieldEntity description(String description) {
        this.setDescription(description);
        return this;
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
        if (!(o instanceof NtsMetafieldEntity)) {
            return false;
        }
        return id != null && id.equals(((NtsMetafieldEntity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetafieldEntity{" +
            "id=" + id +
            ", ownerResource='" + ownerResource + '\'' +
            ", ownerId=" + ownerId +
            ", namespace='" + namespace + '\'' +
            ", key='" + key + '\'' +
            ", value='" + value + '\'' +
            ", type='" + type + '\'' +
            ", description='" + description + '\'' +
            ", deleted=" + deleted +
            ", createdBy='" + getCreatedBy() + '\'' +
            ", createdAt=" + getCreatedAt() +
            ", updatedBy='" + getUpdatedBy() + '\'' +
            ", updatedAt=" + getUpdatedAt() +
            '}';
    }
}
