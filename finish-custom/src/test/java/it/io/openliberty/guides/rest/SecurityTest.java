package it.io.openlibert.guides.rest;

import static org.junit.Assert.*;
import java.io.*;
import java.util.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.json.JsonArray;
import javax.ws.rs.core.Form;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.xml.sax.InputSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Node;

public class SecurityTest {
	private static String httpPort;
	private static String baseHttpUrl;
	private static String loginUrl;

	private Client client;

	private final String SYSTEM_PROPERTIES = "system/properties";
	private final String APP_NAME = "LibertyProject";

	private final String ADMIN_NAME = "bob:bobpwd";

	private final String USER_NAME = "alice:alicepwd";

	private final String INCORRECT_NAME = "carl:carlpwd";

	@BeforeClass
	public static void oneTimeSetup(){

		httpPort = "9080";
		baseHttpUrl = "http://localhost:" + httpPort + "/" + "LibertyProject";
		System.out.println("BaseHttpUrl is : ");
		System.out.println(baseHttpUrl);
	}

	@Before
	public void setup(){
		client = ClientBuilder.newClient();
	}

	@After 
	public void teardown() {
		client.close();
	}

	@Test
	public void testSuite() throws Exception{
		// this.panagiotis();
		// this.testForm();
		// this.testCorrectAdmin();
		// this.testCorrectUser();
		// this.testIncorrectUser();
	}
	public void testCorrectAdmin(){
		int expectedResponseStatus = 200;
		int actualResponseStatus = logIn(ADMIN_NAME);
		assertEquals(expectedResponseStatus, actualResponseStatus);
	}

	public void testCorrectUser(){
		int expectedResponseStatus = 403;
		int actualResponseStatus = logIn(USER_NAME);
		assertEquals(expectedResponseStatus, actualResponseStatus);
	}

	public void testIncorrectUser(){
		int expectedResponseStatus = 401;
		int actualResponseStatus = logIn(INCORRECT_NAME);
		assertEquals(expectedResponseStatus, actualResponseStatus);
	}

	public void panagiotis() throws Exception{
		getViewState();
		assertEquals(1,1);
	}

	public void testForm(){
		int e = 0;
		// int a = 1;
		try {
				int a = testLogInHttpClient("bob:bobpwd");
						assertEquals(e,a);

		} catch (Exception ex){
			System.out.println("io exception");
			//do nothing
		}

	}

		//Helper function - to be removed
	 //Reads the page given the response
		public void printPage(Response response){
					System.out.println(response.getStatus());
				 BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) response.getEntity()));
	    List<String> result = new ArrayList<String>();
	    try {
	    	String input;
	    	while ((input = br.readLine()) != null){
	    		System.out.println(input);

	    		result.add(input);
	    	}
	    	br.close();
	    } catch (IOException e){
	    	e.printStackTrace();
	    	fail();
	    }
		}

		public void getViewState() throws Exception{
						Response response = client.target("http://localhost:9080/LibertyProject/primary.jsf")
																													.request(MediaType.TEXT_HTML)
																													.get();
        String xml = response.readEntity(String.class);;
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        // String body = response.readEntity(String.class);

        InputSource source = new InputSource(new StringReader(xml));
        Node n = (Node) xpath.evaluate("//input[@name='javax.faces.ViewState']", source, XPathConstants.NODE);
        String viewState = n.getAttributes().getNamedItem("value").getNodeValue();
        System.out.println(viewState);
        
    }

		// public void getViewState(){
			// Response response = client.target("http://localhost:9080/LibertyProject/primary.jsf")
			// 																										.request(MediaType.TEXT_HTML)
			// 																										.get();
			// String body = response.readEntity(String.class);
		// 	System.out.println(body);

		// 	InputSource source = new InputSource(body);
		// 	XPathFactory xpathFactory = XPathFactory.newInstance();
		// 	XPath xpath = xpathFactory.newXPath();

		// 	String viewStateValue = xpath.evaluate("//input[@name='javax.faces.ViewState']", source);
		// 	System.out.println("View state value:");
		// 	System.out.println(viewStateValue);
		// }


		public int logIn(String usernameAndPassword){
			String authHeader = "Basic "
	        + java.util.Base64.getEncoder()
	                          .encodeToString(usernameAndPassword.getBytes());

	  Response response = client.target("http://localhost:9080/LibertyProject/System/properties")
	                                     .request(MediaType.APPLICATION_JSON)
	                                     .header("Authorization",
	                                         authHeader)
	                                     .get();

    int loginResponseValue = response.getStatus();
    System.out.println("Printing the LibertyProject page:");
    printPage(response);
    response.close();

    return loginResponseValue;

		}

		public int testLogInHttpClient(String usernameAndPassword) throws Exception{
			//Sending GET
			String authHeader = "Form "
      + java.util.Base64.getEncoder()
                        .encodeToString(usernameAndPassword.getBytes());

   HttpClient client = new DefaultHttpClient();
   HttpGet request = new HttpGet("http://localhost:9080/LibertyProject/primary.jsf");

   request.addHeader("Form ", "bob:bobwd");

   HttpResponse response = client.execute(request);

   BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

   StringBuffer result = new StringBuffer();
   String line = "";
   while ((line = rd.readLine()) != null) {
   	result.append(line);
   }

   System.out.println(result.toString());

   return 0;

		}

}