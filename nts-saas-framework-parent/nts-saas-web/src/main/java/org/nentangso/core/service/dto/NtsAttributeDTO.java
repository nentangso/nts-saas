package org.nentangso.core.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.nentangso.core.service.utils.NtsValidationUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Represents a generic custom attribute.
 */
@Schema(description = "Represents a generic custom attribute.")
public class NtsAttributeDTO implements Serializable {
    /**
     * Key or name of the attribute.
     */
    @NotNull
    @Schema(description = "Key or name of the attribute.\n")
    private final String key;

    /**
     * Value of the attribute.
     */
    @Schema(description = "Value of the attribute.")
    private final String value;

    private NtsAttributeDTO(Builder builder) {
        this.key = builder.key;
        this.value = builder.value;

        if (!builder.skipValidation) {
            validateObject(null);
        }
    }

    public void validateObject(String prefix) {
        NtsValidationUtils.validateObject(this, prefix);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(NtsAttributeDTO input) {
        return new Builder(input);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static final class Builder {
        private String key;
        private String value;
        private boolean skipValidation;

        private Builder() {
        }

        public Builder(NtsAttributeDTO input) {
            this.key = input.getKey();
            this.value = input.getValue();
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

        public Builder skipValidation(boolean skipValidation) {
            this.skipValidation = skipValidation;
            return this;
        }

        public NtsAttributeDTO build() {
            return new NtsAttributeDTO(this);
        }
    }
}
