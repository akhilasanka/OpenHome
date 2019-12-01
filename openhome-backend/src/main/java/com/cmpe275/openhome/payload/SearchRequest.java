package com.cmpe275.openhome.payload;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class SearchRequest {
    @Override
    public String toString() {
        return "SearchRequest{" +
                "city='" + city + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", sharingType='" + sharingType + '\'' +
                ", propertyType='" + propertyType + '\'' +
                ", internet='" + internet + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", desc='" + desc + '\'' +
                '}';
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
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

    public String getInternet() {
        return internet;
    }

    public void setInternet(String internet) {
        this.internet = internet;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String city;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date from;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date to;

    private String sharingType;

    public SearchRequest(String city, Date from, Date to, String sharingType, String propertyType, String internet, double minPrice, double maxPrice, String desc) {
        this.city = city;
        this.from = from;
        this.to = to;
        this.sharingType = sharingType;
        this.propertyType = propertyType;
        this.internet = internet;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.desc = desc;
    }

    private String propertyType;

    private String internet;

    private double minPrice;

    private double maxPrice;

    private String desc;
}
