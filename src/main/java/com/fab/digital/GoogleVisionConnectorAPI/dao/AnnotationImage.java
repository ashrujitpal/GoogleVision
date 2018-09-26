package com.fab.digital.GoogleVisionConnectorAPI.dao;

import java.util.ArrayList;
import java.util.List;

public class AnnotationImage {
	
	private List<AnnotateImageRequest> requests;

	public List<AnnotateImageRequest> getRequests() {
		return requests;
	}

	public void setRequests(ArrayList<AnnotateImageRequest> requests) {
		this.requests = requests;
	}
	
	

}
