package org.nentangso.core.service.helper.location;

import org.nentangso.core.security.NtsSecurityUtils;
import org.nentangso.core.service.dto.NtsLocationDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NtsBitSetLocationDeserializer implements NtsLocationDeserializer {
    public static final String DESERIALIZER_NAME = "org.nentangso.core.service.helper.location.NtsBitSetLocationDeserializer";

    private final NtsLocationProvider<? extends NtsLocationDTO> locationProvider;
    private final String bitSetClaim;

    public NtsBitSetLocationDeserializer(NtsLocationProvider<? extends NtsLocationDTO> locationProvider, String bitSetClaim) {
        this.locationProvider = locationProvider;
        this.bitSetClaim = bitSetClaim;
    }

    @Override
    public Mono<Set<Long>> getGrantedLocationIds() {
        return getCurrentUserLocationBitSet()
            .flatMap(bitSet -> {
                if (isGrantedAllLocations(bitSet)) {
                    return locationProvider.findAllIds();
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
        return NtsSecurityUtils.getCurrentUserClaim(bitSetClaim)
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
