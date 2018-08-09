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

import static org.junit.Assert.*;
import java.io.*;
import java.util.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


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
        URL = "http://localhost:" + port + "/" + "ServletSample/";
        // end::URL[]
        
    }

    @Before
    public void setup(){
        client = ClientBuilder.newClient();
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
        httpclient = new DefaultHttpClient(httpParams);
    }

    @After 
    public void teardown() {
        httpclient.getConnectionManager().shutdown();
        client.close();
    }

    @Test
    public void testSuite(){
        this.testCorrectAdmin();
        this.testCorrectUser();
        this.testIncorrectUser();
    }

    public void testCorrectAdmin(){
        int expectedResponseStatus = 200;
        int actualResponseStatus = logIn(ADMIN_NAME, ADMIN_SERVLET);
        assertEquals(expectedResponseStatus, actualResponseStatus);
    }

    public void testCorrectUser(){
        int expectedResponseStatus = 200;
        int actualResponseStatus = logIn(USER_NAME, USER_SERVLET);
        assertEquals(expectedResponseStatus, actualResponseStatus);
    }

    public void testIncorrectAuthorization(){
        int expectedResponseStatus = 403;
        int actualResponseStatus = logIn(USER_NAME, ADMIN_SERVLET);
        assertEquals(expectedResponseStatus, actualResponseStatus);
    }

    public void testIncorrectUser(){
        int expectedResponseStatus = 401;
        int actualResponseStatus = logIn(INCORRECT_NAME, USER_SERVLET);
        assertEquals(expectedResponseStatus, actualResponseStatus);
    }


    public int logIn(String usernameAndPassword, String servlet){
        String authHeader = "Basic "
            + java.util.Base64.getEncoder()
                              .encodeToString(usernameAndPassword.getBytes());

        Response response = client.target("http://localhost:9080/ServletSample/" + servlet)
                                         .request(MediaType.APPLICATION_JSON)
                                         .header("Authorization",
                                             authHeader)
                                         .get();

        int loginResponseValue = response.getStatus();
        response.close();

        return loginResponseValue;

    }


    public void testForm() throws Exception{
        int expectedStatus = 200;
        int actualStatus = executeFormLogin(httpclient, "http://localhost:9080/ServletSample/adminonly", "bob", "bobpwd");
        System.out.println(actualStatus);
        assertEquals(expectedStatus, actualStatus);

    }

    public int executeFormLogin(HttpClient httpclient, String url, String username, String password) throws Exception{
        
        HttpPost postMethod = new HttpPost(url);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("j_username", username));
        nvps.add(new BasicNameValuePair("j_password", password));
        
        postMethod.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = httpclient.execute(postMethod);

        int status = response.getStatusLine().getStatusCode();
        return status;
    }

}
// end::security[]
