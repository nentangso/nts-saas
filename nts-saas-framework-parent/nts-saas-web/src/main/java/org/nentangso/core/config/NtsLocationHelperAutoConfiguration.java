package org.nentangso.core.config;

import org.nentangso.core.client.NtsHelperLocationRestClient;
import org.nentangso.core.client.NtsKeycloakClient;
import org.nentangso.core.service.dto.NtsLocationDTO;
import org.nentangso.core.service.helper.NtsLocationHelper;
import org.nentangso.core.service.helper.location.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "enabled",
    havingValue = "true"
)
@Configuration
public class NtsLocationHelperAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public NtsDefaultLocationCacheable ntsDefaultLocationCacheable(
        NtsProperties ntsProperties,
        javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration
    ) {
        return new NtsDefaultLocationCacheable(ntsProperties, jcacheConfiguration);
    }

    @Bean
    @ConditionalOnProperty(
        prefix = "nts.helper.location",
        name = "provider",
        havingValue = NtsRestLocationProvider.PROVIDER_NAME
    )
    public NtsRestLocationProvider ntsRestLocationProvider(
        NtsDefaultLocationCacheable locationCacheable,
        NtsHelperLocationRestClient restClient
    ) {
        return new NtsRestLocationProvider(
            locationCacheable,
            restClient
        );
    }

    @Bean
    @ConditionalOnProperty(
        prefix = "nts.helper.location",
        name = "provider",
        havingValue = NtsKeycloakLocationProvider.PROVIDER_NAME
    )
    public NtsKeycloakLocationProvider ntsKeycloakLocationProvider(
        NtsKeycloakLocationProperties keycloakLocationProperties,
        NtsDefaultLocationCacheable locationCacheable,
        NtsKeycloakClient keycloakClient
    ) {
        return new NtsKeycloakLocationProvider(
            keycloakLocationProperties,
            locationCacheable,
            keycloakClient
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public NtsLocationProvider<? extends NtsLocationDTO> ntsLocationProvider(NtsProperties ntsProperties, ApplicationContext applicationContext) {
        String provider = ntsProperties.getHelper().getLocation().getProvider();
        try {
            Class<?> clazz = provider != null ? Class.forName(provider) : NtsRestLocationProvider.class;
            return (NtsLocationProvider<? extends NtsLocationDTO>) applicationContext.getBean(clazz);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                "Configuration property nts.helper.location.provider class %s can not be loaded.",
                provider
            ));
        }
    }

    @Bean
    @ConditionalOnProperty(
        prefix = "nts.helper.location",
        name = "deserializer",
        havingValue = NtsBitSetLocationDeserializer.DESERIALIZER_NAME
    )
    public NtsBitSetLocationDeserializer ntsBitSetLocationDeserializer(
        NtsLocationProvider<? extends NtsLocationDTO> locationProvider,
        @Value("${nts.helper.location.bitset.claim:nlb}") String bitSetClaim
    ) {
        return new NtsBitSetLocationDeserializer(
            locationProvider,
            bitSetClaim
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public NtsLocationDeserializer ntsLocationDeserializer(NtsProperties ntsProperties, ApplicationContext applicationContext) {
        String deserializer = ntsProperties.getHelper().getLocation().getDeserializer();
        try {
            Class<?> clazz = deserializer != null ? Class.forName(deserializer) : NtsBitSetLocationDeserializer.class;
            return (NtsLocationDeserializer) applicationContext.getBean(clazz);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                "Configuration property nts.helper.location.deserializer class %s can not be loaded.",
                deserializer
            ));
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public NtsLocationHelper ntsLocationHelper(
        NtsLocationProvider<? extends NtsLocationDTO> locationProvider,
        NtsLocationDeserializer locationDeserializer
    ) {
        return new NtsDefaultLocationHelper(locationProvider, locationDeserializer);
    }
}
