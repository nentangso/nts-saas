package org.nentangso.core.service.helper;

import org.nentangso.core.service.dto.NtsLocationDTO;
import org.nentangso.core.service.provider.NtsLocationProvider;
import org.nentangso.core.service.provider.NtsLocationProviderFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.util.*;

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

    public Mono<List<? extends NtsLocationDTO>> findAll() {
        return locationProvider.findAll()
            .map(Map::values)
            .map(ArrayList::new)
            .map(Collections::unmodifiableList);
    }

    private Mono<Set<Long>> findAlIds() {
        return locationProvider.findAllIds();
    }

    public Mono<? extends NtsLocationDTO> findById(Long id) {
        return locationProvider.findById(id);
    }

    public Mono<Set<Long>> getGrantedLocationIds() {
        return locationProvider.getGrantedLocationIds();
    }

    public Mono<Boolean> isGrantedAllLocations() {
        return locationProvider.isGrantedAllLocations();
    }

    public Mono<Boolean> isGrantedAnyLocations(Iterable<Long> ids) {
        return locationProvider.isGrantedAnyLocations(ids);
    }

    public Mono<Boolean> isGrantedAnyLocations(Long... ids) {
        return locationProvider.isGrantedAnyLocations(ids);
    }

    public Mono<Boolean> isGrantedLocation(@Min(1L) Long id) {
        return locationProvider.isGrantedLocation(id);
    }
}
