package org.nentangso.core.service.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * A location represents a geographical location where your stores, pop-up stores, headquarters, and warehouses exist.
 * You can use the Location resource to track sales, manage inventory, and configure the tax rates to apply at checkout.
 */
public class LocationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The ID of the location.
     */
    private Long id;
    /**
     * The name of the location.
     */
    private String name;
    /**
     * The address of this location.
     */
    private LocationAddressDTO address;
    /**
     * Whether the location address has been verified.
     */
    private boolean addressVerified;
    /**
     * Whether the location is active. If {@code true}, then the location can be used to sell products, stock inventory, and fulfill orders. Merchants can deactivate locations from the Shopify admin. Deactivated locations don't contribute to the shop's location limit.
     */
    private boolean active;
    /**
     * The date and time (ISO 8601 format) when the location was created.
     */
    private Instant createdAt;
    /**
     * The date and time (ISO 8601 format) when the location was last updated.
     */
    private Instant updatedAt;
    /**
     * The date and time (ISO 8601 format) that the location was deactivated at. For example, 3:30 pm on September 7, 2019 in the time zone of UTC (Universal Time Coordinated) is represented as "2019-09-07T15:50:00Z".
     */
    private Instant deactivatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationAddressDTO getAddress() {
        return address;
    }

    public void setAddress(LocationAddressDTO address) {
        this.address = address;
    }

    public boolean isAddressVerified() {
        return addressVerified;
    }

    public void setAddressVerified(boolean addressVerified) {
        this.addressVerified = addressVerified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(Instant deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", address=" + address +
            ", addressVerified=" + addressVerified +
            ", active=" + active +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            ", deactivatedAt=" + deactivatedAt +
            '}';
    }
}
