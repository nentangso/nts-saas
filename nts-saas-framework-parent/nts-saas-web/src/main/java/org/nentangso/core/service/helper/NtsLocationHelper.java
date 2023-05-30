package org.nentangso.core.service.helper;

import org.nentangso.core.service.dto.NtsLocationDTO;
import org.nentangso.core.service.provider.NtsLocationProvider;
import org.nentangso.core.service.provider.NtsLocationProviderFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "enabled",
    havingValue = "true"
)
@Service
public class NtsLocationHelper {
    private final NtsLocationProvider<? extends NtsLocationDTO> locationProvider;

    public NtsLocationHelper(NtsLocationProviderFactory locationProviderFactory) {
        this.locationProvider = locationProviderFactory.getLocationProvider();
    }

    public List<? extends NtsLocationDTO> findAll() {
        return locationProvider.findAll()
            .values()
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }

    private Set<Long> findAlIds() {
        return locationProvider.findAllIds();
    }

    public Optional<? extends NtsLocationDTO> findById(Long id) {
        return locationProvider.findById(id);
    }

    public Set<Long> getGrantedLocationIds() {
        return locationProvider.getGrantedLocationIds();
    }

    public boolean isGrantedAllLocations() {
        return locationProvider.isGrantedAllLocations();
    }

    public boolean isGrantedAnyLocations(Iterable<Long> ids) {
        return locationProvider.isGrantedAnyLocations(ids);
    }

    public boolean isGrantedAnyLocations(Long... ids) {
        return locationProvider.isGrantedAnyLocations(ids);
    }

    public boolean isGrantedLocation(@Min(1L) Long id) {
        return locationProvider.isGrantedLocation(id);
    }
}
