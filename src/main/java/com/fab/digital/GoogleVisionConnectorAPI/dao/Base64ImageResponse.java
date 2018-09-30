package com.fab.digital.GoogleVisionConnectorAPI.dao;
import com.google.cloud.vision.v1.Feature.Type;

public class Base64ImageResponse {
	
	private String image;
	private Type detectionType;
	
	

	public Type getDetectionType() {
		return detectionType;
	}

	public void setDetectionType(Type faceDetection) {
		this.detectionType = faceDetection;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
}
