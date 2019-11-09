package com.cmpe275.openhome.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cmpe275.openhome.entity.Property;

@Service
public class PropertyServiceImpl implements PropertyService {
	
	private static List<Property> myHardcodedPropertyList = new ArrayList<Property>();

	  static {
		  Property property1 = new Property();
		  property1.setId(1L);
		  property1.setOwner("foo");
		  
		  Property property2 = new Property();
		  property2.setId(2L);
		  property2.setOwner("foo");

		  Property property3 = new Property();
		  property3.setId(3L);
		  property3.setOwner("foo");

		  Property property4 = new Property();
		  property4.setId(4L);
		  property4.setOwner("foo");
		  
		  myHardcodedPropertyList.add(property1);
		  myHardcodedPropertyList.add(property2);
		  myHardcodedPropertyList.add(property3);
		  myHardcodedPropertyList.add(property4);
	  }

	public List<Property> getHardcodedPropertyList() {
		// method for testing
		return myHardcodedPropertyList;
	}
}
