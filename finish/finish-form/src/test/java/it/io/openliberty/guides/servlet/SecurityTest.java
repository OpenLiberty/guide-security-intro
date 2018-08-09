// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
// end::copyright[]
// tag::security[]
package it.io.openliberty.guides.servlet;

import org.apache.http.util.EntityUtils;

import static org.junit.Assert.*;
import java.io.*;
import java.util.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.http.client.params.ClientPNames;

import javax.servlet.http.HttpServletResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SecurityTest {
  private Client client;
  private HttpClient httpclient;

  private static String BASE_URL;

  private final String ADMIN_NAME = "bob";
  private final String ADMIN_PASS = "bobpwd";
  private final String ADMIN_SERVLET = "adminonly";

  private final String USER_NAME = "alice";
  private final String USER_PASS = "alicepwd";
  private final String USER_SERVLET = "servlet";

  private final String INCORRECT_NAME = "carl";
  private final String INCORRECT_PASS = "carlpwd";

  @BeforeClass
  public static void init() {

    // tag::URL[]
    String port = System.getProperty("liberty.test.port");
    BASE_URL = "http://localhost:" + port + "/" +  "ServletSample/";

    // end::URL[]

  }

  @Before
  public void setup() {
    HttpParams httpParams = new BasicHttpParams();
    httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
    // httpclient = new DefaultHttpClient(httpParams);
    httpclient = new DefaultHttpClient();
  }

  @After
  public void teardown() {
    httpclient.getConnectionManager().shutdown();
  }

  @Test
  public void testSuite() throws Exception {
    this.testCorrectAdmin();
    this.testCorrectUser();
    this.testIncorrectUser();

  }

  public void testCorrectAdmin() throws Exception {
    httpclient = new DefaultHttpClient();

    int expectedStatus = 200;
    System.out.println(BASE_URL + ADMIN_SERVLET);
    int actualStatus = executeFormLogin(httpclient,
        BASE_URL + ADMIN_SERVLET, USER_NAME, USER_PASS);
    httpclient.getConnectionManager().shutdown();

    assertEquals(expectedStatus, actualStatus);

  }

  public void testCorrectUser() throws Exception {
    httpclient = new DefaultHttpClient();

    int expectedStatus = 200; 
    int actualStatus = executeFormLogin(httpclient, BASE_URL + USER_SERVLET, USER_NAME, USER_PASS);
    httpclient.getConnectionManager().shutdown();

    assertEquals(expectedStatus, actualStatus);
  }

  public void testIncorrectUser() throws Exception {
    httpclient = new DefaultHttpClient();

    int expectedStatus = 403;
    int actualStatus = executeFormLogin(httpclient, BASE_URL + USER_SERVLET, INCORRECT_NAME, INCORRECT_PASS);
    httpclient.getConnectionManager().shutdown();

    assertEquals(expectedStatus, actualStatus);
  }

  public void printPage(HttpResponse response) throws Exception{
      // System.out.println(response.getStatus());
      BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) response.getEntity().getContent()));
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

  // public int executeFormLogin(HttpClient httpclient, String url, String
  // username, String password) throws Exception{

  // HttpPost postMethod = new HttpPost(url);

  // List<NameValuePair> nvps = new ArrayList<NameValuePair>();
  // nvps.add(new BasicNameValuePair("j_username", username));
  // nvps.add(new BasicNameValuePair("j_password", password));

  // postMethod.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

  // HttpResponse response = httpclient.execute(postMethod);
  // System.out.println("Page: " + url + "\n" + "Username: " + username);
  // printPage(response); 

  // int status = response.getStatusLine().getStatusCode();
  // return status;
  // }

 public String executeFormLogin(HttpClient httpclient, String url, String username, String password, boolean redirect, String description, String[] cookies) throws Exception {
        String methodName = "executeFormLogin";
        // Log.info(logClass, methodName, "Submitting Login form (POST) =  " + url + " username =" + username + " password=" + password + " description=" + description);

        HttpPost postMethod = new HttpPost(url);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("j_username", username));
        nvps.add(new BasicNameValuePair("j_password", password));
        if (description != null)
            nvps.add(new BasicNameValuePair("j_description", description));

        postMethod.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = httpclient.execute(postMethod);

        // Log.info(logClass, methodName, "postMethod.getStatusCode():  " + response.getStatusLine().getStatusCode());
        // Log.info(logClass, methodName, "postMethod response: " + response.toString());

        EntityUtils.consume(response.getEntity());

        String location = "No Redirect";
        if (redirect) {
            // Verify redirect to servlet
            int status = response.getStatusLine().getStatusCode();
            assertTrue("Form login did not result in redirect: " + status, status == HttpServletResponse.SC_MOVED_TEMPORARILY);
            Header header = response.getFirstHeader("Location");

            location = header.getValue();
            // Log.info(logClass, methodName, "Redirect location:  " + location);
            // Log.info(logClass, methodName, "Modified Redirect location:  " + location);
        } else {
            // Verify we got a 200 from the servlet
            int status = response.getStatusLine().getStatusCode();
            assertTrue("Form login did not result in redirect: " + status, status == HttpServletResponse.SC_OK);
        }
        if (cookies != null) {
            for(String cookie : cookies) {
                Header cookieHeader = getCookieHeader(response, cookie);
                assertCookie(cookieHeader.toString(), false, true);
            }
        }
        return location;
    }

}

// end::security[]
