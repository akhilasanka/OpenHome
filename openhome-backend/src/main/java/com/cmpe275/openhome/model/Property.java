package com.cmpe275.openhome.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "property")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "address_street")
    private String addressStreet;

    @Column(name = "address_city")
    private String addressCity;

    @Column(name = "address_state")
    private String addressState;

    @Column(name = "address_zipcode")
    private Long addressZipcode;

    @Column(name = "sharing_type")
    private String sharingType;

    @Column(name = "property_type")
    private String propertyType;

    @Column(name = "num_bedroom")
    private Long numBedroom;

    @Column(name = "square_footage")
    private Long squareFootage;

    @Column(name = "has_private_bathroom")
    private String hasPrivateBathroom;

    @Column(name = "has_private_shower")
    private String hasPrivateShower;

    @Column(name = "weekday_price")
    double weekdayPrice;

    @Column(name = "weekend_price")
    double weekendPrice;

    @Column(name = "phone_number")
    private Long phoneNumber;

    @Column(name = "headline")
    private String headline;

    @Column(name = "description")
    private String description;

    @Column(name = "parking_availability")
    private String parkingAvailability;

    @Column(name = "daily_parking_fee")
    double dailyParkingFee;

    @Column(name = "wifi_availability")
    private String wifiAvailability;

    @Column(name = "additional_features")
    private String additionalFeatures;

    @Column(name = "available_days")
    private String availableDays;

    @Column(name = "photos")
    private String photosArrayJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public Long getAddressZipcode() {
        return addressZipcode;
    }

    public void setAddressZipcode(Long addressZipcode) {
        this.addressZipcode = addressZipcode;
    }

    public String getSharingType() {
        return sharingType;
    }

    public void setSharingType(String sharingType) {
        this.sharingType = sharingType;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Long getNumBedroom() {
        return numBedroom;
    }

    public void setNumBedroom(Long numBedroom) {
        this.numBedroom = numBedroom;
    }

    public Long getSquareFootage() {
        return squareFootage;
    }

    public void setSquareFootage(Long squareFootage) {
        this.squareFootage = squareFootage;
    }

    public String getHasPrivateBathroom() {
        return hasPrivateBathroom;
    }

    public void setHasPrivateBathroom(String hasPrivateBathroom) {
        this.hasPrivateBathroom = hasPrivateBathroom;
    }

    public String getHasPrivateShower() {
        return hasPrivateShower;
    }

    public void setHasPrivateShower(String hasPrivateShower) {
        this.hasPrivateShower = hasPrivateShower;
    }

    public double getWeekdayPrice() {
        return weekdayPrice;
    }

    public void setWeekdayPrice(double weekdayPrice) {
        this.weekdayPrice = weekdayPrice;
    }

    public double getWeekendPrice() {
        return weekendPrice;
    }

    public void setWeekendPrice(double weekendPrice) {
        this.weekendPrice = weekendPrice;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParkingAvailability() {
        return parkingAvailability;
    }

    public void setParkingAvailability(String parkingAvailability) {
        this.parkingAvailability = parkingAvailability;
    }

    public double getDailyParkingFee() {
        return dailyParkingFee;
    }

    public void setDailyParkingFee(double dailyParkingFee) {
        this.dailyParkingFee = dailyParkingFee;
    }

    public String getWifiAvailability() {
        return wifiAvailability;
    }

    public void setWifiAvailability(String wifiAvailability) {
        this.wifiAvailability = wifiAvailability;
    }

    public String getAdditionalFeatures() {
        return additionalFeatures;
    }

    public void setAdditionalFeatures(String additionalFeatures) {
        this.additionalFeatures = additionalFeatures;
    }

    public String getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(String availableDays) {
        this.availableDays = availableDays;
    }

    public String getPhotosArrayJson() {
        return photosArrayJson;
    }

    public void setPhotosArrayJson(String photosArrayJson) {
        this.photosArrayJson = photosArrayJson;
    }

    @Override
    public String toString() {
        return "SearchRequest{" +
                "city='" + addressCity + '\'' +

                ", sharingType='" + sharingType + '\'' +
                ", propertyType='" + propertyType + '\'' +
                ", internet='" + wifiAvailability + '\'' +
                ", minPrice=" + weekdayPrice +
                ", maxPrice=" + weekendPrice +
                ", zipCode='" + addressZipcode + '\'' +
                '}';
    }
}
