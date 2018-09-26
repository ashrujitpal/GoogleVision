package com.fab.digital.GoogleVisionConnectorAPI.dao;

import java.util.List;

public class ImageContext {
	
	private String latLongRect;
	private List<String>[] languageHints;
	private CropHintsParams cropHintsParams;
	private WebDetectionParams detectionParams;
	
	
	public String getLatLongRect() {
		return latLongRect;
	}
	public void setLatLongRect(String latLongRect) {
		this.latLongRect = latLongRect;
	}
	public List<String>[] getLanguageHints() {
		return languageHints;
	}
	public void setLanguageHints(List<String>[] languageHints) {
		this.languageHints = languageHints;
	}
	public CropHintsParams getCropHintsParams() {
		return cropHintsParams;
	}
	public void setCropHintsParams(CropHintsParams cropHintsParams) {
		this.cropHintsParams = cropHintsParams;
	}
	public WebDetectionParams getDetectionParams() {
		return detectionParams;
	}
	public void setDetectionParams(WebDetectionParams detectionParams) {
		this.detectionParams = detectionParams;
	}
	

}
