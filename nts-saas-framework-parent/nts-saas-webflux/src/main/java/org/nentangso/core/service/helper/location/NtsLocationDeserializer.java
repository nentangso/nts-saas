package org.nentangso.core.service.helper.location;

import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.util.Set;

public interface NtsLocationDeserializer {
    Mono<Set<Long>> getGrantedLocationIds();

    Mono<Boolean> isGrantedAllLocations();

    Mono<Boolean> isGrantedAnyLocations(Iterable<Long> ids);

    Mono<Boolean> isGrantedAnyLocations(Long... ids);

    Mono<Boolean> isGrantedLocation(@Min(1L) Long id);
}
