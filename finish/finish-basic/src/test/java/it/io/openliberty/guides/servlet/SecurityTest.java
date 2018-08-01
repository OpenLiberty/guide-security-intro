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


public class SecurityTest {
    private Client client;

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
        URL = "http://localhost:" + port + "/" + war + "/";
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

}
// end::security[]
