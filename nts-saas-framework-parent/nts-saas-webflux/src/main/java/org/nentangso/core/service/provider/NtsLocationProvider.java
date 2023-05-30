package org.nentangso.core.service.provider;

import org.nentangso.core.service.dto.NtsLocationDTO;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.util.Map;
import java.util.Set;

public interface NtsLocationProvider<T extends NtsLocationDTO> {
    Mono<Map<Long, T>> findAll();

    Mono<Set<Long>> findAllIds();

    Mono<T> findById(final Long id);

    Mono<Set<Long>> getGrantedLocationIds();

    Mono<Boolean> isGrantedAllLocations();

    Mono<Boolean> isGrantedAnyLocations(Iterable<Long> ids);

    Mono<Boolean> isGrantedAnyLocations(Long... ids);

    Mono<Boolean> isGrantedLocation(@Min(1L) Long id);
}
