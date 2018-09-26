package com.fab.digital.GoogleVisionConnectorAPI.dao;

import java.util.ArrayList;

public class AnnotateImageRequest {
	
	private Image image;
	private ArrayList<Feature> features;
	private ImageContext imageContext;
	
	
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public ArrayList<Feature> getFeatures() {
		return features;
	}
	public void setFeatures(ArrayList<Feature> features) {
		this.features = features;
	}
	public ImageContext getImageContext() {
		return imageContext;
	}
	public void setImageContext(ImageContext imageContext) {
		this.imageContext = imageContext;
	}
	
	

}
