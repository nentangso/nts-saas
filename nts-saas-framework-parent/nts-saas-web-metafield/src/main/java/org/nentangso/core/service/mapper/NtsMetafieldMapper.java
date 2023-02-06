package org.nentangso.core.service.mapper;

import org.mapstruct.Mapper;
import org.nentangso.core.domain.NtsMetafieldEntity;
import org.nentangso.core.service.dto.NtsMetafieldDTO;
import org.nentangso.core.web.rest.vm.MetafieldInput;

@Mapper(componentModel = "spring", uses = {})
public interface NtsMetafieldMapper {
    NtsMetafieldDTO.Builder create(MetafieldInput input);

    NtsMetafieldDTO toDto(NtsMetafieldEntity metafieldEntity);

    NtsMetafieldDTO toDto(MetafieldInput metafield, String ownerResource, long ownerId);
}
