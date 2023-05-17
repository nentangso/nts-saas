package org.nentangso.core.service.helper;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.domain.NtsNoteEntity;
import org.nentangso.core.repository.NtsNoteRepository;
import org.nentangso.core.service.errors.NtsNotFoundException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnProperty(
    prefix = "nts.helper.note",
    name = "enabled",
    havingValue = "true"
)
@Service
public class NtsNoteHelper {
    private final NtsNoteRepository noteRepository;

    public NtsNoteHelper(NtsNoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Optional<String> findNoteById(@NotNull @Min(1) Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return noteRepository.findById(id).map(NtsNoteEntity::getNote);
    }

    public Map<Long, String> findAllNoteById(Collection<@NotNull @Min(1) Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return noteRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(NtsNoteEntity::getId, NtsNoteEntity::getNote));
    }

    @Transactional
    public Optional<NtsNoteEntity> save(String note, Long id) {
        if (StringUtils.isEmpty(note)) {
            if (Objects.nonNull(id)) {
                noteRepository.deleteById(id);
            }
            return Optional.empty();
        }
        NtsNoteEntity noteEntity = new NtsNoteEntity();
        if (Objects.nonNull(id)) {
            noteEntity = noteRepository.findById(id).orElseThrow(NtsNotFoundException::new);
            if (StringUtils.equals(noteEntity.getNote(), note)) {
                return Optional.of(noteEntity);
            }
        }
        noteEntity.setNote(note);
        return Optional.of(noteRepository.save(noteEntity));
    }

    @Transactional
    public Optional<NtsNoteEntity> save(String note, NtsNoteEntity noteEntity) {
        if (StringUtils.isEmpty(note)) {
            if (Objects.nonNull(noteEntity) && Objects.nonNull(noteEntity.getId())) {
                noteRepository.deleteById(noteEntity.getId());
            }
            return Optional.empty();
        }
        if (Objects.isNull(noteEntity) || Objects.isNull(noteEntity.getId())) {
            noteEntity = new NtsNoteEntity();
        }
        if (StringUtils.equals(noteEntity.getNote(), note)) {
            return Optional.of(noteEntity);
        }
        noteEntity.setNote(note);
        return Optional.of(noteRepository.save(noteEntity));
    }
}
