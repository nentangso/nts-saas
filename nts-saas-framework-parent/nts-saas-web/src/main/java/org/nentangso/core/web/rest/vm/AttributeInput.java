package org.nentangso.core.web.rest.vm;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Specifies the input fields required for an attribute.
 */
public class AttributeInput implements Serializable {
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttributeInput{" +
            "key='" + key + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
