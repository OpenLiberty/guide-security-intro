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
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.Before;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.client.params.ClientPNames;
import it.io.openliberty.guides.security.util.TestUtils;

public class SecurityTest {

  private static String urlHttp;
  private static String urlHttps;

  private DefaultHttpClient httpclient;
  private boolean redirect;
  private String loginUrl;
  private String loginTitle;
  private String formUrl;
  TestUtils helper = new TestUtils();

  @Before
  public void setup() throws Exception {

    HttpParams httpParams = new BasicHttpParams();
    httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
    httpclient = new DefaultHttpClient(httpParams);

    urlHttp = "http://" + System.getProperty("liberty.test.hostname") + ":"
        + System.getProperty("liberty.test.port");

    // use https url in the future
    urlHttps = "https://" + System.getProperty("liberty.test.hostname") + ":"
        + System.getProperty("liberty.test.ssl.port");

    redirect = false;
    formUrl = urlHttp + "/j_security_check";
    loginUrl = urlHttp + "/login.jsf";
    loginTitle = "Security Guide Form Login";

  }

  @Test
  public void testAuthenticationFail() throws Exception {
    executeGetRequestFormCreds(httpclient, urlHttp + "/profile", "bob",
        "wrongpassword", HttpServletResponse.SC_OK, "Security Login Error");
  }

  @Test
  public void testAuthenticationSucceed() throws Exception {
    executeGetRequestFormCreds(httpclient, urlHttp + "/profile", "bob", "bobpwd",
        HttpServletResponse.SC_OK, "Username: bob");
  }

  @Test
  public void testAuthorizationForAdmin() throws Exception {
    executeGetRequestFormCreds(httpclient, urlHttp + "/application", "bob", "bobpwd",
        HttpServletResponse.SC_OK, "Application Page");
  }

  @Test
  public void testAuthorizationForUser() throws Exception {
    executeGetRequestFormCreds(httpclient, urlHttp + "/application", "alice",
        "alicepwd", HttpServletResponse.SC_FORBIDDEN,
        "Error 403: AuthorizationFailed");
  }

  protected void executeGetRequestFormCreds(DefaultHttpClient httpClient,
      String resourceUrl, String userid, String password, int expectedStatusCode,
      String expectedContent) throws Exception {
    // Send servlet query to get form login page.
    String content = helper.getFormLoginPage(httpClient, resourceUrl, redirect,
        loginUrl, loginTitle);

    // Execute Form login and get redirect location.
    String location = helper.executeFormLogin(httpClient, formUrl, userid, password,
        true);

    // Redirect to the given page, ensure it is the original servlet request and
    // it returns the right response.
    HttpResponse response = helper.accessPage(httpClient, location);

    // Process the response from an http invocation, such as validating the
    // status code, extracting the response entity...
    HttpEntity entity = response.getEntity();
    String contentAfterLogin = EntityUtils.toString(entity);
    EntityUtils.consume(entity);

    int actualStatusCode = response.getStatusLine().getStatusCode();

    assertEquals("Expected " + expectedStatusCode + " was not returned",
        expectedStatusCode, actualStatusCode);

    assertTrue("Response did not contain expected content (" + expectedContent + ")",
        contentAfterLogin.contains(expectedContent));
  }

}
// end::test[]
