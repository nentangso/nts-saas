package org.nentangso.core.repository;

import org.nentangso.core.domain.OptionEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ConditionalOnProperty(
    prefix = "nts.helper.option",
    name = "enabled",
    havingValue = "true"
)
@Repository
public interface OptionRepository extends CrudRepository<OptionEntity, Long> {
    @Override
    @Modifying
    @Query("update OptionEntity e set e.deleted = true where e.id = ?1")
    void deleteById(Long id);

    @Override
    @Modifying
    @Query("update OptionEntity e set e.deleted = true where e = ?1")
    void delete(OptionEntity entity);

    @Override
    @Modifying
    @Query("update OptionEntity e set e.deleted = true where e.id in ?1")
    void deleteAllById(Iterable<? extends Long> ids);

    @Override
    @Modifying
    @Query("update OptionEntity e set e.deleted = true where e in ?1")
    void deleteAll(Iterable<? extends OptionEntity> entities);

    @Override
    @Modifying
    @Query("update OptionEntity e set e.deleted = true")
    void deleteAll();

    Optional<OptionEntity> findOneByOptionKey(String optionKey);

    List<OptionEntity> findByOptionKeyIn(Collection<String> optionKeys);

    void flush();
}
