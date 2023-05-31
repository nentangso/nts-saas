package org.nentangso.core.service.helper.location;

import javax.validation.constraints.Min;
import java.util.Set;

public interface NtsLocationDeserializer {
    Set<Long> getGrantedLocationIds();

    boolean isGrantedAllLocations();

    boolean isGrantedAnyLocations(Iterable<Long> ids);

    boolean isGrantedAnyLocations(Long... ids);

    boolean isGrantedLocation(@Min(1L) Long id);
}
