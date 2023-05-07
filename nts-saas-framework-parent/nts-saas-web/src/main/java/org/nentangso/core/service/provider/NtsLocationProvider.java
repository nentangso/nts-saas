package org.nentangso.core.service.provider;

import org.nentangso.core.service.dto.LocationDTO;

import javax.validation.constraints.Min;
import java.util.Optional;
import java.util.Set;

public interface NtsLocationProvider {
    Set<Long> findAllIds();

    Optional<LocationDTO> findById(Long id);

    boolean isGrantedAnyLocations();

    boolean hasGrantedLocation(@Min(1) Integer id);
}
