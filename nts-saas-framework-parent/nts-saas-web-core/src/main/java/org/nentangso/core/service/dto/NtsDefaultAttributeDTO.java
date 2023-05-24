package org.nentangso.core.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Represents a generic custom attribute.
 */
@Schema(description = "Represents a generic custom attribute.")
public class NtsDefaultAttributeDTO implements NtsAttributeDTO, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Key or name of the attribute.
     */
    @NotNull
    @Schema(description = "Key or name of the attribute.")
    private String key;

    /**
     * Value of the attribute.
     */
    @Schema(description = "Value of the attribute.")
    private String value;

    public NtsDefaultAttributeDTO() {
    }

    public NtsDefaultAttributeDTO(NtsAttributeDTOBuilder builder) {
        this.key = builder.getKey();
        this.value = builder.getValue();
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
