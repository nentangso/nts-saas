package org.nentangso.core.service.helper.location;

import org.nentangso.core.service.dto.NtsLocationDTO;
import org.nentangso.core.service.helper.NtsLocationHelper;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NtsDefaultLocationHelper implements NtsLocationHelper {
    private final NtsLocationProvider<? extends NtsLocationDTO> locationProvider;
    private final NtsLocationDeserializer locationDeserializer;

    public NtsDefaultLocationHelper(
        NtsLocationProvider<? extends NtsLocationDTO> locationProvider,
        NtsLocationDeserializer locationDeserializer
    ) {
        this.locationProvider = locationProvider;
        this.locationDeserializer = locationDeserializer;
    }

    public List<? extends NtsLocationDTO> findAll() {
        return locationProvider.findAll()
            .values()
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }

    public Set<Long> findAlIds() {
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
