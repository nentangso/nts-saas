package org.nentangso.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nts")
public class NtsProperties {
    private final HelperProperties helper = new HelperProperties();

    public HelperProperties getHelper() {
        return helper;
    }

    public static class HelperProperties {
        private final LocationProperties location = new LocationProperties();

        public LocationProperties getLocation() {
            return location;
        }

        public static class LocationProperties {
            private boolean enabled = false;

            private String provider;

            private final CacheProperties cache = new CacheProperties();

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getProvider() {
                return provider;
            }

            public void setProvider(String provider) {
                this.provider = provider;
            }

            public CacheProperties getCache() {
                return cache;
            }

            public static class CacheProperties {
                private boolean enabled = true;
                private String keyPrefix = "nts:helper:location:";

                public boolean isEnabled() {
                    return enabled;
                }

                public void setEnabled(boolean enabled) {
                    this.enabled = enabled;
                }

                public String getKeyPrefix() {
                    return keyPrefix;
                }

                public void setKeyPrefix(String keyPrefix) {
                    this.keyPrefix = keyPrefix;
                }
            }
        }
    }
}
