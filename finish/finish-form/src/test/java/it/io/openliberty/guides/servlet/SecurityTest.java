// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
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
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class SecurityTest {
    private Client client;
    private HttpClient httpclient;

    private static String URL;
    private final String ADMIN_NAME = "bob:bobpwd";
    private final String ADMIN_SERVLET = "adminonly";

    private final String USER_NAME = "alice:alicepwd";
    private final String USER_SERVLET = "servlet";

    private final String INCORRECT_NAME = "carl:carlpwd";
 

 
    @BeforeClass
    public static void init() {

        // tag::URL[]
        String port = System.getProperty("liberty.test.port");
        String war = System.getProperty("war.name");
        // end::URL[]
        
    }

    @Before
    public void setup(){
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
        httpclient = new DefaultHttpClient(httpParams);
    }

    @After 
    public void teardown() {
        httpclient.getConnectionManager().shutdown();
    }

    @Test
    public void testSuite() throws Exception{
        this.testForm();

    }

    //HttpClient httpclient, String url, String username, String password, boolean redirect, String description, String[] cookies

    public void testForm() throws Exception{
        int expectedStatus = 200;
        String actualStatus = executeFormLogin(httpclient, "http://localhost:9090/ServletSample/adminonly", "bob", "bobpwd", false, null, null);
        System.out.println(actualStatus);
        assertEquals(0, 0);

    }

    // public int executeFormLogin(HttpClient httpclient, String url, String username, String password) throws Exception{
        
    //     HttpPost postMethod = new HttpPost(url);

    //     List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    //     nvps.add(new BasicNameValuePair("j_username", username));
    //     nvps.add(new BasicNameValuePair("j_password", password));
        
    //     postMethod.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    //     HttpResponse response = httpclient.execute(postMethod);

    //     int status = response.getStatusLine().getStatusCode();
    //     return status;
    // }


    protected Header getCookieHeader(HttpResponse response, String cookieName) {

        String methodName = "getCookie";

        // Log.info(logClass, methodName, response.toString() + ", cookieName=" + cookieName);

        Header[] setCookieHeaders = response.getHeaders("Set-Cookie");

        if (setCookieHeaders == null) {

            fail("There must be Set-Cookie headers.");

        }

        for (Header header : setCookieHeaders) {

            // Log.info(logClass, methodName, "Header: " + header);

            for (HeaderElement e : header.getElements()) {

                if (e.getName().equals(cookieName)) {

                    return header;

                }

            }

        }

        fail("Set-Cookie for " + cookieName + " not found.");

        return null;

    }


    public void assertCookie(String cookieHeaderString, boolean secure, boolean httpOnly) {

        assertTrue("The Path parameter must be set.", cookieHeaderString.contains("Path=/"));

        assertEquals("The Secure parameter must" + (secure == true ? "" : " not" + " be set."), secure, cookieHeaderString.contains("Secure"));

        assertEquals("The HttpOnly parameter must" + (httpOnly == true ? "" : " not" + " be set."), httpOnly, cookieHeaderString.contains("HttpOnly"));

    }

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

        System.out.println("RESPONSE RESULT)");
        System.out.println(response.getStatusLine().getStatusCode());



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
