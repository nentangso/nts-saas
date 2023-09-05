package org.nentangso.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nts")
public class NtsProperties {
    private final SecurityProperties security = new SecurityProperties();
    private final HelperProperties helper = new HelperProperties();
    private final ClientProperties client = new ClientProperties();

    public SecurityProperties getSecurity() {
        return security;
    }

    public HelperProperties getHelper() {
        return helper;
    }

    public ClientProperties getClient() {
        return client;
    }

    public static class SecurityProperties {
        private final OAuth2Properties oauth2 = new OAuth2Properties();

        public OAuth2Properties getOauth2() {
            return oauth2;
        }

        public static class OAuth2Properties {
            private String rolesClaim = "roles";
            private String rolePrefix = "ROLE_";
            private boolean reverseOrderOfDisplayName = true;

            public String getRolesClaim() {
                return rolesClaim;
            }

            public void setRolesClaim(String rolesClaim) {
                this.rolesClaim = rolesClaim;
            }

            public String getRolePrefix() {
                return rolePrefix;
            }

            public void setRolePrefix(String rolePrefix) {
                this.rolePrefix = rolePrefix;
            }

            public boolean isReverseOrderOfDisplayName() {
                return reverseOrderOfDisplayName;
            }

            public void setReverseOrderOfDisplayName(boolean reverseOrderOfDisplayName) {
                this.reverseOrderOfDisplayName = reverseOrderOfDisplayName;
            }
        }
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
                private Long expiration = 3600L;

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

                public Long getExpiration() {
                    return expiration;
                }

                public void setExpiration(Long expiration) {
                    this.expiration = expiration;
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
