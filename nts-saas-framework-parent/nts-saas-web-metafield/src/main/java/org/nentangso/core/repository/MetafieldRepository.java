package org.nentangso.core.repository;

import org.nentangso.core.domain.MetafieldEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@ConditionalOnProperty(
    prefix = "nts.helper.metafield",
    name = "enabled",
    havingValue = "true"
)
@Repository
public interface MetafieldRepository extends CrudRepository<MetafieldEntity, Long> {
    @Override
    @Modifying
    @Query("update MetafieldEntity e set e.deleted = true where e.id = ?1")
    void deleteById(Long id);

    @Override
    @Modifying
    @Query("update MetafieldEntity e set e.deleted = true where e = ?1")
    void delete(MetafieldEntity entity);

    @Override
    @Modifying
    @Query("update MetafieldEntity e set e.deleted = true where e.id in ?1")
    void deleteAllById(Iterable<? extends Long> ids);

    @Override
    @Modifying
    @Query("update MetafieldEntity e set e.deleted = true where e in ?1")
    void deleteAll(Iterable<? extends MetafieldEntity> entities);

    @Override
    @Modifying
    @Query("update MetafieldEntity e set e.deleted = true")
    void deleteAll();

    List<MetafieldEntity> findAllByOwnerResourceAndOwnerId(String ownerResource, Long ownerId);

    long countByOwnerResourceAndOwnerId(String ownerResource, Long ownerId);
}
