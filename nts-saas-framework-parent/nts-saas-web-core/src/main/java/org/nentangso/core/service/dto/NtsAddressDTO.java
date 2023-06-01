package org.nentangso.core.service.dto;

/**
 * Represents the address of a location.
 */
public interface NtsAddressDTO {
    /**
     * The phone number of the location. This value can contain special characters, such as {@code -} or {@code +}.
     *
     * @return the phone number
     */
    default String getPhone() {
        return null;
    }

    /**
     * The location's street address.
     *
     * @return the address
     */
    default String getAddress1() {
        return null;
    }

    /**
     * The optional second line of the location's street address.
     *
     * @return the address
     */
    default String getAddress2() {
        return null;
    }

    /**
     * The country the location is in.
     *
     * @return the country
     */
    default String getCountry() {
        return null;
    }

    /**
     * The two-letter code (ISO 3166-1 alpha-2 format) corresponding to country the location is in.
     *
     * @return the country code
     */
    default String getCountryCode() {
        return null;
    }

    /**
     * The localized name of the location's country.
     *
     * @return the country name
     */
    default String getLocalizedCountryName() {
        return null;
    }

    /**
     * The city the location is in.
     *
     * @return the city
     */
    default String getCity() {
        return null;
    }

    /**
     * The province, state, or district of the location.
     *
     * @return the province
     */
    default String getProvince() {
        return null;
    }

    /**
     * The province, state, or district code (ISO 3166-2 alpha-2 format) of the location.
     *
     * @return the province code
     */
    default String getProvinceCode() {
        return null;
    }

    /**
     * The localized name of the location's region. Typically a province, state, or district.
     *
     * @return the province name
     */
    default String getLocalizedProvinceName() {
        return null;
    }

    /**
     * The zip or postal code.
     *
     * @return the zip
     */
    default String getZip() {
        return null;
    }
}
