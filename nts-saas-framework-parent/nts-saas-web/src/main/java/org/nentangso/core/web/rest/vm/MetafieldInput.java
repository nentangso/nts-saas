package org.nentangso.core.web.rest.vm;

import javax.validation.constraints.Size;
import java.io.Serializable;

public class MetafieldInput implements Serializable {
    /**
     * A container for a set of metafields. You need to define a custom namespace for your metafields to distinguish them from the metafields used by other apps. Minimum length: 2 characters. Maximum length: 20 characters.
     */
    @Size(min = 2, max = 20)
    private String namespace;

    /**
     * The name of the metafield. Minimum length: 3 characters. Maximum length: 30 characters.
     */
    @Size(min = 3, max = 30)
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
    @Size(max = 65535)
    private String value;

    /**
     * The metafield's information type.
     * <p>
     * See the list of [supported types](https://shop.dev/apps/metafields/definitions/types).
     */
    @Size(max = 50)
    private String type;

    /**
     * A description of the information that the metafield contains.
     */
    @Size(max = 255)
    private String description;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetafieldInput{" +
            "namespace='" + namespace + '\'' +
            ", key='" + key + '\'' +
            ", value='" + value + '\'' +
            ", type='" + type + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
