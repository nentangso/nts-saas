package org.nentangso.core.service.provider;

import org.nentangso.core.config.NtsProperties;
import org.nentangso.core.service.dto.NtsLocationDTO;
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
        NtsProperties ntsProperties
    ) {
        this.applicationContext = applicationContext;
        this.provider = ntsProperties.getHelper().getLocation().getProvider();
    }

    public NtsLocationProvider<? extends NtsLocationDTO> getLocationProvider() {
        try {
            Class<?> clazz = Class.forName(provider);
            return (NtsLocationProvider<? extends NtsLocationDTO>) applicationContext.getBean(clazz);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                "Configuration property nts.helper.location.provider class %s can not be loaded.",
                provider
            ));
        }
    }
}
