package com.fab.digital.GoogleVisionConnectorAPI.dao;

import java.util.Map;

public class ImageResponse {
	
	private Map<?, ?> backSideEmiratesIdDetails;
	private Map<?, ?> frontSideEmiratesIdDetails;
	private String textDescription;
	private Image image;

	public String getTextDescription() {
		return textDescription;
	}

	public void setTextDescription(String textDescription) {
		this.textDescription = textDescription;
	}

	public Map<?, ?> getBackSideEmiratesIdDetails() {
		return backSideEmiratesIdDetails;
	}

	public void setBackSideEmiratesIdDetails(Map<?, ?> backSideEmiratesIdDetails) {
		this.backSideEmiratesIdDetails = backSideEmiratesIdDetails;
	}

	public Map<?, ?> getFrontSideEmiratesIdDetails() {
		return frontSideEmiratesIdDetails;
	}

	public void setFrontSideEmiratesIdDetails(Map<?, ?> frontSideEmiratesIdDetails) {
		this.frontSideEmiratesIdDetails = frontSideEmiratesIdDetails;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	

}
