package com.cmpe275.openhome.service;

import com.cmpe275.openhome.entity.PropertyDetails;
import com.cmpe275.openhome.exception.ResourceNotFoundException;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.ReservationStatusEnum;
import com.cmpe275.openhome.payload.EditPropertyResponse;
import com.cmpe275.openhome.payload.EditPropertyStatus;
import com.cmpe275.openhome.payload.SearchProperty;
import com.cmpe275.openhome.payload.SearchRequest;
import com.cmpe275.openhome.repository.PropertyRepository;
import com.cmpe275.openhome.repository.PropertyRepositoryCustom;
import com.cmpe275.openhome.repository.ReservationRepository;
import com.cmpe275.openhome.util.DateUtils;
import com.cmpe275.openhome.util.SystemDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.*;
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
	public EditPropertyStatus editProperty(Property newProperty, Boolean isApprovedForPayingFine) throws Exception {

		LocalDate currentDate = SystemDateTime.getCurSystemTime().toLocalDate();

		//fetch record from DB to compare against edited changes
		Optional<Property> fromDbOptional = propertyRepository.findById(newProperty.getId());

		if (!fromDbOptional.isPresent()) {
			//if editing an invalid newProperty which is not in DB
			throw new Exception(String.format("newProperty with id {} not found", newProperty.getId()));
		}

		Property oldProperty = fromDbOptional.get();

		List initialAvailability = availableDaysList(oldProperty.getAvailableDays());
		List newAvailability = availableDaysList(newProperty.getAvailableDays());

		Boolean hasAvailabilityChanged = !initialAvailability.equals(newAvailability);

		//if anything other than availability is changed, just save that change. if not continue
		if(!hasAvailabilityChanged) {
			Property savedProperty = propertyRepository.save(newProperty);
			return EditPropertyStatus.EditSuccessful;
		}

		List<Reservation> cancelledReservations = new ArrayList<>();

		List<Reservation> conflictingReservations = conflictingReservations(oldProperty, newAvailability);

		if (conflictingReservations.size() > 0 && isApprovedForPayingFine) {
			for (Reservation r : conflictingReservations) {
				cancelledReservations.add(r);
				reservationService.hostCancelReservation(r);
			}
		} else if (conflictingReservations.size() > 0 && !isApprovedForPayingFine) {
			return EditPropertyStatus.NeedsApproval;
		}

		Property savedProperty = propertyRepository.save(newProperty);
		return EditPropertyStatus.EditSuccessful;
	}

	@Override
	public EditPropertyStatus deleteProperty(Property newProperty, Boolean isApprovedForPayingFine) throws Exception {

		LocalDate currentDate = SystemDateTime.getCurSystemTime().toLocalDate();

		//fetch record from DB to compare against edited changes
		Optional<Property> fromDbOptional = propertyRepository.findById(newProperty.getId());

		if (!fromDbOptional.isPresent()) {
			//if editing an invalid newProperty which is not in DB
			throw new Exception(String.format("newProperty with id {} not found", newProperty.getId()));
		}

		List<Reservation> cancelledReservations = new ArrayList<>();

		List statusList = new ArrayList<>();
		statusList.add(ReservationStatusEnum.checkedIn);
		statusList.add(ReservationStatusEnum.pendingCheckIn);

		LocalDate oneYearFromNow = currentDate.plusDays(365);

		List<Reservation> reservations = reservationRepository.findReservationsBetweenDatesForGivenStatus(
				newProperty.getId(),
				DateUtils.convertLocalDateToDate(currentDate),
				DateUtils.convertLocalDateToDate(oneYearFromNow),
				statusList
		);
		System.out.println("Reservations:"+reservations);

		if (reservations.size() > 0 && isApprovedForPayingFine) {
			for (Reservation r : reservations) {
				cancelledReservations.add(r);
				reservationService.hostCancelReservation(r);
			}
		} else if (reservations.size() > 0 && !isApprovedForPayingFine) {
			return EditPropertyStatus.NeedsApproval;
		}

		newProperty.setIsDeleted(true);
		System.out.println("Deleted");
		Property savedProperty = propertyRepository.save(newProperty);
		return EditPropertyStatus.EditSuccessful;
	}

	@Override
	public List<SearchProperty> searchProperties(SearchRequest searchRequest) {
		System.out.println("Search Request:"+searchRequest);

		List<Reservation> reservationsPendingBasedOnEndDate = reservationService.findAllReservationsPendingBasedOnEndDate(searchRequest.getFrom(), searchRequest.getTo());
		List<Reservation> reservationsPendingBasedOnCheckoutDate = reservationService.findAllReservationsPendingBasedOnCheckoutDate(searchRequest.getFrom(), searchRequest.getTo());


		Set<Long> property_ids_pendingCheckIn = reservationsPendingBasedOnEndDate.stream().map(r -> r.getProperty().getId()).collect(Collectors.toSet());
		Set<Long> property_ids_checkedIn = reservationsPendingBasedOnCheckoutDate.stream().map(r -> r.getProperty().getId()).collect(Collectors.toSet());

		Set<Long> reserved_property_ids = new HashSet<>();
		reserved_property_ids.addAll(property_ids_pendingCheckIn);
		reserved_property_ids.addAll(property_ids_checkedIn);


		List<Property> properties = propertyRepositoryCustom.findPropertiesBySearchCriteria(searchRequest, reserved_property_ids);

		Set<Integer> requiredDays = getDaysForDateRange(searchRequest.getFrom(), searchRequest.getTo());
		System.out.println("Required Days"+requiredDays);

		List<SearchProperty> searchProperties = new ArrayList<>();
		for (Property p : properties) {
			List availableDays = availableDaysList(p.getAvailableDays());
			System.out.println("Available Days:"+availableDays);

			Boolean allRequiredDaysAvailable = true;
			//check if property if available on all days requested
			for (Integer day : requiredDays) {
				if (!availableDays.contains(day)) {
					allRequiredDaysAvailable = false;
					break;
				}
			}

			if (!allRequiredDaysAvailable) {
				continue;
			}

			String imagesString = p.getPhotosArrayJson();
			String[] images = imagesString.split(",");
			String imageUrl = "";
			if (images.length > 0) {
				imageUrl = images[0].replace("[", "").replace("\"", "").replace("]", "");
			}
			SearchProperty sp = new SearchProperty(imageUrl, p.getId(), p.getHeadline(), p.getAddressStreet(), p.getAddressCity(), p.getAddressState(), p.getAddressZipcode(), p.getWeekdayPrice(), p.getWeekendPrice());
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
    public List<SearchProperty> getProperties(String hostId) {
        List<Property> properties = propertyRepository.findByOwnerId(Long.parseLong(hostId));
		List<SearchProperty> searchProperties = new ArrayList<>();
		for (Property p : properties) {
			String imagesString = p.getPhotosArrayJson();
			String[] images = imagesString.split(",");
			String imageUrl = "";
			if (images.length > 0) {
				imageUrl = images[0].replace("[", "").replace("\"", "").replace("]", "");
			}
			SearchProperty sp = new SearchProperty(imageUrl, p.getId(), p.getHeadline(), p.getAddressStreet(), p.getAddressCity(), p.getAddressState(), p.getAddressZipcode(), p.getWeekdayPrice(), p.getWeekendPrice());
			searchProperties.add(sp);
		}
		return searchProperties;
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

	private List<Reservation> conflictingReservations(Property property, List newAvailableDays) {
		List conflictingReservations = new ArrayList();

		LocalDate currentDate = SystemDateTime.getCurSystemTime().toLocalDate();
		LocalDate oneYearFromNow = currentDate.plusDays(365);

		List statusList = new ArrayList<>();
		statusList.add(ReservationStatusEnum.checkedIn);
		statusList.add(ReservationStatusEnum.pendingCheckIn);

		List<Reservation> reservations = reservationRepository.findReservationsBetweenDatesForGivenStatus(
				property.getId(),
				DateUtils.convertLocalDateToDate(currentDate),
				DateUtils.convertLocalDateToDate(oneYearFromNow),
				statusList
		);

		for (Reservation r : reservations) {
			Set<Integer> bookedDays = getDaysForDateRange(r.getStartDate(), r.getEndDate());
			for (Integer day : bookedDays) {
				if (!newAvailableDays.contains(day)){
					conflictingReservations.add(r);
					break;
				}
			}
		}

		return conflictingReservations;
	}

	private Set<Integer> getDaysForDateRange(Date startDate, Date endDate) {
	  	System.out.println("Start Date:"+startDate);
	  	System.out.println("End Date:"+endDate);

		Set days = new HashSet();
		Date current = startDate;

		while (current.before(endDate)) {
			days.add(current.getDay());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(current);
			calendar.add(Calendar.DATE, 1);
			current = calendar.getTime();
		}

		return days;
	}

	private List<Integer> availableDaysList(String availableDays) {
	  	List availableDaysList = new ArrayList();
	  	if (availableDays.contains("SU")) {
	  		availableDaysList.add(0);
		}
		if (availableDays.contains("M")) {
			availableDaysList.add(1);
		}
		if (availableDays.contains("TU")) {
			availableDaysList.add(2);
		}
		if (availableDays.contains("W")) {
			availableDaysList.add(3);
		}
		if (availableDays.contains("TH")) {
			availableDaysList.add(4);
		}
		if (availableDays.contains("F")) {
			availableDaysList.add(5);
		}
		if (availableDays.contains("SA")) {
			availableDaysList.add(6);
		}
		return availableDaysList;
	}
}
