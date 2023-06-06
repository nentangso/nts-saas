package org.nentangso.core.service.helper.location;

import org.nentangso.core.service.dto.NtsLocationDTO;
import org.nentangso.core.service.helper.NtsLocationHelper;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.util.*;

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

    public Mono<List<? extends NtsLocationDTO>> findAll() {
        return locationProvider.findAll()
            .map(Map::values)
            .map(ArrayList::new)
            .map(Collections::unmodifiableList);
    }

    public Mono<Set<Long>> findAlIds() {
        return locationProvider.findAllIds();
    }

    public Mono<? extends NtsLocationDTO> findById(Long id) {
        return locationProvider.findById(id);
    }

    public Mono<Set<Long>> getGrantedLocationIds() {
        return locationDeserializer.getGrantedLocationIds();
    }

    public Mono<Boolean> isGrantedAllLocations() {
        return locationDeserializer.isGrantedAllLocations();
    }

    public Mono<Boolean> isGrantedAnyLocations(Iterable<Long> ids) {
        return locationDeserializer.isGrantedAnyLocations(ids);
    }

    public Mono<Boolean> isGrantedAnyLocations(Long... ids) {
        return locationDeserializer.isGrantedAnyLocations(ids);
    }

    public Mono<Boolean> isGrantedLocation(@Min(1L) Long id) {
        return locationDeserializer.isGrantedLocation(id);
    }
}
