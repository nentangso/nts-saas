package org.nentangso.core.service.helper;

import org.nentangso.core.service.dto.NtsLocationDTO;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NtsLocationHelper {
    List<? extends NtsLocationDTO> findAll();

    Set<Long> findAlIds();

    Optional<? extends NtsLocationDTO> findById(Long id);

    Set<Long> getGrantedLocationIds();

    boolean isGrantedAllLocations();

    boolean isGrantedAnyLocations(Iterable<Long> ids);

    boolean isGrantedAnyLocations(Long... ids);

    boolean isGrantedLocation(@Min(1L) Long id);
}
