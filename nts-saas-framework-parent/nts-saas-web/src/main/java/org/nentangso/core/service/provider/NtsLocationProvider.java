package org.nentangso.core.service.provider;

import org.nentangso.core.service.dto.NtsLocationDTO;

import javax.validation.constraints.Min;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface NtsLocationProvider<T extends NtsLocationDTO> {
    Map<Long, T> findAll();

    Set<Long> findAllIds();

    Optional<T> findById(Long id);

    Set<Long> getGrantedLocationIds();

    boolean isGrantedAllLocations();

    boolean isGrantedAnyLocations(Iterable<Long> ids);

    boolean isGrantedAnyLocations(Long... ids);

    boolean isGrantedLocation(@Min(1L) Long id);
}
