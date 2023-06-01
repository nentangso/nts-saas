package org.nentangso.core.service.dto;

/**
 * Represents a generic custom attribute.
 */
public interface NtsAttributeDTO {
    /**
     * Key or name of the attribute.
     *
     * @return the key
     */
    default String getKey() {
        return null;
    }

    /**
     * Value of the attribute.
     *
     * @return the value
     */
    default String getValue() {
        return null;
    }

    ;

    static NtsAttributeDTOBuilder newBuilder() {
        return new NtsAttributeDTOBuilder();
    }

    static NtsAttributeDTOBuilder newBuilder(NtsAttributeDTO input) {
        return new NtsAttributeDTOBuilder(input);
    }
}
