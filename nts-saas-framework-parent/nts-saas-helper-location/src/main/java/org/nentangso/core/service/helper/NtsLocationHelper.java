package org.nentangso.core.service.helper;

import org.nentangso.core.service.dto.LocationDTO;
import org.nentangso.core.service.provider.NtsLocationProvider;
import org.nentangso.core.service.provider.NtsLocationProviderFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "enabled",
    havingValue = "true"
)
@Service
public class NtsLocationHelper {
    private final NtsLocationProvider locationProvider;

    public NtsLocationHelper(NtsLocationProviderFactory locationProviderFactory) {
        this.locationProvider = locationProviderFactory.getLocationProvider();
    }

    private Set<Long> findAlIds() {
        return locationProvider.findAllIds();
    }

    public Set<LocationDTO> findAll() {
        return Collections.emptySet();
    }

    public Optional<LocationDTO> findById(Long id) {
        return locationProvider.findById(id);
    }

    public Set<Long> getGrantedLocationIds() {
        if (isGrantedAnyLocations()) {
            return findAlIds();
        }
        return Collections.emptySet();
    }

    public boolean isGrantedAnyLocations() {
        return locationProvider.isGrantedAnyLocations();
    }
}
