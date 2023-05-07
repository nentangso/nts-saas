package org.nentangso.core.service.dto;

import org.nentangso.core.service.utils.NtsValidationUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.function.Supplier;

public class NtsMetafieldDTO extends AbstractAuditingDTO implements Serializable {
    /**
     * The unique ID of the metafield.
     */
    private final Long id;

    /**
     * The type of resource that the metafield is attached to.
     */
    @NotBlank
    @Size(max = 20)
    private final String ownerResource;

    /**
     * The unique ID of the resource that the metafield is attached to.
     */
    @NotNull
    @Min(1L)
    private final Long ownerId;

    /**
     * A container for a set of metafields. You need to define a custom namespace for your metafields to distinguish them from the metafields used by other apps. Minimum length: 2 characters. Maximum length: 20 characters.
     */
    @NotNull
    @Size(min = 2, max = 20)
    private final String namespace;

    /**
     * The name of the metafield. Minimum length: 3 characters. Maximum length: 30 characters.
     */
    @NotNull
    @Size(min = 3, max = 30)
    private final String key;

    /**
     * The information to be stored as metadata. Maximum length: 512 characters when metafield namespace is equal to tags and key is equal to alt.
     * When using type, see this list of validations.
     * <p>
     * When using the deprecated value_type, the maximum length of value varies:
     * If value_type is a string, then maximum length: 5,000,000 characters.
     * If value_type is an integer, then maximum length: 100,000 characters.
     * If value_type is a json_string, then maximum length: 100,000 characters.
     */
    @Size(max = 65535)
    private final String value;

    /**
     * The metafield's information type.
     * <p>
     * See the list of [supported types](https://shop.dev/apps/metafields/definitions/types).
     */
    @NotBlank
    @Size(max = 50)
    private final String type;

    /**
     * A description of the information that the metafield contains.
     */
    @Size(max = 255)
    private final String description;

    public NtsMetafieldDTO(Long id, String ownerResource, Long ownerId, String namespace, String key, String value, String type, String description) {
        this.id = id;
        this.ownerResource = ownerResource;
        this.ownerId = ownerId;
        this.namespace = namespace;
        this.key = key;
        this.value = value;
        this.type = type;
        this.description = description;

        validateObject(null);
    }

    public NtsMetafieldDTO(Builder builder) {
        this.id = builder.id;
        this.ownerResource = builder.ownerResource;
        this.ownerId = builder.ownerId;
        this.namespace = builder.namespace;
        this.key = builder.key;
        this.value = builder.value;
        this.type = builder.type;
        this.description = builder.description;
        this.setCreatedBy(builder.createdBy);
        this.setCreatedAt(builder.createdAt);
        this.setUpdatedBy(builder.updatedBy);
        this.setUpdatedAt(builder.updatedAt);

        if (!builder.skipValidation) {
            validateObject(null);
        }
    }

    public void validateObject(String prefix) {
        NtsValidationUtils.validateObject(this, prefix);
    }

    public static Builder newBuilder(NtsMetafieldDTO dto) {
        return new Builder(dto);
    }

    public Long getId() {
        return id;
    }

    public String getOwnerResource() {
        return ownerResource;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetafieldDTO{" +
            "id=" + id +
            ", ownerResource='" + ownerResource + '\'' +
            ", ownerId=" + ownerId +
            ", namespace='" + namespace + '\'' +
            ", key='" + key + '\'' +
            ", value='" + value + '\'' +
            ", type='" + type + '\'' +
            ", description='" + description + '\'' +
            '}';
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String ownerResource;
        private Long ownerId;
        private String namespace;
        private String key;
        private String value;
        private String type;
        private String description;
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private boolean skipValidation;

        public Builder() {
        }

        public Builder(NtsMetafieldDTO dto) {
            this.id = dto.getId();
            this.ownerResource = dto.getOwnerResource();
            this.ownerId = dto.getOwnerId();
            this.namespace = dto.getNamespace();
            this.key = dto.getKey();
            this.value = dto.getValue();
            this.type = dto.getType();
            this.description = dto.getDescription();
            this.createdBy = dto.getCreatedBy();
            this.createdAt = dto.getCreatedAt();
            this.updatedBy = dto.getUpdatedBy();
            this.updatedAt = dto.getCreatedAt();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder ownerResource(String ownerResource) {
            this.ownerResource = ownerResource;
            return this;
        }

        public Builder ownerId(Long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder namespaceIf(boolean condition, Supplier<String> namespaceSupplier) {
            if (condition) {
                return namespace(namespaceSupplier.get());
            }
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder keyIf(boolean condition, Supplier<String> keySupplier) {
            if (condition) {
                return key(keySupplier.get());
            }
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder valueIf(boolean condition, Supplier<String> valueSupplier) {
            if (condition) {
                return value(valueSupplier.get());
            }
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder typeIf(boolean condition, Supplier<String> typeSupplier) {
            if (condition) {
                return type(typeSupplier.get());
            }
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder descriptionIf(boolean condition, Supplier<String> descriptionSupplier) {
            if (condition) {
                return description(descriptionSupplier.get());
            }
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder skipValidation(boolean skipValidation) {
            this.skipValidation = skipValidation;
            return this;
        }

        public NtsMetafieldDTO build() {
            return new NtsMetafieldDTO(this);
        }
    }
}
