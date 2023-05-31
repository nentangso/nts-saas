package org.nentangso.core.client;

import org.nentangso.core.client.vm.KeycloakClientRole;
import org.nentangso.core.service.helper.location.NtsKeycloakLocationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "provider",
    havingValue = NtsKeycloakLocationProvider.PROVIDER_NAME
)
@FeignClient(
    name = "nts-helper-location-keycloak",
    configuration = NtsKeycloakFeignConfiguration.class,
    url = "${nts.helper.location.keycloak.admin-base-url:http://localhost:8080/admin/realms/master}"
)
public interface NtsKeycloakClient {
    @GetMapping("/clients/{clientId}/roles")
    ResponseEntity<List<KeycloakClientRole>> findClientRoles(
        @PathVariable("clientId") String internalClientId,
        @RequestParam("briefRepresentation") Boolean briefRepresentation
    );

    @GetMapping("/clients/{clientId}/roles/{roleName}")
    ResponseEntity<KeycloakClientRole> findClientRole(
        @PathVariable("clientId") String internalClientId,
        @PathVariable("roleName") String roleName
    );
}
