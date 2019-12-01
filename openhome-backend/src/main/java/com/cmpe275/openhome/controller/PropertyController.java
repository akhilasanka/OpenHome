package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.entity.PropertyDetails;
import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.payload.ApiResponse;
import com.cmpe275.openhome.payload.PostPropertyRequest;
import com.cmpe275.openhome.payload.SearchPropertyResponse;
import com.cmpe275.openhome.payload.SearchRequest;
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
import sun.tools.tree.BooleanExpression;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
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
            .body(new ApiResponse(true, "Property registered successfully@"));
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/hosts/{hostId}/property/{propertyId}/edit")
  @PreAuthorize("hasRole('USER')")
  public Boolean editProperty(@CurrentUser UserPrincipal userPrincipal, @RequestParam Boolean isApproved, @PathVariable String hostId, @PathVariable long propertyId, @Valid @RequestBody PostPropertyRequest postPropertyRequest) throws Exception {
    User owner = userRepository.getOne(Long.parseLong(hostId));
    Property property = PropertyJsonToModelUtil.getProperty(postPropertyRequest, owner);
    property.setId(propertyId);
    return propertyService.editProperty(property, isApproved);
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/hosts/{hostId}/property/{propertyId}/delete")
  @PreAuthorize("hasRole('USER')")
  public Boolean deleteProperty(@CurrentUser UserPrincipal userPrincipal, @RequestParam Boolean isApproved, @PathVariable String hostId, @PathVariable long propertyId, @Valid @RequestBody PostPropertyRequest postPropertyRequest) throws Exception {
    User owner = userRepository.getOne(Long.parseLong(hostId));
    Property property = PropertyJsonToModelUtil.getProperty(postPropertyRequest, owner);
    property.setId(propertyId);
    return propertyService.deleteProperty(property, isApproved);
  }

    @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/hosts/{hostId}/property/{propertyId}")
  public PropertyDetails getProperty(@CurrentUser UserPrincipal userPrincipal, @PathVariable String hostId, @PathVariable String propertyId) {
    return propertyService.getHardcodedPropertyDetails(); // TODO: proper impl!
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/property/search")
  public SearchPropertyResponse searchProperty(@CurrentUser UserPrincipal userPrincipal, @RequestBody SearchRequest searchRequest) {
    return new SearchPropertyResponse(propertyService.searchProperties(searchRequest));
  }

}