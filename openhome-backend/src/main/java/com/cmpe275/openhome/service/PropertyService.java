package com.cmpe275.openhome.service;

import java.util.List;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.entity.PropertyDetails;
import com.cmpe275.openhome.payload.SearchProperty;
import com.cmpe275.openhome.payload.SearchRequest;

public interface PropertyService {
	List<Property> getHardcodedPropertyList();

	Property hostProperty(Property property);

	Boolean editProperty(Property property, Boolean isApprovedForPayingFine) throws Exception;

	Boolean deleteProperty(Property property, Boolean isApprovedForPayingFine) throws Exception;

	List<SearchProperty> searchProperties(SearchRequest searchRequest);

	Property getProperty(String propertyId);
}
