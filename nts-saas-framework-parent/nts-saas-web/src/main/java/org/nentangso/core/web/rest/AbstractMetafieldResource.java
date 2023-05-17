package org.nentangso.core.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.nentangso.core.domain.NtsMetafieldEntity;
import org.nentangso.core.service.dto.NtsMetafieldDTO;
import org.nentangso.core.service.errors.NtsNotFoundException;
import org.nentangso.core.service.helper.NtsJsonHelper;
import org.nentangso.core.service.helper.NtsMetafieldHelper;
import org.nentangso.core.service.mapper.NtsMetafieldMapper;
import org.nentangso.core.web.rest.utils.NtsRequestUtils;
import org.nentangso.core.web.rest.vm.MetafieldInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link NtsMetafieldEntity}.
 */
public abstract class AbstractMetafieldResource {
    private final Logger log = LoggerFactory.getLogger(AbstractMetafieldResource.class);

    private static final String ENTITY_NAME = "metafield";

    protected final NtsJsonHelper jsonHelper;
    protected final NtsMetafieldHelper metafieldHelper;
    protected final NtsMetafieldMapper metafieldMapper;

    protected AbstractMetafieldResource(NtsJsonHelper jsonHelper, NtsMetafieldHelper metafieldHelper, NtsMetafieldMapper metafieldMapper) {
        this.jsonHelper = jsonHelper;
        this.metafieldHelper = metafieldHelper;
        this.metafieldMapper = metafieldMapper;
    }

    protected abstract String getApplicationName();

    protected abstract String getOwnerResource();

    protected abstract boolean existsByOwnerId(long ownerId);

    protected abstract URI buildCreatedUri(long ownerId, long metafieldId) throws URISyntaxException;

    /**
     * {@code POST /metafields} : Create a new metafield
     *
     * @param ownerId   the owner id.
     * @param metafield the metafield to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new metafieldDTO, or with status {@code 400 (Bad Request)}.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    protected ResponseEntity<NtsMetafieldDTO> createMetafield(long ownerId, MetafieldInput metafield) throws URISyntaxException {
        log.debug("REST request to save metafield : {}", metafield);
        if (!existsByOwnerId(ownerId)) {
            throw new NtsNotFoundException();
        }
        NtsMetafieldDTO metafieldDTO = metafieldMapper.toDto(metafield, getOwnerResource(), ownerId);
        NtsMetafieldDTO result = metafieldHelper.save(metafieldDTO);
        return ResponseEntity
            .created(buildCreatedUri(ownerId, result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(getApplicationName(), true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /metafields/:id} : Updates an existing metafield.
     *
     * @param ownerId   the owner id.
     * @param id        the id of the metafield to save.
     * @param metafield the metafield to update.
     * @param request   the http servlet request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated metafield,
     * or with status {@code 400 (Bad Request)} if the customerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the metafield couldn't be updated.
     * @throws IOException if the Location URI syntax is incorrect.
     */
    protected ResponseEntity<NtsMetafieldDTO> updateMetafield(long ownerId, long id, MetafieldInput metafield, HttpServletRequest request) throws IOException {
        log.debug("REST request to update metafield : {}", metafield);
        if (!existsByOwnerId(ownerId)) {
            throw new NtsNotFoundException();
        }
        NtsMetafieldDTO.Builder builder = metafieldHelper.findOne(getOwnerResource(), ownerId, id)
            .map(NtsMetafieldDTO::newBuilder)
            .orElseThrow(NtsNotFoundException::new);

        String body = NtsRequestUtils.getBody(request);
        JsonNode node = jsonHelper.getJsonNode(body, null);

        NtsMetafieldDTO metafieldDTO = builder
            .namespaceIf(jsonHelper.existField(node, "namespace"), metafield::getNamespace)
            .keyIf(jsonHelper.existField(node, "key"), metafield::getKey)
            .valueIf(jsonHelper.existField(node, "value"), metafield::getValue)
            .typeIf(jsonHelper.existField(node, "type"), metafield::getType)
            .descriptionIf(jsonHelper.existField(node, "description"), metafield::getDescription)
            .build();

        NtsMetafieldDTO result = metafieldHelper.save(metafieldDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(getApplicationName(), true, ENTITY_NAME, String.valueOf(id)))
            .body(result);
    }

    /**
     * {@code GET /metafields} : get all metafields
     *
     * @param ownerId the owner id
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of metafields in body.
     */
    protected ResponseEntity<List<NtsMetafieldDTO>> getAllMetafields(long ownerId) {
        log.debug("REST request to get metafields");
        if (!existsByOwnerId(ownerId)) {
            throw new NtsNotFoundException();
        }
        List<NtsMetafieldDTO> metafields = metafieldHelper.findAllByOwner(getOwnerResource(), ownerId);
        return ResponseEntity.ok(metafields);
    }

    /**
     * {@code GET  /metafields/count} : count all the metafields.
     *
     * @param ownerId the owner id
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    protected ResponseEntity<Long> countMetafields(long ownerId) {
        log.debug("REST request to count metafields");
        long count = metafieldHelper.count(getOwnerResource(), ownerId);
        return ResponseEntity.ok().body(count);
    }

    /**
     * {@code GET  /metafields/:id} : get the "id" metafield.
     *
     * @param ownerId the owner id
     * @param id      the id of the metafield to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the metafield, or with status {@code 404 (Not Found)}.
     */
    protected ResponseEntity<NtsMetafieldDTO> getMetafield(long ownerId, long id) {
        log.debug("REST request to get metafield : {}", id);
        Optional<NtsMetafieldDTO> metafieldDTO = metafieldHelper.findOne(getOwnerResource(), ownerId, id);
        return ResponseUtil.wrapOrNotFound(metafieldDTO);
    }

    /**
     * {@code DELETE  /metafields/:id} : delete the "id" metafields.
     *
     * @param ownerId the owner id
     * @param id      the id of the metafields to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    protected ResponseEntity<Void> deleteMetafield(long ownerId, long id) {
        log.debug("REST request to delete metafield : {}", id);
        metafieldHelper.delete(getOwnerResource(), ownerId, id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(getApplicationName(), true, ENTITY_NAME, String.valueOf(id)))
            .build();
    }
}
