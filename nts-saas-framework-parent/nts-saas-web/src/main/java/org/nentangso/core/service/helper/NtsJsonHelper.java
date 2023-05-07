package org.nentangso.core.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@SuppressWarnings("unused")
@Service
public class NtsJsonHelper {
    private final ObjectMapper objectMapper;

    public NtsJsonHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode getJsonNode(String rawString, String rootName) throws IOException {
        JsonNode tree = objectMapper.readTree(rawString);
        if (StringUtils.isNotEmpty(rootName) && tree.has(rootName)) {
            return tree.path(rootName);
        } else {
            return tree;
        }
    }

    public boolean existField(JsonNode node, String fieldName) {
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
}
