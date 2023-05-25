package org.nentangso.core.service.dto;

import java.time.Instant;
import java.util.List;

public class NtsLocationDTOBuilder {
    /**
     * The ID of the location.
     */
    private Long id;
    /**
     * The name of the location.
     */
    private String name;
    /**
     * The phone number of the location. This value can contain special characters, such as {@code -} or {@code +}.
     */
    private String phone;
    /**
     * The location's street address.
     */
    private String address1;
    /**
     * The optional second line of the location's street address.
     */
    private String address2;
    /**
     * The country the location is in.
     */
    private String country;
    /**
     * The two-letter code (ISO 3166-1 alpha-2 format) corresponding to country the location is in.
     */
    private String countryCode;
    /**
     * The localized name of the location's country.
     */
    private String localizedCountryName;
    /**
     * The city the location is in.
     */
    private String city;
    /**
     * The province, state, or district of the location.
     */
    private String province;
    /**
     * The province, state, or district code (ISO 3166-2 alpha-2 format) of the location.
     */
    private String provinceCode;
    /**
     * The localized name of the location's region. Typically a province, state, or district.
     */
    private String localizedProvinceName;
    /**
     * The zip or postal code.
     */
    private String zip;
    /**
     * Whether the location address has been verified.
     */
    private boolean addressVerified;
    /**
     * Whether the location is active. If {@code true}, then the location can be used to sell products, stock inventory, and fulfill orders. Merchants can deactivate locations from the Shopify admin. Deactivated locations don't contribute to the shop's location limit.
     */
    private boolean active;
    /**
     * The date and time (ISO 8601 format) that the location was deactivated at. For example, 3:30 pm on September 7, 2019 in the time zone of UTC (Universal Time Coordinated) is represented as "2019-09-07T15:50:00Z".
     */
    private Instant deactivatedAt;
    /**
     * The date and time (ISO 8601 format) when the location was created.
     */
    private Instant createdAt;
    /**
     * The date and time (ISO 8601 format) when the location was last updated.
     */
    private Instant updatedAt;
    /**
     * The custom information added to the location on behalf of the customer.
     */
    private List<NtsDefaultAttributeDTO> customAttributes;

    public Long getId() {
        return id;
    }

    public NtsLocationDTOBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public NtsLocationDTOBuilder name(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public NtsLocationDTOBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAddress1() {
        return address1;
    }

    public NtsLocationDTOBuilder address1(String address1) {
        this.address1 = address1;
        return this;
    }

    public String getAddress2() {
        return address2;
    }

    public NtsLocationDTOBuilder address2(String address2) {
        this.address2 = address2;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public NtsLocationDTOBuilder country(String country) {
        this.country = country;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public NtsLocationDTOBuilder countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getLocalizedCountryName() {
        return localizedCountryName;
    }

    public NtsLocationDTOBuilder localizedCountryName(String localizedCountryName) {
        this.localizedCountryName = localizedCountryName;
        return this;
    }

    public String getCity() {
        return city;
    }

    public NtsLocationDTOBuilder city(String city) {
        this.city = city;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public NtsLocationDTOBuilder province(String province) {
        this.province = province;
        return this;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public NtsLocationDTOBuilder provinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
        return this;
    }

    public String getLocalizedProvinceName() {
        return localizedProvinceName;
    }

    public NtsLocationDTOBuilder localizedProvinceName(String localizedProvinceName) {
        this.localizedProvinceName = localizedProvinceName;
        return this;
    }

    public String getZip() {
        return zip;
    }

    public NtsLocationDTOBuilder zip(String zip) {
        this.zip = zip;
        return this;
    }

    public boolean isAddressVerified() {
        return addressVerified;
    }

    public NtsLocationDTOBuilder addressVerified(boolean addressVerified) {
        this.addressVerified = addressVerified;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public NtsLocationDTOBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    public Instant getDeactivatedAt() {
        return deactivatedAt;
    }

    public NtsLocationDTOBuilder deactivatedAt(Instant deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public NtsLocationDTOBuilder createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public NtsLocationDTOBuilder updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public List<NtsDefaultAttributeDTO> getCustomAttributes() {
        return customAttributes;
    }

    public NtsLocationDTOBuilder customAttributes(List<NtsDefaultAttributeDTO> customAttributes) {
        this.customAttributes = customAttributes;
        return this;
    }

    public NtsDefaultLocationDTO build() {
        return new NtsDefaultLocationDTO(this);
    }
}
