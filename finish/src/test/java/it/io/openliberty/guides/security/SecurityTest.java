// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::test[]
package it.io.openliberty.guides.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

public class SecurityTest {

    private static String urlHttp;
    private static String urlHttps;

    @Before
    public void setup() throws Exception {
        urlHttp = "http://localhost:" + System.getProperty("liberty.test.port");
        urlHttps = "https://localhost:" + System.getProperty("liberty.test.ssl.port");
        TestUtils.trustAll();
    }

    @Test
    public void testAuthenticationFail() throws Exception {
        executeURL("/", "bob", "wrongpassword", true, -1, "Don't care");
        System.out.println("testAuthenticationFail passed!");
    }

    @Test
    public void testAuthorizationForAdmin() throws Exception {
        executeURL("/", "bob", "bobpwd", false, 
            HttpServletResponse.SC_OK, "admin, user");
        System.out.println("testAuthorizationForAdmin passed!");
    }

    @Test
    public void testAuthorizationForUser() throws Exception {
        executeURL("/", "alice", "alicepwd", false, 
            HttpServletResponse.SC_OK, "<title>User</title>");
        System.out.println("testAuthorizationForUser passed!");
    }

    @Test
    public void testAuthorizationFail() throws Exception {
        executeURL("/", "dave", "davepwd", false, 
            HttpServletResponse.SC_FORBIDDEN, "Error 403: AuthorizationFailed");
        System.out.println("testAuthorizationFail passed!");
    }

    private void executeURL(
        String testUrl, String userid, String password,
        boolean expectLoginFail, int expectedCode, String expectedContent) 
        throws Exception {

        // Use HttpClient to execute the testUrl by HTTP
        URI url = new URI(urlHttp + testUrl);
        HttpGet getMethod = new HttpGet(url);
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        SSLContext sslContext = SSLContext.getDefault();
        clientBuilder.setSSLContext(sslContext);
        clientBuilder.setDefaultRequestConfig(
            RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
        HttpClient client = clientBuilder.build();
        HttpResponse response = client.execute(getMethod);

        // The response should return the login.html
        String loginBody = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertTrue(
            "Not redirected to home.html", 
            loginBody.contains("window.location.assign"));
        String[] redirect = loginBody.split("'");

        // Use j_security_check to login
        HttpPost postMethod = new HttpPost(urlHttps + "/j_security_check");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("j_username", userid ));
        nvps.add(new BasicNameValuePair("j_password", password));
        postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        response = client.execute(postMethod);
        assertEquals(
            "Expected " + HttpServletResponse.SC_FOUND + " status code for login",
        HttpServletResponse.SC_FOUND, response.getStatusLine().getStatusCode());

        // Return if the login fails
        if (expectLoginFail) {
            String location = response.getFirstHeader("Location").getValue();
            assertTrue(
                "Error.html was not returned", 
                location.contains("error.html"));
            return;
        }

        // Use HttpClient to execute the redirected url
        url = new URI(urlHttps + redirect[1]);
        getMethod = new HttpGet(url);
        response = client.execute(getMethod);
        assertEquals(
            "Expected " + expectedCode + " status code for login",
            expectedCode, response.getStatusLine().getStatusCode());

        // Return if not SC_OK
        if (expectedCode != HttpServletResponse.SC_OK) {
            return;
        }

        // Check the content of the response returned
        String actual = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertTrue(
            "The actual content did not contain the userid \"" + userid + 
            "\". It was:\n" + actual,
        actual.contains(userid));
        assertTrue(
            "The url " + testUrl + 
            " did not return the expected content \"" + expectedContent + "\"" +
            "The actual content was:\n" + actual, 
        actual.contains(expectedContent));
    }

}
// end::test[]
