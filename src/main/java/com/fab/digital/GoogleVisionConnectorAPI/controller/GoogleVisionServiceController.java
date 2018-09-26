package com.fab.digital.GoogleVisionConnectorAPI.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fab.digital.GoogleVisionConnectorAPI.dao.Base64ImageRequest;
/*
import com.fab.digital.GoogleVisionConnectorAPI.dao.AnnotateImageRequest;
import com.fab.digital.GoogleVisionConnectorAPI.dao.AnnotationImage;

import com.fab.digital.GoogleVisionConnectorAPI.dao.Feature;
import com.fab.digital.GoogleVisionConnectorAPI.dao.Image;
import com.fab.digital.GoogleVisionConnectorAPI.dao.Type;
*/
import com.fab.digital.GoogleVisionConnectorAPI.dao.BinaryImageRequest;
import com.fab.digital.GoogleVisionConnectorAPI.dao.ImageResponse;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;


import java.util.List;


@RestController
@RequestMapping("/google-vision")
public class GoogleVisionServiceController {
	
	@PostMapping("/detect/emiratesid/backend")
	public ImageResponse detectEmiratesIdBack(RequestEntity<Base64ImageRequest> request) {
		
		List<AnnotateImageResponse> imageResponses = detectText(request);
		
		AnnotateImageResponse response = imageResponses.get(0);
		
		String description = response.getTextAnnotations(0).getDescription();
		
		StringTokenizer str = new StringTokenizer(description, "\n");
		
		Map<String, String> dataMap = new LinkedHashMap<String,String>();
		List<String> dataList = new LinkedList<String>();
		
		while (str.hasMoreElements()) {			
			
			String value = (String) str.nextElement();			
			dataList.add(value);
		}
		String sexString = "";
		StringTokenizer sexToken = new StringTokenizer(dataList.get(0).toString()," ");
		while (sexToken.hasMoreElements()) {
			sexString = sexToken.nextElement().toString();
			
		}
		String dobValueString = dataList.get(2).toString()
				                   +dataList.get(3).toString()
				                   +dataList.get(4).toString()
				                   +dataList.get(5).toString()
				                   +dataList.get(6).toString();
		String expString = dataList.get(10).toString();
		String cardString = dataList.get(11).toString();
		
		dataMap.put("Sex",sexString);
		dataMap.put("Date Of Birth",dobValueString);
		dataMap.put("Expiary Date",expString);
		dataMap.put("Card Number",cardString);
		
		ImageResponse imageResponse = new ImageResponse();
		imageResponse.setBackSideEmiratesIdDetails(dataMap);
		imageResponse.setTextDescription(description);
		
		return imageResponse;
		
	}
	
	@PostMapping("/detect/text")
	public List<AnnotateImageResponse> detectText(RequestEntity<Base64ImageRequest> request) {
	//public String detectText(RequestEntity<Base64ImageRequest> request) {	
	
		
		/*
		final String url = "https://vision.googleapis.com/v1/images:annotate";
		
		HttpHeaders httpHeaders = new HttpHeaders(); 
		
		httpHeaders.set("authorization", "Bearer ya29.c.El8kBqOkmpvUqiNoa3ozONPYBeiMkas2LFKoeGIuB3kLes-oW7oUYJbK31w-mpY-mE6hmjTBepFDKEcGHpA8Qu_Dt-0i0TqgMCCw3dfINau9SCPx9SYMLqhutIBRH69qDg");
		httpHeaders.set("content-type", "application/json");
		httpHeaders.set("cache-control", "no-cache");
		httpHeaders.set("charset", "utf-8");
		
		AnnotationImage annotationImage = new AnnotationImage();
		
		AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
		
		Feature feature = new Feature();
		
		feature.setType(Type.TEXT_DETECTION);
		feature.setMaxResults("1");
		
		Image image = new Image();
		image.setContent(request.getBody().getImage());
		
		ArrayList<Feature> featureList = new ArrayList<Feature>();
		featureList.add(feature);
		annotateImageRequest.setFeatures(featureList);
		annotateImageRequest.setImage(image);

		ArrayList<AnnotateImageRequest> imageRequestList = new ArrayList<AnnotateImageRequest>();
		imageRequestList.add(annotateImageRequest);
		annotationImage.setRequests(imageRequestList);
		
		HttpEntity<AnnotationImage> httpEntity = new HttpEntity<AnnotationImage>(annotationImage,httpHeaders);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		
		SSLContext context = null;
		
		try {
			context = SSLContext.getInstance("SSL");
			try {
				context.init(null,null,null);
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//@SuppressWarnings("deprecation")
		CloseableHttpClient httpClient = HttpClients.custom().setSslcontext(context).build();
		
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		
		RestTemplate restTemplate = new RestTemplate(factory);
		ResponseEntity<String> response = null;
		
		response = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, httpEntity, String.class);
		
		
		return response.getBody();
		
	}*/
		
		// Instantiates a client
		
		List<AnnotateImageResponse> responses = null;
		
	    try {	      
	    	
	      byte[] data = Base64.decodeBase64(request.getBody().getImage());	
	      ByteString imgBytes = ByteString.copyFrom(data);

	      // Builds the image annotation request
	      List<AnnotateImageRequest> requests = new ArrayList<>();
	      Image img = Image.newBuilder().setContent(imgBytes).build();
	      Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
	      AnnotateImageRequest request1 = AnnotateImageRequest.newBuilder()
	          .addFeatures(feat)
	          .setImage(img)
	          .build();
	      requests.add(request1);
	      
	      ImageAnnotatorClient vision = ImageAnnotatorClient.create();

	      // Performs label detection on the image file
	      BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
	      
	      responses = response.getResponsesList();
	      
	      
	      /*for (AnnotateImageResponse res : responses) {
			if (res.hasError()) {
	          System.out.printf("Error: %s\n", res.getError().getMessage());
	          return null;
	        }

	        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
	          annotation.getAllFields().forEach((k, v) ->
	              System.out.printf("%s : %s\n", k, v.toString()));
	        }
	      }*/
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
		
		
		return responses;
		
		
	}
	

}

