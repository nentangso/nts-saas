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
    private final NtsLocationProvider<?> locationProvider;

    public NtsLocationHelper(NtsLocationProviderFactory locationProviderFactory) {
        this.locationProvider = locationProviderFactory.getLocationProvider();
    }

    private Mono<Set<Long>> findAlIds() {
        return locationProvider.findAllIds();
    }

    public Mono<List<NtsLocationDTO>> findAll() {
        return locationProvider.findAll()
            .map(Map::values)
            .map(ArrayList::new)
            .map(Collections::unmodifiableList);
    }

    public Mono<NtsLocationDTO> findById(Long id) {
        return locationProvider.findById(id);
    }

    public Mono<Set<Long>> getGrantedLocationIds() {
        if (isGrantedAnyLocations()) {
            return findAlIds();
        }
        return Mono.just(Collections.emptySet());
    }

    public boolean isGrantedAnyLocations() {
        return locationProvider.isGrantedAnyLocations();
    }

    public boolean hasGrantedLocation(@Min(1) Integer id) {
        return locationProvider.hasGrantedLocation(id);
    }
}
