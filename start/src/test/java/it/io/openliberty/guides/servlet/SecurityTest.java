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
    }

    @After
    public void teardown() {
        client.close();
    }

    @Test
    public void testSuite(){
        this.testCorrectAdmin();
        this.testCorrectUser();
        this.testIncorrectAuthorization();
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


    public int logIn(String usernameAndPassword, String servlet){
        String authHeader = "Basic "
            + java.util.Base64.getEncoder()
                              .encodeToString(usernameAndPassword.getBytes());
        System.out.println("Basic AUTH to " + "http://localhost:9080/ServletSample/" + servlet + "\n Response code: ");
        Response response = client.target("http://localhost:9080/ServletSample/" + servlet)
                                         .request(MediaType.APPLICATION_JSON)
                                         .header("Authorization",
                                             authHeader)
                                         .get();

        int loginResponseValue = response.getStatus();
        printPage(response);
        response.close();

        return loginResponseValue;

    }

}
// end::security[]
