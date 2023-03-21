package org.nentangso.core.service.provider;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.client.NtsKeycloakClient;
import org.nentangso.core.client.vm.KeycloakClientRole;
import org.nentangso.core.config.NtsKeycloakLocationProperties;
import org.nentangso.core.service.dto.LocationDTO;
import org.nentangso.core.service.errors.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "provider",
    havingValue = NtsKeycloakLocationProvider.PROVIDER_NAME
)
@Service
public class NtsKeycloakLocationProvider implements NtsLocationProvider {
    private static final Logger log = LoggerFactory.getLogger(NtsKeycloakLocationProvider.class);

    public static final String PROVIDER_NAME = "keycloak";

    private final NtsKeycloakLocationProperties keycloakLocationProperties;

    private final NtsKeycloakClient keycloakClient;

    public NtsKeycloakLocationProvider(NtsKeycloakLocationProperties keycloakLocationProperties, NtsKeycloakClient keycloakClient) {
        this.keycloakLocationProperties = keycloakLocationProperties;
        this.keycloakClient = keycloakClient;
        validateKeycloakProperties();
    }

    private void validateKeycloakProperties() {
        if (StringUtils.isBlank(keycloakLocationProperties.getAdminBaseUrl())) {
            throw new RuntimeException("Keycloak provider requires property nts.helper.location.keycloak.admin-base-url");
        }
        if (StringUtils.isBlank(keycloakLocationProperties.getInternalClientId())) {
            throw new RuntimeException("Keycloak provider requires property nts.helper.location.keycloak.internal-client-id");
        }
    }

    @Override
    public Set<Long> findAllIds() {
        String clientId = keycloakLocationProperties.getInternalClientId();
        ResponseEntity<List<KeycloakClientRole>> response = keycloakClient.findClientRoles(clientId, false);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Cannot fetch client roles {}", response);
            throw new AccessDeniedException("Cannot fetch client roles");
        }
        Set<Long> locationIds = toLocationIds(response.getBody());
        log.debug("Fetch success location_ids {}", locationIds);
        return locationIds;
    }

    public Set<Long> toLocationIds(Collection<KeycloakClientRole> clientRoles) {
        return Optional.ofNullable(clientRoles)
            .orElseGet(Collections::emptyList)
            .stream()
            .map(m -> {
                try {
                    return Long.parseUnsignedLong(m.getName());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<LocationDTO> findById(Long id) {
        String clientId = keycloakLocationProperties.getInternalClientId();
        ResponseEntity<KeycloakClientRole> response = keycloakClient.findClientRole(clientId, String.valueOf(id));
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Cannot fetch client role #{} => {}", id, response);
            throw new NotFoundException(String.format("Cannot fetch client role #%s", id));
        }
        return toLocationDTO(response.getBody());
    }

    private Optional<LocationDTO> toLocationDTO(KeycloakClientRole clientRole) {
        return Optional.ofNullable(clientRole)
            .map(m -> {
                LocationDTO dto = new LocationDTO();
                dto.setId(Long.parseUnsignedLong(m.getName()));
                dto.setName(clientRole.getDescription());
                return dto;
            });
    }

    @Override
    public boolean isGrantedAnyLocations() {
//        SecurityContext securityContext = SecurityContextHolder.getContext();
        // bit số 0 -> all locations
        // user_attribute uni_granted_any_location=true
        return true;
    }
}
