package org.nentangso.core.repository;

import org.nentangso.core.domain.NtsUserEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link NtsUserEntity} entity.
 */
@Repository
public interface NtsUserRepository extends JpaRepository<NtsUserEntity, String> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<NtsUserEntity> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<NtsUserEntity> findOneWithAuthoritiesByLogin(String login);

    Page<NtsUserEntity> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
