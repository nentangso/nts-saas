package org.nentangso.core.service.provider;

import org.nentangso.core.service.dto.LocationDTO;

import java.util.Optional;
import java.util.Set;

public interface NtsLocationProvider {
    Set<Long> findAllIds();

    Optional<LocationDTO> findById(Long id);

    boolean isGrantedAnyLocations();
}
