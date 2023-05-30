package org.nentangso.core.service.provider;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.client.NtsKeycloakClient;
import org.nentangso.core.client.vm.KeycloakClientRole;
import org.nentangso.core.config.NtsKeycloakLocationProperties;
import org.nentangso.core.security.NtsSecurityUtils;
import org.nentangso.core.service.dto.NtsAttributeDTO;
import org.nentangso.core.service.dto.NtsDefaultAttributeDTO;
import org.nentangso.core.service.dto.NtsDefaultLocationDTO;
import org.nentangso.core.service.dto.NtsLocationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "provider",
    havingValue = NtsKeycloakLocationProvider.PROVIDER_NAME
)
@Service
public class NtsKeycloakLocationProvider implements NtsLocationProvider<NtsDefaultLocationDTO> {
    private static final Logger log = LoggerFactory.getLogger(NtsKeycloakLocationProvider.class);

    public static boolean disableCacheForTests = false;

    public static final String PROVIDER_NAME = "keycloak";
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
    private final ReactiveRedisOperations<String, Map<Long, NtsDefaultLocationDTO>> locationsRedisOps;

    public NtsKeycloakLocationProvider(NtsKeycloakLocationProperties keycloakLocationProperties, NtsKeycloakClient keycloakClient, ReactiveRedisOperations<String, Map<Long, NtsDefaultLocationDTO>> locationsRedisOps) {
        this.keycloakLocationProperties = keycloakLocationProperties;
        this.keycloakClient = keycloakClient;
        this.locationsRedisOps = locationsRedisOps;
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
    public Mono<Map<Long, NtsDefaultLocationDTO>> findAll() {
        return getCacheLocations()
            .switchIfEmpty(Mono.defer(() -> {
                String clientId = keycloakLocationProperties.getInternalClientId();
                return keycloakClient.findClientRoles(clientId, false)
                    .map(this::toLocations)
                    .map(items -> items.stream().collect(Collectors.toMap(NtsDefaultLocationDTO::getId, v -> v)))
                    .flatMap(this::setCacheLocations);
            }));
    }

    private Mono<Map<Long, NtsDefaultLocationDTO>> getCacheLocations() {
        if (disableCacheForTests) {
            return Mono.empty();
        }
        final String cacheKey = generateCacheKey();
        return locationsRedisOps.opsForValue().get(cacheKey);
    }

    private String generateCacheKey() {
        return keycloakLocationProperties.getCacheKeyPrefix() + "locations_by_id";
    }

    private Mono<Map<Long, NtsDefaultLocationDTO>> setCacheLocations(Map<Long, NtsDefaultLocationDTO> items) {
        if (disableCacheForTests) {
            return Mono.just(items);
        }
        final String cacheKey = generateCacheKey();
        return locationsRedisOps.opsForValue().set(cacheKey, items).thenReturn(items);
    }

    private List<NtsDefaultLocationDTO> toLocations(Collection<KeycloakClientRole> clientRoles) {
        return clientRoles.stream()
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
    public Mono<Set<Long>> findAllIds() {
        return findAll()
            .map(Map::keySet);
    }

    @Override
    public Mono<NtsDefaultLocationDTO> findById(final Long id) {
        return findAll()
            .flatMap(f -> Mono.justOrEmpty(f.getOrDefault(id, null)));
    }

    @Override
    public Mono<Set<Long>> getGrantedLocationIds() {
        return getCurrentUserLocationBitSet()
            .flatMap(bitSet -> {
                if (isGrantedAllLocations(bitSet)) {
                    return findAllIds();
                }
                if (bitSet.length() <= 1) {
                    return Mono.just(Collections.emptySet());
                }
                return Flux.range(1, bitSet.length() - 1)
                    .filter(bitSet::get)
                    .map(Integer::toUnsignedLong)
                    .collect(Collectors.toSet());
            });
    }

    private Mono<BitSet> getCurrentUserLocationBitSet() {
        return NtsSecurityUtils.getCurrentUserClaim(keycloakLocationProperties.getBitSetClaim())
            .map(this::parseBitSet)
            .switchIfEmpty(Mono.just(new BitSet()));
    }

    private BitSet parseBitSet(Object input) {
        if (input instanceof String) {
            byte[] bytes = Base64.getDecoder().decode((String) input);
            return BitSet.valueOf(bytes);
        }
        return new BitSet();
    }

    @Override
    public Mono<Boolean> isGrantedAllLocations() {
        return getCurrentUserLocationBitSet()
            .map(this::isGrantedAllLocations);
    }

    private boolean isGrantedAllLocations(BitSet bitSet) {
        return bitSet.length() > 0 && bitSet.get(0);
    }

    @Override
    public Mono<Boolean> isGrantedAnyLocations(Iterable<Long> ids) {
        return getCurrentUserLocationBitSet()
            .map(bitSet -> StreamSupport.stream(ids.spliterator(), false).anyMatch(id -> isGrantedLocation(bitSet, id)));
    }

    @Override
    public Mono<Boolean> isGrantedAnyLocations(Long... ids) {
        Iterable<Long> it = Stream.of(ids).collect(Collectors.toSet());
        return isGrantedAnyLocations(it);
    }

    @Override
    public Mono<Boolean> isGrantedLocation(@Min(1L) Long id) {
        return getCurrentUserLocationBitSet()
            .map(bitSet -> isGrantedLocation(bitSet, id));
    }

    private boolean isGrantedLocation(BitSet bitSet, Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        return isGrantedAllLocations(bitSet)
            || (id.intValue() < bitSet.length() && bitSet.get(id.intValue()));
    }
}
