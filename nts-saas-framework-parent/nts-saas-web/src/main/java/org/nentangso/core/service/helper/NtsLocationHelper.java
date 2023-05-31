package org.nentangso.core.service.helper;

import org.nentangso.core.service.dto.NtsLocationDTO;
import org.nentangso.core.service.helper.location.NtsLocationDeserializer;
import org.nentangso.core.service.helper.location.NtsLocationProvider;
import org.nentangso.core.service.helper.location.NtsLocationProviderFactory;
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
    private final NtsLocationDeserializer locationDeserializer;

    public NtsLocationHelper(NtsLocationProviderFactory locationProviderFactory) {
        this.locationProvider = locationProviderFactory.getLocationProvider();
        this.locationDeserializer = locationProviderFactory.getLocationDeserializer();
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
        return locationDeserializer.getGrantedLocationIds();
    }

    public boolean isGrantedAllLocations() {
        return locationDeserializer.isGrantedAllLocations();
    }

    public boolean isGrantedAnyLocations(Iterable<Long> ids) {
        return locationDeserializer.isGrantedAnyLocations(ids);
    }

    public boolean isGrantedAnyLocations(Long... ids) {
        return locationDeserializer.isGrantedAnyLocations(ids);
    }

    public boolean isGrantedLocation(@Min(1L) Long id) {
        return locationDeserializer.isGrantedLocation(id);
    }
}
