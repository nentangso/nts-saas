package org.nentangso.core.service.dto;

import java.util.function.Supplier;

public class NtsAttributeDTOBuilder {
    private String key;
    private String value;
    private boolean skipValidation;

    public NtsAttributeDTOBuilder() {
    }

    public NtsAttributeDTOBuilder(NtsAttributeDTO input) {
        this.key = input.getKey();
        this.value = input.getValue();
    }

    public String getKey() {
        return key;
    }

    public NtsAttributeDTOBuilder key(String key) {
        this.key = key;
        return this;
    }

    public NtsAttributeDTOBuilder keyIf(boolean condition, Supplier<String> keySupplier) {
        if (condition) {
            return key(keySupplier.get());
        }
        return this;
    }

    public String getValue() {
        return value;
    }

    public NtsAttributeDTOBuilder value(String value) {
        this.value = value;
        return this;
    }

    public NtsAttributeDTOBuilder valueIf(boolean condition, Supplier<String> valueSupplier) {
        if (condition) {
            return value(valueSupplier.get());
        }
        return this;
    }

    public NtsAttributeDTOBuilder skipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
        return this;
    }

    public boolean isSkipValidation() {
        return skipValidation;
    }

    public NtsDefaultAttributeDTO build() {
        return new NtsDefaultAttributeDTO(this);
    }
}
