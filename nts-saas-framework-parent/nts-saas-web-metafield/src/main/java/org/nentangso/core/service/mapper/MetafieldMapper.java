package org.nentangso.core.service.mapper;

import org.mapstruct.Mapper;
import org.nentangso.core.domain.MetafieldEntity;
import org.nentangso.core.service.dto.MetafieldDTO;
import org.nentangso.core.web.rest.vm.MetafieldInput;

@Mapper(componentModel = "spring", uses = {})
public interface MetafieldMapper {
    MetafieldDTO.Builder create(MetafieldInput input);

    MetafieldDTO toDto(MetafieldEntity metafieldEntity);

    MetafieldDTO toDto(MetafieldInput metafield, String ownerResource, long ownerId);
}
