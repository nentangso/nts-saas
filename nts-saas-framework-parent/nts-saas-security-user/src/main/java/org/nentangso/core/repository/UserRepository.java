package org.nentangso.core.repository;

import org.nentangso.core.domain.UserEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link UserEntity} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<UserEntity> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<UserEntity> findOneWithAuthoritiesByLogin(String login);

    Page<UserEntity> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
