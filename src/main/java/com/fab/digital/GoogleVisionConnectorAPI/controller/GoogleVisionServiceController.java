package com.fab.digital.GoogleVisionConnectorAPI.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
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

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.CropHint;
import com.google.cloud.vision.v1.CropHintsAnnotation;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.cloud.vision.v1.Vertex;
import com.google.protobuf.ByteString;


import java.util.List;


@RestController
@RequestMapping("/google-vision")
public class GoogleVisionServiceController {
	
	@GetMapping("/check/endpoint")
	public String checkEndpoint() {
		
		return "Check Done";
	}
	
	@PostMapping("/detect/emiratesid/backend")
	public ImageResponse detectEmiratesIdBack(RequestEntity<Base64ImageRequest> request) {
		
		Base64ImageRequest base64ImageRequest = request.getBody();
		base64ImageRequest.setDetectionType(Type.DOCUMENT_TEXT_DETECTION);		
		
		List<AnnotateImageResponse> imageResponses = detectTextOrImage(base64ImageRequest);
		
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
	
	@PostMapping("/detect/emiratesid/frontend/text")
	public ImageResponse detectEmiratesIdFront(RequestEntity<Base64ImageRequest> request) {
		
		Base64ImageRequest base64ImageRequest = request.getBody();
		base64ImageRequest.setDetectionType(Type.DOCUMENT_TEXT_DETECTION);
		
		
		List<AnnotateImageResponse> imageResponses = detectTextOrImage(base64ImageRequest);	
		
		AnnotateImageResponse response = imageResponses.get(0);
		
		String description = response.getTextAnnotations(0).getDescription();
		
		StringTokenizer str = new StringTokenizer(description, "\n");
		
		Map<String, String> dataMap = new LinkedHashMap<String,String>();
		List<String> dataList = new LinkedList<String>();
		
		while (str.hasMoreElements()) {			
			
			String value = (String) str.nextElement();	
			System.out.println("Token::::" + value);
			dataList.add(value);
		}		
		
		String idCardNumber = dataList.get(4).toString();
		String nameString = dataList.get(6).toString();
		String cardString = dataList.get(9).toString();
		
		dataMap.put("Resident Identity Card",idCardNumber);
		dataMap.put(nameString.split(":")[0].toString(),nameString.split(":")[1].toString());
		dataMap.put(cardString.split(":")[0],cardString.split(":")[1]);
		
		ImageResponse imageResponse = new ImageResponse();
		imageResponse.setFrontSideEmiratesIdDetails(dataMap);
		imageResponse.setTextDescription(description);
		
		
		return imageResponse;
		
	}
	
	public List<AnnotateImageResponse> detectTextOrImage(Base64ImageRequest request) {
		
			
			// Instantiates a client			
			List<AnnotateImageResponse> responses = null;
			
		    try {	      
		    	
		      byte[] data = Base64.decodeBase64(request.getImage());
		      
		      ByteString imgBytes = ByteString.copyFrom(data);

		      // Builds the image annotation request
		      List<AnnotateImageRequest> requests = new ArrayList<>();
		      Image img = Image.newBuilder().setContent(imgBytes).build();
		      
		      Feature feat = null;
		      
		      if (request.getDetectionType().equals(Type.DOCUMENT_TEXT_DETECTION)) {
		    	  feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
			
		      }else if (request.getDetectionType().equals(Type.FACE_DETECTION)) {
				feat = Feature.newBuilder().setType(Type.FACE_DETECTION).build();
			
		      }else {
				throw new Exception("Unknown Type pased");
			}
		      
		      AnnotateImageRequest request1 = AnnotateImageRequest.newBuilder()
		          .addFeatures(feat)
		          .setImage(img)
		          .build();
		      requests.add(request1);
		      
		      ImageAnnotatorClient vision = ImageAnnotatorClient.create();

		      // Performs label detection on the image file
		      BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);		      
		      responses = response.getResponsesList();    
		      
		      for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
		          System.out.printf("Error: %s\n", res.getError().getMessage());
		          return null;
		        }

		        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
		          annotation.getAllFields().forEach((k, v) ->
		              System.out.printf("%s : %s\n", k, v.toString()));
		        }
		      }
		      
		    }catch(Exception e) {
		    	e.printStackTrace();
		    }
			
			
			return responses;
			
			
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
				      
				      Feature feat = null;
				      feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
				      
				     /* if (request.getBody().getDetectionType().equals(Type.DOCUMENT_TEXT_DETECTION)) {
				    	  feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
					
				      }else if (request.getBody().getDetectionType().equals(Type.FACE_DETECTION)) {
						feat = Feature.newBuilder().setType(Type.FACE_DETECTION).build();
					
				      }else {
						throw new Exception("Unknown Type pased");
					}
				      */
				      AnnotateImageRequest request1 = AnnotateImageRequest.newBuilder()
				          .addFeatures(feat)
				          .setImage(img)
				          .build();
				      requests.add(request1);
				      
				      ImageAnnotatorClient vision = ImageAnnotatorClient.create();

				      // Performs label detection on the image file
				      BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);		      
				      responses = response.getResponsesList();    
				      
				      for (AnnotateImageResponse res : responses) {
						if (res.hasError()) {
				          System.out.printf("Error: %s\n", res.getError().getMessage());
				          return null;
				        }

				        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
				          annotation.getAllFields().forEach((k, v) ->
				              System.out.printf("%s : %s\n", k, v.toString()));
				        }
				      }
				      
				    }catch(Exception e) {
				    	e.printStackTrace();
				    }
					
					
					return responses;
		
		
	}
	
	
	@PostMapping("/crop/image/withface")
	public void cropImage(RequestEntity<Base64ImageRequest> request) {
		
		// Instantiates a client
		try {
			//detectFaces(request.getBody());
			detectFaces(request.getBody());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public static void detectFaces(Base64ImageRequest request) throws Exception, IOException {
		  List<AnnotateImageRequest> requests = new ArrayList<>();
		  
		  byte[] data = Base64.decodeBase64(request.getImage());

		  ByteString imgBytes = ByteString.copyFrom(data);

		  Image img = Image.newBuilder().setContent(imgBytes).build();
		  Feature feat = Feature.newBuilder().setType(Type.FACE_DETECTION).build();
		  AnnotateImageRequest request1 =
		      AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		  requests.add(request1);

		  try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
		    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
		    List<AnnotateImageResponse> responses = response.getResponsesList();

		    for (AnnotateImageResponse res : responses) {
		      if (res.hasError()) {
		        System.out.println("Error: %s\n"+ res.getError().getMessage());
		        return;
		      }

		      // For full list of available annotations, see http://g.co/cloud/vision/docs
		      for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
		    	  System.out.println( "anger: %s\njoy: %s\nsurprise: %s\nposition: %s"+
		            annotation.getAngerLikelihood()+
		            annotation.getJoyLikelihood()+
		            annotation.getSurpriseLikelihood()+ annotation.getBoundingPoly());
		    	  
		    	  BoundingPoly boundingPoly = annotation.getBoundingPoly();
		    	  List<Vertex> vertexsList = boundingPoly.getVerticesList();
		    	  
		    	  
		    	  
		    	  int verticeCount = boundingPoly.getVerticesCount();
		    	  System.out.println("verticeCount for face" + verticeCount);
		    	  
		    	  if (verticeCount == 4) {
					
		    		  BufferedImage myImage;
		    		  
		    		  ByteArrayInputStream bais = new ByteArrayInputStream(data);
		    		    try {
		    		    	myImage = ImageIO.read(bais);
		    		    } catch (IOException e) {
		    		        throw new RuntimeException(e);
		    		    }
		    		  
		    		    BufferedImage myImage1;
		    		    
		    		    myImage1 = myImage.getSubimage(
		   		    		  vertexsList.get(0).getX(),
		   		    		  vertexsList.get(0).getY(), 
		   		    		  (vertexsList.get(1).getX()-vertexsList.get(0).getX()), 
		   		    		  (vertexsList.get(3).getY()-vertexsList.get(0).getY()));
		   		       
		   		    File outputfile = new File("./cropedface_image.png");
		   		    ImageIO.write(myImage1, "png", outputfile);
		    		  
				}
		    	  
		    	  
		      }
		    }
		  }
		}
	
	
	
	public static void detectCropHints(Base64ImageRequest request) throws Exception,
    IOException {
	  List<AnnotateImageRequest> requests = new ArrayList<>();
	
	  byte[] data = Base64.decodeBase64(request.getImage());
      
      ByteString imgBytes = ByteString.copyFrom(data);
	
	  Image img = Image.newBuilder().setContent(imgBytes).build();
	  
	  
	  Feature feat = Feature.newBuilder().setType(Type.CROP_HINTS).build();
	  AnnotateImageRequest request1 =
	      AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
	  requests.add(request1);
	
	  try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
	    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
	    List<AnnotateImageResponse> responses = response.getResponsesList();
	
	    for (AnnotateImageResponse res : responses) {
	      if (res.hasError()) {
	        //out.printf("Error: %s\n", res.getError().getMessage());
	    	  System.out.println("Error:"+ res.getError().getMessage());
	        return;
	      }
	
	      // For full list of available annotations, see http://g.co/cloud/vision/docs
	      CropHintsAnnotation annotation = res.getCropHintsAnnotation();
	      
	      
	      
	      for (CropHint hint : annotation.getCropHintsList()) {
	        //out.println(hint.getBoundingPoly());
	        System.out.println("Bounding Poly:"+ hint.getBoundingPoly());
	        //System.out.println("All Vields:"+ hint.getAllFields());
	        BoundingPoly  poly = hint.getBoundingPoly();
	        
	        
	      }
	    }
	    
	    BufferedImage myImaxe;
	   // myImage.getSubImage(x1, y1, (x2-x1), (y2-y1));

	    
	     
	    
	    /*im = Image.open(image_file)
	    		draw = ImageDraw.Draw(im)
	    		draw.polygon([
	    		    vects[0].x, vects[0].y,
	    		    vects[1].x, vects[1].y,
	    		    vects[2].x, vects[2].y,
	    		    vects[3].x, vects[3].y], None, 'red')
	    		im.save('output-hint.jpg', 'JPEG')*/
	    
	    
	    
	  }
	}
	
	
	
		/*public static void detectCropHints(Base64ImageRequest request) throws Exception,
	    IOException {
		  List<AnnotateImageRequest> requests = new ArrayList<>();
		
		  byte[] data = Base64.decodeBase64(request.getImage());
	      
	      ByteString imgBytes = ByteString.copyFrom(data);
		
		  Image img = Image.newBuilder().setContent(imgBytes).build();
		  
		  
		  Feature feat = Feature.newBuilder().setType(Type.CROP_HINTS).build();
		  AnnotateImageRequest request1 =
		      AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		  requests.add(request1);
		
		  try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
		    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
		    List<AnnotateImageResponse> responses = response.getResponsesList();
		
		    for (AnnotateImageResponse res : responses) {
		      if (res.hasError()) {
		        //out.printf("Error: %s\n", res.getError().getMessage());
		    	  System.out.println("Error:"+ res.getError().getMessage());
		        return;
		      }
		
		      // For full list of available annotations, see http://g.co/cloud/vision/docs
		      CropHintsAnnotation annotation = res.getCropHintsAnnotation();
		      
		      
		      
		      for (CropHint hint : annotation.getCropHintsList()) {
		        //out.println(hint.getBoundingPoly());
		        System.out.println("Bounding Poly:"+ hint.getBoundingPoly());
		        System.out.println("All Vields:"+ hint.getAllFields());
		        BoundingPoly  poly = hint.getBoundingPoly();
		        
		        
		      }
		    }
		    
		    BufferedImage myImaxe;
		   // myImage.getSubImage(x1, y1, (x2-x1), (y2-y1));

		    
		     
		    
		    im = Image.open(image_file)
		    		draw = ImageDraw.Draw(im)
		    		draw.polygon([
		    		    vects[0].x, vects[0].y,
		    		    vects[1].x, vects[1].y,
		    		    vects[2].x, vects[2].y,
		    		    vects[3].x, vects[3].y], None, 'red')
		    		im.save('output-hint.jpg', 'JPEG')
		    
		    
		    
		  }
		}*/
	

}

