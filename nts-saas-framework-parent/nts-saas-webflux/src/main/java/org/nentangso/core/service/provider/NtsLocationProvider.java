package org.nentangso.core.service.provider;

import org.nentangso.core.service.dto.NtsLocationDTO;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.util.Set;

public interface NtsLocationProvider {
    <T extends NtsLocationDTO> Mono<Set<T>> findAll();

    Mono<Set<Long>> findAllIds();

    Mono<NtsLocationDTO> findById(Long id);

    boolean isGrantedAnyLocations();

    boolean hasGrantedLocation(@Min(1) Integer id);

}
