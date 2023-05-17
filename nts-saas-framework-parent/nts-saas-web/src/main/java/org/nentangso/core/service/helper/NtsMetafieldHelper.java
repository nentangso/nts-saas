package org.nentangso.core.service.helper;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.domain.NtsMetafieldEntity;
import org.nentangso.core.repository.NtsMetafieldRepository;
import org.nentangso.core.service.dto.NtsMetafieldDTO;
import org.nentangso.core.service.errors.NtsNotFoundException;
import org.nentangso.core.service.mapper.NtsMetafieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ConditionalOnProperty(
    prefix = "nts.helper.metafield",
    name = "enabled",
    havingValue = "true"
)
@Service
public class NtsMetafieldHelper {
    private static final Logger log = LoggerFactory.getLogger(NtsMetafieldHelper.class);

    private final NtsMetafieldRepository metafieldRepository;
    private final NtsMetafieldMapper metafieldMapper;

    public NtsMetafieldHelper(NtsMetafieldRepository metafieldRepository, NtsMetafieldMapper metafieldMapper) {
        this.metafieldRepository = metafieldRepository;
        this.metafieldMapper = metafieldMapper;
    }

    @Transactional
    public NtsMetafieldDTO save(NtsMetafieldDTO metafieldDTO) {
        log.debug("Request to save metafield: {}", metafieldDTO);
        if (metafieldDTO == null) {
            throw new IllegalArgumentException("metafieldDTO");
        }
        NtsMetafieldEntity metafieldEntity = new NtsMetafieldEntity()
            .ownerResource(metafieldDTO.getOwnerResource())
            .ownerId(metafieldDTO.getOwnerId());
        if (metafieldDTO.getId() != null) {
            metafieldEntity = metafieldRepository.findById(metafieldDTO.getId())
                .orElseThrow(NtsNotFoundException::new);
        }
        metafieldEntity.namespace(metafieldDTO.getNamespace())
            .key(metafieldDTO.getKey())
            .value(metafieldDTO.getValue())
            .type(metafieldDTO.getType())
            .description(metafieldDTO.getDescription());
        metafieldEntity = metafieldRepository.save(metafieldEntity);
        return metafieldMapper.toDto(metafieldEntity);
    }

    public Optional<NtsMetafieldDTO> findOne(String ownerResource, Long ownerId, Long id) {
        if (StringUtils.isBlank(ownerResource) || ownerId == null || ownerId <= 0 || id == null || id <= 0) {
            return Optional.empty();
        }
        return metafieldRepository.findById(id)
            .filter(m -> StringUtils.equalsIgnoreCase(m.getOwnerResource(), ownerResource))
            .filter(m -> Objects.equals(m.getOwnerId(), ownerId))
            .map(metafieldMapper::toDto);
    }

    public List<NtsMetafieldDTO> findAllByOwner(String ownerResource, Long ownerId) {
        if (StringUtils.isBlank(ownerResource) || ownerId == null || ownerId <= 0) {
            return Collections.emptyList();
        }
        return metafieldRepository.findAllByOwnerResourceAndOwnerId(ownerResource, ownerId)
            .stream()
            .map(metafieldMapper::toDto)
            .collect(Collectors.toList());
    }

    public long count(String ownerResource, Long ownerId) {
        if (StringUtils.isBlank(ownerResource) || ownerId == null || ownerId <= 0) {
            return 0;
        }
        return metafieldRepository.countByOwnerResourceAndOwnerId(ownerResource, ownerId);
    }

    @Transactional
    public void delete(String ownerResource, Long ownerId, Long id) {
        if (StringUtils.isBlank(ownerResource) || ownerId == null || ownerId <= 0 || id == null || id <= 0) return;
        NtsMetafieldEntity metafieldEntity = metafieldRepository.findById(id)
            .filter(m -> StringUtils.equalsIgnoreCase(m.getOwnerResource(), ownerResource))
            .filter(m -> Objects.equals(m.getOwnerId(), ownerId))
            .orElseThrow(NtsNotFoundException::new);
        metafieldRepository.delete(metafieldEntity);
    }
}
