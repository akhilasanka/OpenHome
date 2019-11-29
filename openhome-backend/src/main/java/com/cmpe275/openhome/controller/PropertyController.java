package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.entity.Property;
import com.cmpe275.openhome.entity.PropertyDetails;
import com.cmpe275.openhome.payload.PostPropertyRequest;
import com.cmpe275.openhome.security.CurrentUser;
import com.cmpe275.openhome.security.UserPrincipal;
import com.cmpe275.openhome.service.PropertyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Random;

@RestController
public class PropertyController {

  @Autowired
  private PropertyService myPropertyService;
  
  @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/hosts/{hostName}/properties")
  public List<Property> getAllProperties(@PathVariable String hostName) {
	 return myPropertyService.getHardcodedPropertyList(); // TODO: proper impl!
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/hosts/{hostId}/property")
  public int postProperty(@CurrentUser UserPrincipal userPrincipal, @PathVariable String hostId, @Valid @RequestBody PostPropertyRequest postPropertyRequest) {
    System.out.println("HostId: " + hostId);
    ObjectMapper mapper = new ObjectMapper();
    try {
      String json = mapper.writeValueAsString(postPropertyRequest);
      System.out.println("JSON = " + json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return new Random().nextInt(); // TODO: proper impl!
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/hosts/{hostId}/property/{propertyId}")
  public PropertyDetails getProperty(@CurrentUser UserPrincipal userPrincipal, @PathVariable String hostId, @PathVariable String propertyId) {
    return myPropertyService.getHardcodedPropertyDetails(); // TODO: proper impl!
  }

}