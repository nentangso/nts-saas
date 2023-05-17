package org.nentangso.core.service.helper;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.domain.NtsTagsEntity;
import org.nentangso.core.repository.NtsTagsRepository;
import org.nentangso.core.service.errors.NtsNotFoundException;
import org.nentangso.core.service.utils.NtsTextUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnProperty(
    prefix = "nts.helper.tag",
    name = "enabled",
    havingValue = "true"
)
@Component
public class NtsTagsHelper {
    private final NtsTagsRepository tagsRepository;

    public NtsTagsHelper(NtsTagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    public Set<@NotNull String> findTagsById(@NotNull @Min(1) Long id) {
        if (id == null || id <= 0) {
            return Collections.emptySet();
        }
        return tagsRepository.findById(id)
            .map(NtsTagsEntity::getTags)
            .map(NtsTextUtils::splitTags)
            .orElseGet(Collections::emptySet);
    }

    public Optional<String> findJoinedTagsById(@NotNull @Min(1) Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return tagsRepository.findById(id).map(NtsTagsEntity::getTags);
    }

    public Map<Long, Set<String>> findAllTagsById(Collection<@NotNull @Min(1) Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return tagsRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(NtsTagsEntity::getId, v -> NtsTextUtils.splitTags(v.getTags())));
    }

    public Map<Long, String> findAllJoinedTagsById(Collection<@NotNull @Min(1) Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return tagsRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(NtsTagsEntity::getId, NtsTagsEntity::getTags));
    }

    @Transactional
    public Optional<NtsTagsEntity> save(Set<String> tags, Long id) {
        String joinedTags = NtsTextUtils.joinTags(tags);
        return save(joinedTags, id);
    }

    @Transactional
    public Optional<NtsTagsEntity> save(String joinedTags, Long id) {
        if (StringUtils.isEmpty(joinedTags)) {
            if (Objects.nonNull(id)) {
                tagsRepository.deleteById(id);
            }
            return Optional.empty();
        }
        NtsTagsEntity tagsEntity = new NtsTagsEntity();
        if (Objects.nonNull(id)) {
            tagsEntity = tagsRepository.findById(id).orElseThrow(NtsNotFoundException::new);
            if (StringUtils.equals(tagsEntity.getTags(), joinedTags)) {
                return Optional.of(tagsEntity);
            }
        }
        tagsEntity.setTags(joinedTags);
        return Optional.of(tagsRepository.save(tagsEntity));
    }

    @Transactional
    public Optional<NtsTagsEntity> save(String joinedTags, NtsTagsEntity tagsEntity) {
        if (StringUtils.isEmpty(joinedTags)) {
            if (Objects.nonNull(tagsEntity) && Objects.nonNull(tagsEntity.getId())) {
                tagsRepository.deleteById(tagsEntity.getId());
            }
            return Optional.empty();
        }
        if (Objects.isNull(tagsEntity) || Objects.isNull(tagsEntity.getId())) {
            tagsEntity = new NtsTagsEntity();
        }
        if (StringUtils.equals(tagsEntity.getTags(), joinedTags)) {
            return Optional.of(tagsEntity);
        }
        tagsEntity.setTags(joinedTags);
        return Optional.of(tagsRepository.save(tagsEntity));
    }
}
