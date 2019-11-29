package com.cmpe275.openhome.service;

import java.util.ArrayList;
import java.util.List;

import com.cmpe275.openhome.entity.PropertyDetails;
import org.springframework.stereotype.Service;

import com.cmpe275.openhome.model.Property;

@Service
public class PropertyServiceImpl implements PropertyService {
	
	private static List<Property> myHardcodedPropertyList = new ArrayList<Property>();
	private static PropertyDetails myHardcodedPropertyDetails = new PropertyDetails();

	  static {
		  Property property1 = new Property();
		  property1.setId(1L);
		  property1.setHeadline("foo");
		  
		  Property property2 = new Property();
		  property2.setId(2L);
		  property2.setHeadline("foo");

		  Property property3 = new Property();
		  property3.setId(3L);
		  property3.setHeadline("foo");

		  Property property4 = new Property();
		  property4.setId(4L);
		  property4.setHeadline("foo");
		  
		  myHardcodedPropertyList.add(property1);
		  myHardcodedPropertyList.add(property2);
		  myHardcodedPropertyList.add(property3);
		  myHardcodedPropertyList.add(property4);

		  myHardcodedPropertyDetails.setMyId(1L);
		  myHardcodedPropertyDetails.setOwnerId(1L);
	  }

	public List<Property> getHardcodedPropertyList() {
		// method for testing
		return myHardcodedPropertyList;
	}

	@Override
	public PropertyDetails getHardcodedPropertyDetails() {
		return null;
	}

	@Override
	public Property hostProperty(Property property) {
		return null;
	}
}
