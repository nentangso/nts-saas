package org.nentangso.core.repository;

import org.nentangso.core.domain.NtsAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link NtsAuthority} entity.
 */
public interface NtsAuthorityRepository extends JpaRepository<NtsAuthority, String> {}
