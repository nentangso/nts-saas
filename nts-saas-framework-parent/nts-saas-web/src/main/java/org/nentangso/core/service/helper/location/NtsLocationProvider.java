package org.nentangso.core.service.helper.location;

import org.nentangso.core.service.dto.NtsLocationDTO;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface NtsLocationProvider<T extends NtsLocationDTO> {
    Map<Long, T> findAll();

    Set<Long> findAllIds();

    Optional<T> findById(Long id);
}
