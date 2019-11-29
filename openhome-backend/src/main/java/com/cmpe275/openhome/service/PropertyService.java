package com.cmpe275.openhome.service;

import java.util.List;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.entity.PropertyDetails;

public interface PropertyService {
	List<Property> getHardcodedPropertyList();
	PropertyDetails getHardcodedPropertyDetails();

	Property hostProperty(Property property);
}
