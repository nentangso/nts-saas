package org.nentangso.core.repository;

import org.nentangso.core.domain.NtsAuthority;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * Spring Data R2DBC repository for the {@link NtsAuthority} entity.
 */
public interface NtsAuthorityRepository extends R2dbcRepository<NtsAuthority, String> {}
