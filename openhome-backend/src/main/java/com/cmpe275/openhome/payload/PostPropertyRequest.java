package com.cmpe275.openhome.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class PostPropertyRequest {
    @NotBlank
    String propertyContact;

    @NotBlank
    String streetAddress;

    String unitNumber;

    @NotBlank
    String city;

    @NotBlank
    String state;

    @NotBlank
    String zipCode;

    @NotBlank
    String headline;

    @NotBlank
    String description;

    @NotBlank
    String sharingType;

    @NotBlank
    String propertyType;

    @NotBlank
    String bedrooms;

    @NotBlank
    String sqft;

    String privateBathShowerAvailable;

    String privateBathroomAvailable;

    @NotBlank
    String freeWifi;

    @NotBlank
    String parkingAvailable;

    String parkingFree;

    String parkingCost;

    @NotBlank
    String[] photos;

    @NotBlank
    String alwaysAvailable;

    @NotBlank
    String weeklyAvailability;

    String weekdayRentPrice;

    String weekendRentPrice;

    public String getPropertyContact() {
        return propertyContact;
    }

    public void setPropertyContact(String propertyContact) {
        this.propertyContact = propertyContact;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public String getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(String bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getSqft() {
        return sqft;
    }

    public void setSqft(String sqft) {
        this.sqft = sqft;
    }

    public String getPrivateBathShowerAvailable() {
        return privateBathShowerAvailable;
    }

    public void setPrivateBathShowerAvailable(String privateBathShowerAvailable) {
        this.privateBathShowerAvailable = privateBathShowerAvailable;
    }

    public String getPrivateBathroomAvailable() {
        return privateBathroomAvailable;
    }

    public void setPrivateBathroomAvailable(String privateBathroomAvailable) {
        this.privateBathroomAvailable = privateBathroomAvailable;
    }

    public String getFreeWifi() {
        return freeWifi;
    }

    public void setFreeWifi(String freeWifi) {
        this.freeWifi = freeWifi;
    }

    public String getParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(String parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public String getParkingFree() {
        return parkingFree;
    }

    public void setParkingFree(String parkingFree) {
        this.parkingFree = parkingFree;
    }

    public String getParkingCost() {
        return parkingCost;
    }

    public void setParkingCost(String parkingCost) {
        this.parkingCost = parkingCost;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public String getWeeklyAvailability() {
        return weeklyAvailability;
    }

    public void setWeeklyAvailability(String weeklyAvailability) {
        this.weeklyAvailability = weeklyAvailability;
    }

    public String getWeekdayRentPrice() {
        return weekdayRentPrice;
    }

    public void setWeekdayRentPrice(String weekdayRentPrice) {
        this.weekdayRentPrice = weekdayRentPrice;
    }

    public String getWeekendRentPrice() {
        return weekendRentPrice;
    }

    public void setWeekendRentPrice(String weekendRentPrice) {
        this.weekendRentPrice = weekendRentPrice;
    }

    public String getAlwaysAvailable() {
        return alwaysAvailable;
    }

    public void setAlwaysAvailable(String alwaysAvailable) {
        this.alwaysAvailable = alwaysAvailable;
    }

}
