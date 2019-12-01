package com.cmpe275.openhome.payload;

public class SearchProperty {

    private String imageUrl;
    private long id;
    private String headline;
    private String street;
    private String city;
    private String state;
    private long zip;
    private double weekdayPrice;
    private double weekendPrice;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public long getZip() {
        return zip;
    }

    public void setZip(long zip) {
        this.zip = zip;
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


    public SearchProperty(String imageUrl, long id, String headline, String street, String city, String state, long zip, double weekdayPrice, double weekendPrice) {
        this.imageUrl = imageUrl;
        this.id = id;
        this.headline = headline;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.weekdayPrice = weekdayPrice;
        this.weekendPrice = weekendPrice;
    }
}
