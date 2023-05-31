package org.nentangso.core.config;

import org.nentangso.core.service.helper.location.NtsKeycloakLocationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "provider",
    havingValue = NtsKeycloakLocationProvider.PROVIDER_NAME
)
@Configuration
@ConfigurationProperties(prefix = "nts.helper.location.keycloak", ignoreUnknownFields = false)
public class NtsKeycloakLocationProperties implements Serializable {
    private static final long serialVersionUID = 1L;

    private String clientRegistrationId = "nts-helper-location";

    private String adminBaseUrl = "";

    private String internalClientId = "";

    private final List<String> customAttributeKeys = new ArrayList<>();

    public String getClientRegistrationId() {
        return clientRegistrationId;
    }

    public void setClientRegistrationId(String clientRegistrationId) {
        this.clientRegistrationId = clientRegistrationId;
    }

    public String getAdminBaseUrl() {
        return adminBaseUrl;
    }

    public void setAdminBaseUrl(String adminBaseUrl) {
        this.adminBaseUrl = adminBaseUrl;
    }

    public String getInternalClientId() {
        return internalClientId;
    }

    public void setInternalClientId(String internalClientId) {
        this.internalClientId = internalClientId;
    }

    public List<String> getCustomAttributeKeys() {
        return customAttributeKeys;
    }
}
