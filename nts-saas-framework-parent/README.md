# Software as a Service framework

### Scripts

Release new version, upload to github

```shell
./mvnw -B release:clean release:prepare release:perform -Pdeploy-github
```

Deploy current version

```shell
./mvnw -B clean deploy -Pdeploy-github
```
