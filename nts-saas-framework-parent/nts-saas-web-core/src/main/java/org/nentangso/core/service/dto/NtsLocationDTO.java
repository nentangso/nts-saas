package org.nentangso.core.service.dto;

import java.time.Instant;
import java.util.List;

/**
 * A location represents a geographical location where your stores, pop-up stores, headquarters, and warehouses exist.
 * You can use the Location resource to track sales, manage inventory, and configure the tax rates to apply at checkout.
 */
public interface NtsLocationDTO {
    /**
     * The ID of the location.
     *
     * @return the id
     */
    default Long getId() {
        return null;
    }

    /**
     * The name of the location.
     *
     * @return the name
     */
    default String getName() {
        return null;
    }

    /**
     * The address of this location.
     *
     * @return the address
     */
    default NtsAddressDTO getAddress() {
        return null;
    }

    /**
     * Whether the location address has been verified.
     *
     * @return the address
     */
    default boolean isAddressVerified() {
        return false;
    }

    /**
     * Whether the location is active. If {@code true}, then the location can be used to sell products, stock inventory, and fulfill orders. Merchants can deactivate locations from the Shopify admin. Deactivated locations don't contribute to the shop's location limit.
     *
     * @return the location status
     */
    default boolean isActive() {
        return false;
    }

    /**
     * The date and time (ISO 8601 format) when the location was created.
     *
     * @return the created at
     */
    default Instant getCreatedAt() {
        return null;
    }

    /**
     * The date and time (ISO 8601 format) when the location was last updated.
     *
     * @return the updated at
     */
    default Instant getUpdatedAt() {
        return null;
    }

    /**
     * The date and time (ISO 8601 format) that the location was deactivated at. For example, 3:30 pm on September 7, 2019 in the time zone of UTC (Universal Time Coordinated) is represented as "2019-09-07T15:50:00Z".
     *
     * @return the deactivated at
     */
    default Instant getDeactivatedAt() {
        return null;
    }

    /**
     * Represents a generic custom attribute.
     *
     * @param <A> the implementation of attribute
     * @return the list of attributes
     */
    default <A extends NtsAttributeDTO> List<A> getCustomAttributes() {
        return null;
    }

    /**
     * The builder
     *
     * @return the builder
     */
    static NtsLocationDTOBuilder newDefaultBuilder() {
        return new NtsLocationDTOBuilder();
    }
}
