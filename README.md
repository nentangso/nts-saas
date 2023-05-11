# nts-saas
Software as a Service power by nentangso.org

### SaaS Framework power by nentangso.org

Modules:

- nts-saas-dependencies
- nts-saas-web-core
- nts-saas-web
- nts-saas-webflux
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
