package org.nentangso.core.repository;

import org.nentangso.core.domain.NtsNoteEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@ConditionalOnProperty(
    prefix = "nts.helper.note",
    name = "enabled",
    havingValue = "true"
)
@Repository
public interface NtsNoteRepository extends CrudRepository<NtsNoteEntity, Long> {
    @Override
    @Modifying
    @Query("update NtsNoteEntity e set e.deleted = true where e.id = ?1")
    void deleteById(Long id);

    @Override
    @Modifying
    @Query("update NtsNoteEntity e set e.deleted = true where e = ?1")
    void delete(NtsNoteEntity entity);

    @Override
    @Modifying
    @Query("update NtsNoteEntity e set e.deleted = true where e.id in ?1")
    void deleteAllById(Iterable<? extends Long> ids);

    @Override
    @Modifying
    @Query("update NtsNoteEntity e set e.deleted = true where e in ?1")
    void deleteAll(Iterable<? extends NtsNoteEntity> entities);

    @Override
    @Modifying
    @Query("update NtsNoteEntity e set e.deleted = true")
    void deleteAll();

    List<NtsNoteEntity> findAllById(Iterable<Long> ids);
}
