package com.cmpe275.openhome.entity;

public class Property {
	
	private Long myId;
	
	private String myOwner;

	public Long getId() {
		return myId;
	}

	public void setId(Long id) {
		myId = id;
	}

	public String getOwner() {
		return myOwner;
	}

	public void setOwner(String owner) {
		myOwner = owner;
	}
}
