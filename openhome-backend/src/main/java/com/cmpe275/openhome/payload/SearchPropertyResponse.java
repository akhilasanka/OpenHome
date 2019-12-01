package com.cmpe275.openhome.payload;

import com.cmpe275.openhome.model.Property;

import java.util.List;

public class SearchPropertyResponse {

    private List<SearchProperty> properties;


    public SearchPropertyResponse(List<SearchProperty> properties) {
        this.properties = properties;
    }

    public List<SearchProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SearchProperty> properties) {
        this.properties = properties;
    }
}
