package com.fab.digital.GoogleVisionConnectorAPI.dao;

import java.util.Map;

public class ImageResponse {
	
	private Map backSideEmiratesIdDetails;
	private String textDescription;

	public String getTextDescription() {
		return textDescription;
	}

	public void setTextDescription(String textDescription) {
		this.textDescription = textDescription;
	}

	public Map getBackSideEmiratesIdDetails() {
		return backSideEmiratesIdDetails;
	}

	public void setBackSideEmiratesIdDetails(Map backSideEmiratesIdDetails) {
		this.backSideEmiratesIdDetails = backSideEmiratesIdDetails;
	}
	

}
