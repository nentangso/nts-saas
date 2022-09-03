# nts-saas
Software as a Service power by nentangso.org

### SaaS Framework power by nentangso.org

Modules:

- nts-saas-dependencies
- nts-saas-core
- nts-saas-annotations
- nts-saas-utils
- nts-saas-data-jpa
- nts-saas-helper
- nts-saas-security-oauth2
- nts-saas-security-oauth2-webflux
- nts-saas-security-user
- nts-saas-security-user-webflux
- nts-saas-helper-note
- nts-saas-helper-tags
- nts-saas-helper-option
- nts-saas-helper-outbox-event
- nts-saas-web-core
- nts-saas-web
- nts-saas-webflux
- nts-saas-web-metafield
- nts-saas-starter-web
- nts-saas-starter-webflux

### Scripts

Prepare to release new version

```shell
./mvnw -B release:clean release:prepare
```

Deploy current version

```shell
# Deploy to maven central
./mvnw -B clean deploy
# Deploy to github
./mvnw -B clean deploy -Dgithub=true
```
