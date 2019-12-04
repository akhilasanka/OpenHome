package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.payload.*;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.security.CurrentUser;
import com.cmpe275.openhome.security.UserPrincipal;
import com.cmpe275.openhome.service.PropertyService;
import com.cmpe275.openhome.util.PropertyJsonToModelUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
public class PropertyController {


  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PropertyService propertyService;

  @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/hosts/{hostName}/properties")
  public List<Property> getAllProperties(@PathVariable String hostName) {
	 return propertyService.getHardcodedPropertyList(); // TODO: proper impl!
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/hosts/{hostId}/property")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> postProperty(@CurrentUser UserPrincipal userPrincipal, @PathVariable String hostId, @Valid @RequestBody PostPropertyRequest postPropertyRequest) {
    System.out.println("HostId: " + hostId);
    ObjectMapper mapper = new ObjectMapper();
    try {
      String json = mapper.writeValueAsString(postPropertyRequest);
      System.out.println("JSON = " + json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    Property result = null;
    try {
      User owner = userRepository.getOne(Long.parseLong(hostId));
      Property property = PropertyJsonToModelUtil.getProperty(postPropertyRequest, owner);
      result = propertyService.hostProperty(property);
    } catch (Exception e) {
      //send a failure status code like 500
      e.printStackTrace();
    }

    URI location = ServletUriComponentsBuilder
            .fromCurrentContextPath().path("/property")
            .buildAndExpand(result.getId()).toUri();

    return ResponseEntity.created(location)
            .body(new PostPropertyResponse(true, result.getId(),
                    "Property registered successfully@"));
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/hosts/{hostId}/property/{propertyId}/edit")
  @PreAuthorize("hasRole('USER')")
  public EditPropertyResponse editProperty(@CurrentUser UserPrincipal userPrincipal, @RequestParam Boolean isPenalityApproved, @PathVariable String hostId, @PathVariable long propertyId, @Valid @RequestBody PostPropertyRequest postPropertyRequest) throws Exception {
    User owner = userRepository.getOne(Long.parseLong(hostId));
    Property property = PropertyJsonToModelUtil.getProperty(postPropertyRequest, owner);
    property.setId(propertyId);
    EditPropertyStatus editStatus;
    try {
      editStatus = propertyService.editProperty(property, isPenalityApproved);
    } catch(Exception e) {
      editStatus = EditPropertyStatus.EditError;
      return new EditPropertyResponse(editStatus, e.getMessage());
    }
      return new EditPropertyResponse(editStatus, "");
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/hosts/{hostId}/property/{propertyId}/delete")
  @PreAuthorize("hasRole('USER')")
  public EditPropertyResponse deleteProperty(@CurrentUser UserPrincipal userPrincipal, @RequestParam Boolean isPenalityApproved, @PathVariable String hostId, @PathVariable long propertyId, @Valid @RequestBody PostPropertyRequest postPropertyRequest) throws Exception {
    User owner = userRepository.getOne(Long.parseLong(hostId));
    Property property = PropertyJsonToModelUtil.getProperty(postPropertyRequest, owner);
    property.setId(propertyId);
    EditPropertyStatus editStatus;
    try {
      editStatus = propertyService.deleteProperty(property, isPenalityApproved);
    } catch(Exception e) {
      editStatus = EditPropertyStatus.EditError;
      return new EditPropertyResponse(editStatus, e.getMessage());
    }
    return new EditPropertyResponse(editStatus, "");
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/property/{propertyId}")
  public Property getProperty(@CurrentUser UserPrincipal userPrincipal, @PathVariable String propertyId) {
    return propertyService.getProperty(propertyId);
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/property/search")
  public SearchPropertyResponse searchProperty(@CurrentUser UserPrincipal userPrincipal, @RequestBody SearchRequest searchRequest) {
    Calendar calendar = Calendar.getInstance();

    Date from = searchRequest.getFrom();
    calendar.setTime(from);
    calendar.add(Calendar.DATE, 1);
    searchRequest.setFrom(calendar.getTime());

    Date to = searchRequest.getTo();
    calendar.setTime(to);
    calendar.add(Calendar.DATE, 1);
    searchRequest.setTo(calendar.getTime());
    return new SearchPropertyResponse(propertyService.searchProperties(searchRequest));
  }

}