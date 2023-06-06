package org.nentangso.core.service.helper;

import org.nentangso.core.service.dto.NtsLocationDTO;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.Set;

public interface NtsLocationHelper {
    Mono<List<? extends NtsLocationDTO>> findAll();

    Mono<Set<Long>> findAlIds();

    Mono<? extends NtsLocationDTO> findById(Long id);

    Mono<Set<Long>> getGrantedLocationIds();

    Mono<Boolean> isGrantedAllLocations();

    Mono<Boolean> isGrantedAnyLocations(Iterable<Long> ids);

    Mono<Boolean> isGrantedAnyLocations(Long... ids);

    Mono<Boolean> isGrantedLocation(@Min(1L) Long id);
}
