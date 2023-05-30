package org.nentangso.core.service.provider;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.service.dto.NtsLocationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "enabled",
    havingValue = "true"
)
@Service
public class NtsLocationProviderFactory {
    private final ApplicationContext applicationContext;
    private final String provider;

    public NtsLocationProviderFactory(
        ApplicationContext applicationContext,
        @Value("${nts.helper.location.provider:}") String provider
    ) {
        this.applicationContext = applicationContext;
        this.provider = provider;
    }

    public NtsLocationProvider<? extends NtsLocationDTO> getLocationProvider() {
        if (StringUtils.equals(provider, NtsKeycloakLocationProvider.PROVIDER_NAME)) {
            return applicationContext.getBean(NtsKeycloakLocationProvider.class);
        }
        throw new RuntimeException(String.format(
            "Configuration property nts.helper.location.provider must be one of supported values %s",
            StringUtils.joinWith(", ", NtsKeycloakLocationProvider.PROVIDER_NAME)
        ));
    }
}
