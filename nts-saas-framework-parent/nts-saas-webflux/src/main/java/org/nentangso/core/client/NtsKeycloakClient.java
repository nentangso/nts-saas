package org.nentangso.core.client;

import org.nentangso.core.client.vm.KeycloakClientRole;
import org.nentangso.core.service.helper.location.NtsKeycloakLocationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.List;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "provider",
    havingValue = NtsKeycloakLocationProvider.PROVIDER_NAME
)
@ReactiveFeignClient(
    name = "nts-helper-location-keycloak",
    configuration = NtsKeycloakFeignConfiguration.class,
    url = "${nts.helper.location.keycloak.admin-base-url:http://localhost:8080/admin/realms/master}"
)
public interface NtsKeycloakClient {
    @GetMapping("/clients/{clientId}/roles")
    Mono<List<KeycloakClientRole>> findClientRoles(
        @PathVariable("clientId") String internalClientId,
        @RequestParam("briefRepresentation") Boolean briefRepresentation
    );

    @GetMapping("/clients/{clientId}/roles/{roleName}")
    Mono<KeycloakClientRole> findClientRole(
        @PathVariable("clientId") String internalClientId,
        @PathVariable("roleName") String roleName
    );

    @PostMapping("/clients/{clientId}/roles")
    Mono<Void> createClientRole(@PathVariable("clientId") String internalClientId, @RequestBody KeycloakClientRole keycloakClientRole);

    @PutMapping("/clients/{clientId}/roles/{roleName}")
    Mono<Void> updateClientRole(
            @PathVariable("clientId") String internalClientId,
            @PathVariable("roleName") String roleName,
            @RequestBody KeycloakClientRole keycloakClientRole
    );
}
