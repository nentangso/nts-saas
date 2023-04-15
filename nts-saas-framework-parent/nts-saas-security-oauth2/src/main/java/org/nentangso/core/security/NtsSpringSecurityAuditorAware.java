package org.nentangso.core.security;

import org.nentangso.core.config.NtsConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of {@link AuditorAware} based on Spring Security.
 */
@Component
@ConditionalOnMissingBean(name = "springSecurityAuditorAware")
@ConditionalOnClass(org.springframework.data.domain.AuditorAware.class)
public class NtsSpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(NtsSecurityUtils.getCurrentUserLogin().orElse(NtsConstants.SYSTEM));
    }
}
