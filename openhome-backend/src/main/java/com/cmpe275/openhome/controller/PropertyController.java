package com.cmpe275.openhome.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cmpe275.openhome.entity.Property;
import com.cmpe275.openhome.service.PropertyService;

@RestController
public class PropertyController {

  @Autowired
  private PropertyService myPropertyService;
  
  @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/hosts/{hostName}/properties")
  public List<Property> getAllProperties(@PathVariable String hostName) {
	 return myPropertyService.getHardcodedPropertyList(); // TODO: proper impl!
  }

}