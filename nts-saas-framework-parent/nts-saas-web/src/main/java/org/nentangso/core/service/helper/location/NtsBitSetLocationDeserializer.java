package org.nentangso.core.service.helper.location;

import org.nentangso.core.security.NtsSecurityUtils;
import org.nentangso.core.service.dto.NtsLocationDTO;

import javax.validation.constraints.Min;
import java.util.*;
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
    public Set<Long> getGrantedLocationIds() {
        BitSet bitSet = getCurrentUserLocationBitSet();
        if (isGrantedAllLocations(bitSet)) {
            return locationProvider.findAllIds();
        }
        if (bitSet.length() <= 1) {
            return Collections.emptySet();
        }
        final Set<Long> locationIds = new LinkedHashSet<>();
        for (int i = 1; i < bitSet.length(); i++) {
            if (!bitSet.get(i)) {
                continue;
            }
            locationIds.add(Integer.toUnsignedLong(i));
        }
        return locationIds;
    }

    private BitSet getCurrentUserLocationBitSet() {
        return NtsSecurityUtils.getCurrentUserClaim(bitSetClaim)
            .map(this::parseBitSet)
            .orElseGet(BitSet::new);
    }

    private BitSet parseBitSet(Object input) {
        if (input instanceof String) {
            byte[] bytes = Base64.getDecoder().decode((String) input);
            return BitSet.valueOf(bytes);
        }
        return new BitSet();
    }

    @Override
    public boolean isGrantedAllLocations() {
        BitSet bitSet = getCurrentUserLocationBitSet();
        return isGrantedAllLocations(bitSet);
    }

    private boolean isGrantedAllLocations(BitSet bitSet) {
        return bitSet.length() > 0 && bitSet.get(0);
    }

    @Override
    public boolean isGrantedAnyLocations(Iterable<Long> ids) {
        BitSet bitSet = getCurrentUserLocationBitSet();
        return StreamSupport.stream(ids.spliterator(), false)
            .anyMatch(id -> isGrantedLocation(bitSet, id));
    }

    @Override
    public boolean isGrantedAnyLocations(Long... ids) {
        Iterable<Long> it = Stream.of(ids).collect(Collectors.toSet());
        return isGrantedAnyLocations(it);
    }

    @Override
    public boolean isGrantedLocation(@Min(1L) Long id) {
        BitSet bitSet = getCurrentUserLocationBitSet();
        return isGrantedLocation(bitSet, id);
    }

    private boolean isGrantedLocation(BitSet bitSet, Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        return isGrantedAllLocations(bitSet)
            || (id.intValue() < bitSet.length() && bitSet.get(id.intValue()));
    }
}
