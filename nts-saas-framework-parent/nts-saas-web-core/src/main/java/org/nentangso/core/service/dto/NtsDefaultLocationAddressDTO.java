package org.nentangso.core.service.dto;

import java.io.Serializable;

/**
 * Represents the address of a location.
 */
public class NtsDefaultLocationAddressDTO implements NtsAddressDTO, Serializable {
    private static final long serialVersionUID = 1L;

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

    public NtsDefaultLocationAddressDTO() {
    }

    public NtsDefaultLocationAddressDTO(NtsLocationDTOBuilder builder) {
        setPhone(builder.getPhone());
        setAddress1(builder.getAddress1());
        setAddress2(builder.getAddress2());
        setCountry(builder.getCountry());
        setCountryCode(builder.getCountryCode());
        setLocalizedCountryName(builder.getLocalizedCountryName());
        setCity(builder.getCity());
        setProvince(builder.getProvince());
        setProvinceCode(builder.getProvinceCode());
        setLocalizedProvinceName(builder.getLocalizedProvinceName());
        setZip(builder.getZip());
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLocalizedCountryName() {
        return localizedCountryName;
    }

    public void setLocalizedCountryName(String localizedCountryName) {
        this.localizedCountryName = localizedCountryName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getLocalizedProvinceName() {
        return localizedProvinceName;
    }

    public void setLocalizedProvinceName(String localizedProvinceName) {
        this.localizedProvinceName = localizedProvinceName;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationAddressDTO{" +
            "phone='" + phone + '\'' +
            ", address1='" + address1 + '\'' +
            ", address2='" + address2 + '\'' +
            ", country='" + country + '\'' +
            ", countryCode='" + countryCode + '\'' +
            ", localizedCountryName='" + localizedCountryName + '\'' +
            ", city='" + city + '\'' +
            ", province='" + province + '\'' +
            ", provinceCode='" + provinceCode + '\'' +
            ", localizedProvinceName='" + localizedProvinceName + '\'' +
            ", zip='" + zip + '\'' +
            '}';
    }
}
