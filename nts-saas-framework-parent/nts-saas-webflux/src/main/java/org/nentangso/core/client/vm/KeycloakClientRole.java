package org.nentangso.core.client.vm;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class KeycloakClientRole implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String description;

    private Map<String, List<String>> attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KeycloakClientRole{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", attributes=" + attributes +
            '}';
    }
}
