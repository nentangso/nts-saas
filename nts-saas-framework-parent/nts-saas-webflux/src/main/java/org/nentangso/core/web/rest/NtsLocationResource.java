package org.nentangso.core.web.rest;

import org.nentangso.core.service.dto.NtsLocationDTO;
import org.nentangso.core.service.helper.NtsLocationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
@ConditionalOnProperty(
    prefix = "nts.web.rest.location",
    name = "enabled",
    havingValue = "true"
)
public class NtsLocationResource {
    private static final Logger log = LoggerFactory.getLogger(NtsLocationResource.class);

    protected final NtsLocationHelper locationHelper;

    protected NtsLocationResource(NtsLocationHelper locationHelper) {
        this.locationHelper = locationHelper;
    }

    @GetMapping("/locations")
    public Mono<List<NtsLocationDTO>> findAll() {
        return locationHelper.findAll();
    }

    @GetMapping("/locations/{id}")
    public Mono<NtsLocationDTO> findOne(@PathVariable Long id) {
        return locationHelper.findById(id);
    }
}
