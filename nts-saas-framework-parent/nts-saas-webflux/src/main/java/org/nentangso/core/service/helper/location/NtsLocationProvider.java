package org.nentangso.core.service.helper.location;

import org.nentangso.core.service.dto.NtsLocationDTO;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

public interface NtsLocationProvider<T extends NtsLocationDTO> {
    Mono<Map<Long, T>> findAll();

    Mono<Set<Long>> findAllIds();

    Mono<T> findById(final Long id);
}
