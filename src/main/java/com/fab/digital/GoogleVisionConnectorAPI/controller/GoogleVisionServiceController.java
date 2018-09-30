package com.fab.digital.GoogleVisionConnectorAPI.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fab.digital.GoogleVisionConnectorAPI.dao.Base64ImageRequest;
import com.fab.digital.GoogleVisionConnectorAPI.dao.Base64ImageResponse;
/*
import com.fab.digital.GoogleVisionConnectorAPI.dao.AnnotateImageRequest;
import com.fab.digital.GoogleVisionConnectorAPI.dao.AnnotationImage;

import com.fab.digital.GoogleVisionConnectorAPI.dao.Feature;
import com.fab.digital.GoogleVisionConnectorAPI.dao.Image;
import com.fab.digital.GoogleVisionConnectorAPI.dao.Type;
*/
import com.fab.digital.GoogleVisionConnectorAPI.dao.ImageResponse;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.CropHint;
import com.google.cloud.vision.v1.CropHintsAnnotation;
import com.google.cloud.vision.v1.CropHintsParams;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageContext;
import com.google.cloud.vision.v1.Vertex;
import com.google.protobuf.ByteString;




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
	public Base64ImageResponse  cropImage(RequestEntity<Base64ImageRequest> request) {
		
		Base64ImageResponse response = null;
		
		// Instantiates a client
		try {
			response = detectFaces(request.getBody());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return response;
	}
	
	@PostMapping("/crop/image/any")
	public Base64ImageResponse  cropAnyImage(RequestEntity<Base64ImageRequest> request) {
		
		Base64ImageResponse response = null;
		
		// Instantiates a client
		try {
				response = detectCropHints(request.getBody());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return response;
	}
	
	public static Base64ImageResponse detectFaces(Base64ImageRequest request) throws Exception, IOException {
		  List<AnnotateImageRequest> requests = new ArrayList<>();
		  Base64ImageResponse response1 = new Base64ImageResponse();
		  
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
		        return null;
		      }

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
		   		    
		   		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
			   		ImageIO.write( myImage1, "jpg", baos );
			   		baos.flush();
			   		byte[] imageInByte = baos.toByteArray();
			   		baos.close();
			   		
			   		response1.setImage(Base64.encodeBase64String(imageInByte));
		    		  
				}
		    	  
		    	  
		      }
		    }
		  }
		  
		  return response1;
		  
		}
	
	
	
	public  Base64ImageResponse detectCropHints(Base64ImageRequest request) throws Exception,
    IOException {
	  List<AnnotateImageRequest> requests = new ArrayList<>();
	  List<AnnotateImageResponse> responses = new ArrayList<>();
	  
	  Base64ImageResponse response1 = new Base64ImageResponse();
	
	  byte[] data = Base64.decodeBase64(request.getImage());
      
      ByteString imgBytes = ByteString.copyFrom(data);
	
	  Image img = Image.newBuilder().setContent(imgBytes).build();
	  CropHintsParams cropHintsParams = CropHintsParams.newBuilder().clearAspectRatios().addAspectRatios((float) 1.77).build();
	  
	  Feature feat = Feature.newBuilder().setType(Type.CROP_HINTS).build();
	  AnnotateImageRequest request1 =
              AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img)
                      .setImageContext(ImageContext.newBuilder().setCropHintsParams(cropHintsParams).build())
                      .build();
	  requests.add(request1);
	
	  try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
	    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
	    responses = response.getResponsesList();
	
	    for (AnnotateImageResponse res : responses) {
	      if (res.hasError()) {
	        //out.printf("Error: %s\n", res.getError().getMessage());
	    	  System.out.println("Error:"+ res.getError().getMessage());
	        return null;
	      }
	
	      // For full list of available annotations, see http://g.co/cloud/vision/docs
	      CropHintsAnnotation annotation = res.getCropHintsAnnotation();
	      
	      
	      
	      for (CropHint hint : annotation.getCropHintsList()) {
	        System.out.println("Bounding Poly:"+ hint.getBoundingPoly());
	        BoundingPoly  poly = hint.getBoundingPoly();
	        
	        List<Vertex> vertexsList = poly.getVerticesList();
	    	  
	    	  
	    	  
	    	  int verticeCount = poly.getVerticesCount();
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
	   		    
	   		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		   		ImageIO.write( myImage1, "jpg", baos );
		   		baos.flush();
		   		byte[] imageInByte = baos.toByteArray();
		   		baos.close();
		   		
		   		response1.setImage(Base64.encodeBase64String(imageInByte));
	    		  
			}
	        
	      }
	    }
	    	    
	  }catch(Exception e) {
		  e.printStackTrace();
	  }
	  
	  return response1;
	}	

}

