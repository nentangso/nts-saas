package org.nentangso.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nts")
public class NtsProperties {
    private final HelperProperties helper = new HelperProperties();
    private final ClientProperties client = new ClientProperties();

    public HelperProperties getHelper() {
        return helper;
    }

    public ClientProperties getClient() {
        return client;
    }

    public static class HelperProperties {
        private final LocationProperties location = new LocationProperties();

        public LocationProperties getLocation() {
            return location;
        }

        public static class LocationProperties {
            private boolean enabled = false;

            private String provider;

            private String deserializer;

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

            public String getDeserializer() {
                return deserializer;
            }

            public void setDeserializer(String deserializer) {
                this.deserializer = deserializer;
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

    public static class ClientProperties {
        private final AuthorizedProperties authorized = new AuthorizedProperties();

        public AuthorizedProperties getAuthorized() {
            return authorized;
        }

        public static class AuthorizedProperties {
            private String clientRegistrationId = "nts-client-authorized";

            public String getClientRegistrationId() {
                return clientRegistrationId;
            }

            public void setClientRegistrationId(String clientRegistrationId) {
                this.clientRegistrationId = clientRegistrationId;
            }
        }
    }
}
