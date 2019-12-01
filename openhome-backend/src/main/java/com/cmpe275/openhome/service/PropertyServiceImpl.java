package com.cmpe275.openhome.service;

import com.cmpe275.openhome.entity.PropertyDetails;
import com.cmpe275.openhome.exception.ResourceNotFoundException;
import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.payload.SearchProperty;
import com.cmpe275.openhome.payload.SearchRequest;
import com.cmpe275.openhome.repository.PropertyRepository;
import com.cmpe275.openhome.repository.PropertyRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private PropertyRepositoryCustom propertyRepositoryCustom;

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
	public Property hostProperty(Property property) {
		Property savedProperty = propertyRepository.save(property);
		return savedProperty;
	}

	@Override
	public List<SearchProperty> searchProperties(SearchRequest searchRequest) {
	  	List<Property> properties =  propertyRepositoryCustom.findProperties(searchRequest);
	  	List<SearchProperty> searchProperties = new ArrayList<>();
	  	for (Property p : properties) {
	  		String imagesString = p.getPhotosArrayJson();
	  		String[] images = imagesString.split(",");
	  		String imageUrl = "";
	  		if (images.length>0) {
	  			imageUrl = images[0].replace("[", "").replace("\"","").replace("]", "");
			}
			SearchProperty sp = new SearchProperty(imageUrl,p.getId(), p.getHeadline(), p.getAddressStreet(), p.getAddressCity(), p.getAddressCity(), p.getAddressZipcode(), p.getWeekdayPrice(), p.getWeekendPrice());
			searchProperties.add(sp);
	  	}
	  	return searchProperties;
	}

	@Override
	public Property getProperty(String propertyId) {
		return propertyRepository.findById(Long.parseLong(propertyId))
				.orElseThrow(() -> new ResourceNotFoundException("Property", "id", propertyId));
	}
}
