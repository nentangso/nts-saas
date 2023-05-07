package org.nentangso.core.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NtsJsonUtils implements InitializingBean {
    private final ObjectMapper objectMapper;

    public NtsJsonUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static JsonNode getJsonNode(String rawString, String rootName) throws IOException {
        JsonNode tree = INSTANCE.objectMapper.readTree(rawString);
        if (StringUtils.isNotEmpty(rootName) && tree.has(rootName)) {
            return tree.path(rootName);
        } else {
            return tree;
        }
    }

    public static boolean existField(JsonNode node, String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return false;
        }
        boolean found;
        if (fieldName.contains(".")) {
            String[] fields = fieldName.split("\\.");
            JsonNode tempNode = node.path(fields[0]);
            found = !tempNode.isMissingNode();
            if (found && fields.length > 1) {
                int index = 1;
                while (found && index < fields.length) {
                    tempNode = tempNode.path(fields[index]);
                    found = !tempNode.isMissingNode();
                    index++;
                }
            }
        } else {
            found = !node.path(fieldName).isMissingNode();
        }
        return found;
    }

    public static NtsJsonUtils INSTANCE;

    @Override
    public void afterPropertiesSet() {
        INSTANCE = this;
    }
}
