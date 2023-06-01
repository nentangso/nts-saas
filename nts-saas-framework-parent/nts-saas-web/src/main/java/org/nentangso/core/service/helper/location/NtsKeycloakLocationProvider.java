package org.nentangso.core.service.helper.location;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.client.NtsKeycloakClient;
import org.nentangso.core.client.vm.KeycloakClientRole;
import org.nentangso.core.config.NtsKeycloakLocationProperties;
import org.nentangso.core.service.dto.NtsAttributeDTO;
import org.nentangso.core.service.dto.NtsDefaultAttributeDTO;
import org.nentangso.core.service.dto.NtsDefaultLocationDTO;
import org.nentangso.core.service.dto.NtsLocationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "provider",
    havingValue = NtsKeycloakLocationProvider.PROVIDER_NAME
)
@Service
public class NtsKeycloakLocationProvider implements NtsLocationProvider<NtsDefaultLocationDTO> {
    private static final Logger log = LoggerFactory.getLogger(NtsKeycloakLocationProvider.class);

    public static final String PROVIDER_NAME = "org.nentangso.core.service.helper.location.NtsKeycloakLocationProvider";
    public static final String ATTRIBUTE_ACTIVE = "active";
    public static final String ATTRIBUTE_CREATED_AT = "createdAt";
    public static final String ATTRIBUTE_UPDATED_AT = "updatedAt";
    public static final String ATTRIBUTE_DEACTIVATED_AT = "deactivatedAt";
    public static final String ATTRIBUTE_PHONE = "phoneNumber";
    public static final String ATTRIBUTE_ADDRESS_1 = "address1";
    public static final String ATTRIBUTE_ADDRESS_2 = "address2";
    public static final String ATTRIBUTE_COUNTRY = "country";
    public static final String ATTRIBUTE_COUNTRY_CODE = "countryCode";
    public static final String ATTRIBUTE_LOCALIZED_COUNTRY_NAME = "localizedCountryName";
    public static final String ATTRIBUTE_CITY = "city";
    public static final String ATTRIBUTE_PROVINCE = "province";
    public static final String ATTRIBUTE_PROVINCE_CODE = "provinceCode";
    public static final String ATTRIBUTE_LOCALIZED_PROVINCE_NAME = "localizedProvinceName";
    public static final String ATTRIBUTE_ZIP = "zip";
    public static final String ATTRIBUTE_ADDRESS_VERIFIED = "addressVerified";

    private final NtsKeycloakLocationProperties keycloakLocationProperties;
    private final NtsKeycloakClient keycloakClient;
    private final NtsDefaultLocationCacheable locationCacheable;

    public NtsKeycloakLocationProvider(NtsKeycloakLocationProperties keycloakLocationProperties, NtsKeycloakClient keycloakClient, NtsDefaultLocationCacheable locationCacheable) {
        this.keycloakLocationProperties = keycloakLocationProperties;
        this.keycloakClient = keycloakClient;
        this.locationCacheable = locationCacheable;
        validateKeycloakProperties();
    }

    private void validateKeycloakProperties() {
        if (StringUtils.isBlank(keycloakLocationProperties.getAdminBaseUrl())) {
            throw new RuntimeException("Keycloak location requires property nts.helper.location.keycloak.admin-base-url");
        }
        if (StringUtils.isBlank(keycloakLocationProperties.getInternalClientId())) {
            throw new RuntimeException("Keycloak location requires property nts.helper.location.keycloak.internal-client-id");
        }
    }

    @Override
    public Map<Long, NtsDefaultLocationDTO> findAll() {
        Map<Long, NtsDefaultLocationDTO> cacheLocations = locationCacheable.getCacheLocations();
        if (cacheLocations != null && !cacheLocations.isEmpty()) {
            return cacheLocations;
        }
        String clientId = keycloakLocationProperties.getInternalClientId();
        ResponseEntity<List<KeycloakClientRole>> response = keycloakClient.findClientRoles(clientId, false);
        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
            log.error("Get keycloak client roles of client {} error, response={}", clientId, response);
            return Collections.emptyMap();
        }
        Map<Long, NtsDefaultLocationDTO> locations = toLocations(response.getBody())
            .stream()
            .collect(Collectors.toMap(NtsDefaultLocationDTO::getId, v -> v));
        locationCacheable.setCacheLocations(locations);
        return locations;
    }

    private List<NtsDefaultLocationDTO> toLocations(List<KeycloakClientRole> clientRoles) {
        return Optional.ofNullable(clientRoles)
            .orElseGet(Collections::emptyList)
            .stream()
            .map(this::toLocation)
            .collect(Collectors.toList());
    }

    private NtsDefaultLocationDTO toLocation(KeycloakClientRole clientRole) {
        log.trace("toLocationDTO clientRole={}", clientRole);
        Map<String, List<String>> attributes = clientRole.getAttributes();
        return NtsLocationDTO.newDefaultBuilder()
            .id(Long.parseUnsignedLong(clientRole.getName()))
            .name(clientRole.getDescription())
            .phone(singleAttribute(attributes, ATTRIBUTE_PHONE).orElse(null))
            .address1(singleAttribute(attributes, ATTRIBUTE_ADDRESS_1).orElse(null))
            .address2(singleAttribute(attributes, ATTRIBUTE_ADDRESS_2).orElse(null))
            .country(singleAttribute(attributes, ATTRIBUTE_COUNTRY).orElse(null))
            .countryCode(singleAttribute(attributes, ATTRIBUTE_COUNTRY_CODE).orElse(null))
            .localizedCountryName(singleAttribute(attributes, ATTRIBUTE_LOCALIZED_COUNTRY_NAME).orElse(null))
            .city(singleAttribute(attributes, ATTRIBUTE_CITY).orElse(null))
            .province(singleAttribute(attributes, ATTRIBUTE_PROVINCE).orElse(null))
            .provinceCode(singleAttribute(attributes, ATTRIBUTE_PROVINCE_CODE).orElse(null))
            .localizedProvinceName(singleAttribute(attributes, ATTRIBUTE_LOCALIZED_PROVINCE_NAME).orElse(null))
            .zip(singleAttribute(attributes, ATTRIBUTE_ZIP).orElse(null))
            .addressVerified(parseBooleanAttribute(attributes, ATTRIBUTE_ADDRESS_VERIFIED).orElse(false))
            .active(parseBooleanAttribute(attributes, ATTRIBUTE_ACTIVE).orElse(false))
            .deactivatedAt(parseInstantAttribute(attributes, ATTRIBUTE_DEACTIVATED_AT).orElse(null))
            .createdAt(parseInstantAttribute(attributes, ATTRIBUTE_CREATED_AT).orElse(null))
            .updatedAt(parseInstantAttribute(attributes, ATTRIBUTE_UPDATED_AT).orElse(null))
            .customAttributes(toCustomAttributes(attributes))
            .build();
    }

    private List<NtsDefaultAttributeDTO> toCustomAttributes(Map<String, List<String>> input) {
        if (keycloakLocationProperties.getCustomAttributeKeys().isEmpty() || input.isEmpty()) {
            return Collections.emptyList();
        }
        final List<NtsDefaultAttributeDTO> attributes = new ArrayList<>();
        keycloakLocationProperties.getCustomAttributeKeys()
            .stream()
            .filter(input::containsKey)
            .forEach(key -> {
                List<String> values = input.get(key);
                if (values == null || values.isEmpty() || StringUtils.isEmpty(values.get(0))) {
                    return;
                }
                String value = values.get(0);
                NtsDefaultAttributeDTO attribute = NtsAttributeDTO.newBuilder()
                    .key(key)
                    .value(value)
                    .build();
                attributes.add(attribute);
            });
        return attributes;
    }

    private Optional<Boolean> parseBooleanAttribute(Map<String, List<String>> attributes, String key) {
        return singleAttribute(attributes, key)
            .map(Boolean::parseBoolean);
    }

    private Optional<String> singleAttribute(Map<String, List<String>> attributes, String key) {
        if (attributes == null || !attributes.containsKey(key)) {
            return Optional.empty();
        }
        List<String> values = attributes.get(key);
        if (values == null || values.isEmpty() || StringUtils.isEmpty(values.get(0))) {
            return Optional.empty();
        }
        return Optional.of(values.get(0));
    }

    private Optional<Instant> parseInstantAttribute(Map<String, List<String>> attributes, String key) {
        return singleAttribute(attributes, key)
            .map(Instant::parse);
    }

    @Override
    public Set<Long> findAllIds() {
        return findAll()
            .keySet();
    }

    @Override
    public Optional<NtsDefaultLocationDTO> findById(final Long id) {
        NtsDefaultLocationDTO value = findAll()
            .getOrDefault(id, null);
        return Optional.ofNullable(value);
    }
}