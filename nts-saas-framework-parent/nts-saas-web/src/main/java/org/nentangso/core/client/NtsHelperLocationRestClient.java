package org.nentangso.core.client;

import org.nentangso.core.service.dto.NtsDefaultLocationDTO;
import org.nentangso.core.service.helper.location.NtsRestLocationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@ConditionalOnProperty(
    prefix = "nts.helper.location",
    name = "provider",
    havingValue = NtsRestLocationProvider.PROVIDER_NAME
)
@NtsAuthorizedFeignClient(
    name = "nts-helper-location-rest",
    url = "${nts.helper.location.rest.api-base-url:http://localhost:8080}"
)
public interface NtsHelperLocationRestClient {
    @GetMapping(path = "${nts.helper.location.rest.get-locations.uri:/api/locations}")
    ResponseEntity<List<NtsDefaultLocationDTO>> findAll();
}
