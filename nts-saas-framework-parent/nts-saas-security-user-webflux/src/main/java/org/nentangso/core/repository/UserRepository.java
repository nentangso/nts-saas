package org.nentangso.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.BeanComparator;
import org.nentangso.core.domain.Authority;
import org.nentangso.core.domain.UserEntity;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * Spring Data R2DBC repository for the {@link UserEntity} entity.
 */
@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, String>, UserRepositoryInternal {
    Mono<UserEntity> findOneByLogin(String login);

    Flux<UserEntity> findAllByIdNotNull(Pageable pageable);

    Flux<UserEntity> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    Mono<Long> count();

    @Query("INSERT INTO nts_user_authority VALUES(:userId, :authority)")
    Mono<Void> saveUserAuthority(String userId, String authority);

    @Query("DELETE FROM nts_user_authority")
    Mono<Void> deleteAllUserAuthorities();

    @Query("DELETE FROM nts_user_authority WHERE user_id = :userId")
    Mono<Void> deleteUserAuthorities(Long userId);
}

interface UserRepositoryInternal {
    Mono<UserEntity> findOneWithAuthoritiesByLogin(String login);

    Mono<UserEntity> create(UserEntity user);

    Flux<UserEntity> findAllWithAuthorities(Pageable pageable);
}

class UserRepositoryInternalImpl implements UserRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final R2dbcConverter r2dbcConverter;

    public UserRepositoryInternalImpl(DatabaseClient db, R2dbcEntityTemplate r2dbcEntityTemplate, R2dbcConverter r2dbcConverter) {
        this.db = db;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.r2dbcConverter = r2dbcConverter;
    }

    @Override
    public Mono<UserEntity> findOneWithAuthoritiesByLogin(String login) {
        return findOneWithAuthoritiesBy("login", login);
    }

    @Override
    public Flux<UserEntity> findAllWithAuthorities(Pageable pageable) {
        String property = pageable.getSort().stream().map(Sort.Order::getProperty).findFirst().orElse("id");
        String direction = String.valueOf(
            pageable.getSort().stream().map(Sort.Order::getDirection).findFirst().orElse(Sort.DEFAULT_DIRECTION)
        );
        long page = pageable.getPageNumber();
        long size = pageable.getPageSize();

        return db
            .sql("SELECT * FROM nts_user u LEFT JOIN nts_user_authority ua ON u.id=ua.user_id")
            .map((row, metadata) ->
                Tuples.of(
                    r2dbcConverter.read(UserEntity.class, row, metadata),
                    Optional.ofNullable(row.get("authority_name", String.class))
                )
            )
            .all()
            .groupBy(t -> t.getT1().getLogin())
            .flatMap(l -> l.collectList().map(t -> updateUserWithAuthorities(t.get(0).getT1(), t)))
            .sort(
                Sort.Direction.fromString(direction) == Sort.DEFAULT_DIRECTION
                    ? new BeanComparator<>(property)
                    : new BeanComparator<>(property).reversed()
            )
            .skip(page * size)
            .take(size);
    }

    @Override
    public Mono<UserEntity> create(UserEntity user) {
        return r2dbcEntityTemplate.insert(UserEntity.class).using(user).defaultIfEmpty(user);
    }

    private Mono<UserEntity> findOneWithAuthoritiesBy(String fieldName, Object fieldValue) {
        return db
            .sql("SELECT * FROM nts_user u LEFT JOIN nts_user_authority ua ON u.id=ua.user_id WHERE u." + fieldName + " = :" + fieldName)
            .bind(fieldName, fieldValue)
            .map((row, metadata) ->
                Tuples.of(
                    r2dbcConverter.read(UserEntity.class, row, metadata),
                    Optional.ofNullable(row.get("authority_name", String.class))
                )
            )
            .all()
            .collectList()
            .filter(l -> !l.isEmpty())
            .map(l -> updateUserWithAuthorities(l.get(0).getT1(), l));
    }

    private UserEntity updateUserWithAuthorities(UserEntity user, List<Tuple2<UserEntity, Optional<String>>> tuples) {
        user.setAuthorities(
            tuples
                .stream()
                .filter(t -> t.getT2().isPresent())
                .map(t -> {
                    Authority authority = new Authority();
                    authority.setName(t.getT2().get());
                    return authority;
                })
                .collect(Collectors.toSet())
        );

        return user;
    }
}
