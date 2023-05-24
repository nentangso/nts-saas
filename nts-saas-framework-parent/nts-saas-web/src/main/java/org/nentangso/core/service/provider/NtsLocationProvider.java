package org.nentangso.core.service.provider;

import org.nentangso.core.service.dto.NtsLocationDTO;

import javax.validation.constraints.Min;
import java.util.Optional;
import java.util.Set;

public interface NtsLocationProvider {
    Set<Long> findAllIds();

    Optional<NtsLocationDTO> findById(Long id);

    boolean isGrantedAnyLocations();

    boolean hasGrantedLocation(@Min(1) Integer id);
}
