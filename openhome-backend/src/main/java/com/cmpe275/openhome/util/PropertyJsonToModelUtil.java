package com.cmpe275.openhome.util;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.payload.PostPropertyRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to process payments charged to guests or penalty charged to hosts.
 */
public class PropertyJsonToModelUtil {
    private static final Gson JSON_CONVERTER = new Gson();
    private static final String ALWAYS_AVAILABLE_DAYS = "SUMTUWTHFSA";
    private static final Map<String, String> FULL_DAY_TO_COMPRESS_MAP = new HashMap<String, String>();
    static {
        FULL_DAY_TO_COMPRESS_MAP.put("monday","M");
        FULL_DAY_TO_COMPRESS_MAP.put("tuesday","TU");
        FULL_DAY_TO_COMPRESS_MAP.put("wednesday","W");
        FULL_DAY_TO_COMPRESS_MAP.put("thursday","TH");
        FULL_DAY_TO_COMPRESS_MAP.put("friday","F");
        FULL_DAY_TO_COMPRESS_MAP.put("saturday","SA");
        FULL_DAY_TO_COMPRESS_MAP.put("sunday","SU");
    }

    public static Property getProperty(PostPropertyRequest postPropertyRequest, Long hostId) {
        String availableDaysCompressedString = getAvailableDaysConpressedString(postPropertyRequest.getAlwaysAvailable(),
                postPropertyRequest.getWeeklyAvailability());

        Property property = new Property();
        property.setOwnerId(hostId);
        property.setAddressStreet(postPropertyRequest.getStreetAddress());
        property.setAddressCity(postPropertyRequest.getCity());
        property.setAddressState(postPropertyRequest.getState());
        property.setAddressZipcode(Long.parseLong(postPropertyRequest.getZipCode()));
        property.setSharingType(postPropertyRequest.getSharingType());
        property.setPropertyType(postPropertyRequest.getPropertyType());
        property.setNumBedroom(Long.parseLong(postPropertyRequest.getBedrooms()));
        property.setSquareFootage(Long.parseLong(postPropertyRequest.getSqft()));
        property.setPhoneNumber(Long.parseLong(postPropertyRequest.getPropertyContact()));
        property.setHeadline(postPropertyRequest.getHeadline());
        property.setDescription(postPropertyRequest.getDescription());
        property.setParkingAvailability(postPropertyRequest.getParkingAvailable());
        property.setWifiAvailability(postPropertyRequest.getFreeWifi());
        property.setAdditionalFeatures("");
        property.setAvailableDays(availableDaysCompressedString);
        property.setPhotosArrayJson(JSON_CONVERTER.toJson(postPropertyRequest.getPhotos()));
        property.setHasPrivateBathroom(postPropertyRequest.getPrivateBathroomAvailable());
        property.setHasPrivateShower(postPropertyRequest.getPrivateBathShowerAvailable());

        if(!postPropertyRequest.getParkingCost().isEmpty()) {
            property.setDailyParkingFee(Long.parseLong(postPropertyRequest.getParkingCost()));
        }
        if(!postPropertyRequest.getWeekdayRentPrice().isEmpty()) {
            property.setWeekdayPrice(Long.parseLong(postPropertyRequest.getWeekdayRentPrice()));
        }
        if(!postPropertyRequest.getWeekendRentPrice().isEmpty()) {
            property.setWeekendPrice(Long.parseLong(postPropertyRequest.getWeekendRentPrice()));
        }
        return property;
    }

    private static String getAvailableDaysConpressedString(String alwaysAvailable, String[] weeklyAvailability) {
        if(alwaysAvailable.equalsIgnoreCase("Yes")) return ALWAYS_AVAILABLE_DAYS;

        StringBuilder compressedDaysBuilder = new StringBuilder();
        for(String dayOfWeek : weeklyAvailability) {
            compressedDaysBuilder.append(FULL_DAY_TO_COMPRESS_MAP.get(dayOfWeek.toLowerCase()));
        }

        return compressedDaysBuilder.toString();
    }
}
