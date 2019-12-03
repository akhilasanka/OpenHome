package com.cmpe275.openhome.service;

import com.cmpe275.openhome.entity.PropertyDetails;
import com.cmpe275.openhome.exception.ResourceNotFoundException;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.payload.SearchProperty;
import com.cmpe275.openhome.payload.SearchRequest;
import com.cmpe275.openhome.repository.PropertyRepository;
import com.cmpe275.openhome.repository.PropertyRepositoryCustom;
import com.cmpe275.openhome.repository.ReservationRepository;
import com.cmpe275.openhome.util.DateUtils;
import com.cmpe275.openhome.util.SystemDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private PropertyRepositoryCustom propertyRepositoryCustom;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private ReservationService reservationService;

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
	public Boolean editProperty(Property property, Boolean isApprovedForPayingFine) throws Exception {

		LocalDate currentDate = SystemDateTime.getCurSystemTime().toLocalDate();
		LocalDate sevenDaysFromNow = currentDate.plusDays(7);

		List<Reservation> sevenDayReservations = reservationRepository.findAllReservationsForPropertyBetweenDates(
				property.getId(),
				DateUtils.convertLocalDateToDate(currentDate),
				DateUtils.convertLocalDateToDate(sevenDaysFromNow)
		);

		LocalDate oneYearFromNow = currentDate.plusDays(365);

		List<Reservation> allReservations = reservationRepository.findAllReservationsForPropertyBetweenDates(
				property.getId(),
				DateUtils.convertLocalDateToDate(currentDate),
				DateUtils.convertLocalDateToDate(oneYearFromNow)
		);

		List<Reservation> cancelledWithPenalty = new ArrayList<>();
		List<Reservation> cancelledWithoutPenalty = new ArrayList<>();


		if (sevenDayReservations.size() > 0 && isApprovedForPayingFine) {
			for (Reservation r : sevenDayReservations) {
				cancelledWithPenalty.add(r);
				reservationService.hostCancelReservation(r);
			}
		} else if (sevenDayReservations.size() > 0 && !isApprovedForPayingFine) {
			return false;
		}

		allReservations.forEach(r -> {
			if (!sevenDayReservations.contains(r.getId())) {
				cancelledWithoutPenalty.add(r);
				try {
					reservationService.hostCancelReservation(r);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		Property savedProperty = propertyRepository.save(property);
		return true;
	}

	@Override
	public Boolean deleteProperty(Property property, Boolean isApprovedForPayingFine) throws Exception {

		LocalDate currentDate = SystemDateTime.getCurSystemTime().toLocalDate();
		LocalDate sevenDaysFromNow = currentDate.plusDays(7);

		List<Reservation> sevenDayReservations = reservationRepository.findAllReservationsForPropertyBetweenDates(
				property.getId(),
				DateUtils.convertLocalDateToDate(currentDate),
				DateUtils.convertLocalDateToDate(sevenDaysFromNow)
		);

		LocalDate oneYearFromNow = currentDate.plusDays(365);

		List<Reservation> allReservations = reservationRepository.findAllReservationsForPropertyBetweenDates(
				property.getId(),
				DateUtils.convertLocalDateToDate(currentDate),
				DateUtils.convertLocalDateToDate(oneYearFromNow)
		);

		List<Reservation> cancelledWithPenalty = new ArrayList<>();
		List<Reservation> cancelledWithoutPenalty = new ArrayList<>();


		if (sevenDayReservations.size() > 0 && isApprovedForPayingFine) {
			for (Reservation r : sevenDayReservations) {
				cancelledWithPenalty.add(r);
				reservationService.hostCancelReservation(r);
			}
		} else if (sevenDayReservations.size() > 0 && !isApprovedForPayingFine) {
			return false;
		}

		allReservations.forEach(r -> {
			if (!sevenDayReservations.contains(r.getId())) {
				cancelledWithoutPenalty.add(r);
				try {
					reservationService.hostCancelReservation(r);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		property.setIsDeleted(true);

		propertyRepository.save(property);
		return true;
	}

	@Override
	public List<SearchProperty> searchProperties(SearchRequest searchRequest) {

	  	List<Reservation> reservationsInDateSelected = reservationService.findAllReservationsBetweenDates(searchRequest.getFrom(), searchRequest.getTo());
		Set<Long> property_ids = reservationsInDateSelected.stream().map(r -> r.getProperty().getId()).collect(Collectors.toSet());

	  	List<Property> properties =  propertyRepositoryCustom.findPropertiesBySearchCriteria(searchRequest, property_ids);

	  	List<SearchProperty> searchProperties = new ArrayList<>();
	  	for (Property p : properties) {
	  		String imagesString = p.getPhotosArrayJson();
	  		String[] images = imagesString.split(",");
	  		String imageUrl = "";
	  		if (images.length>0) {
	  			imageUrl = images[0].replace("[", "").replace("\"","").replace("]", "");
			}
			SearchProperty sp = new SearchProperty(imageUrl,p.getId(), p.getHeadline(), p.getAddressStreet(), p.getAddressCity(), p.getAddressState(), p.getAddressZipcode(), p.getWeekdayPrice(), p.getWeekendPrice());
			searchProperties.add(sp);
	  	}
	  	return searchProperties;
	}

	@Override
	public Property getProperty(String propertyId) {
		return propertyRepository.findById(Long.parseLong(propertyId))
				.orElseThrow(() -> new ResourceNotFoundException("Property", "id", propertyId));
	}
	
	@Override
    public boolean isDateRangeValid(Property property, LocalDate startDate, LocalDate endDate) {
    	boolean isValid = true;
    	
    	List<DayOfWeek> availableDaysList = getAvailableDaysList(property.getAvailableDays());
    	for(LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
    		if (!availableDaysList.contains(date.getDayOfWeek())) {
    			isValid = false;
    			break;
    		}
    	}
    	
    	return isValid;
    }

	private List<DayOfWeek> getAvailableDaysList(String availableDays) {
		List<DayOfWeek> availableDaysList = new ArrayList<DayOfWeek>();
		if (availableDays.contains("M")) {
			availableDaysList.add(DayOfWeek.MONDAY);
		}
		
		if (availableDays.contains("TU")) {
			availableDaysList.add(DayOfWeek.TUESDAY);
		}
		
		if (availableDays.contains("W")) {
			availableDaysList.add(DayOfWeek.WEDNESDAY);
		}
		
		if (availableDays.contains("TH")) {
			availableDaysList.add(DayOfWeek.THURSDAY);
		}
		
		if (availableDays.contains("F")) {
			availableDaysList.add(DayOfWeek.FRIDAY);
		}
		
		if (availableDays.contains("SA")) {
			availableDaysList.add(DayOfWeek.SATURDAY);
		}
		
		if (availableDays.contains("SU")) {
			availableDaysList.add(DayOfWeek.SUNDAY);
		}
		
		return availableDaysList;
	}
}
