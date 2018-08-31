// tag::copyright[]
/**
 * *****************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * <p>Contributors: IBM Corporation - Initial implementation
 * *****************************************************************************
 */
// end::copyright[]
package it.io.openliberty.guides.security.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.servlet.http.HttpServletResponse;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

  /**
   * Send HttpClient get request to the given URL, ensure that the user is
   * redirected or forwarded to the form login page Note that in order to use
   * this method properly, HttlClient needs to be set
   * ClientPNames.HANDLE_REDIRECTS=Boolean.FALSE. This propety let httpclient
   * disable following the redirect automatically.
   *
   * @param httpclient
   *          HttpClient object to execute request
   * @param url
   *          URL for request, should be protected and redirect to form login
   *          page
   * @param redirect
   *          true if redirect is used to go to the login page, otherwise, use
   *          forward.
   * @param formUrl
   *          Url of login page. this value is used when redirect is set as
   *          true.
   * @param formTitle
   *          Name of Login form.
   * @throws Exception
   */

  public String getFormLoginPage(DefaultHttpClient httpclient, String url,
      boolean redirect, String formUrl, String formTitle) throws Exception {

    String content = null;
    try {
      HttpGet getMethod = new HttpGet(url);
      HttpResponse response = httpclient.execute(getMethod);

      if (redirect) {
        assertEquals(
            "Expected " + HttpServletResponse.SC_MOVED_TEMPORARILY
                + " status code for form login page was not returned",
            HttpServletResponse.SC_MOVED_TEMPORARILY,
            response.getStatusLine().getStatusCode());
        // check page url.
        String location = response.getFirstHeader("Location").getValue();
        assertTrue(
            "Expected " + formUrl + " location for form login page was not returned",
            location.equals(formUrl));
        // now get the contents of the redirect url.
        EntityUtils.consume(response.getEntity());
        getMethod = new HttpGet(location);
        response = httpclient.execute(getMethod);
      }
      assertEquals(
          "Expected " + HttpServletResponse.SC_OK
              + " status code for form login page was not returned",
          HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());

      content = EntityUtils.toString(response.getEntity());
      EntityUtils.consume(response.getEntity());

      if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
        // Verify we get the form login JSF
        assertTrue("Did not find expected form login page: " + formTitle,
            content.contains(formTitle));
      }

    } catch (IOException e) {
      fail("Caught unexpected exception: " + e);
    }
    return content;
  }

  /**
   * Post HttpClient request to execute a form login on the given page, using
   * the given username and password
   *
   * @param httpclient
   *          HttpClient object to execute login
   * @param url
   *          URL for login page
   * @param username
   *          User name
   * @param password
   *          User password
   * @return URL of page redirected to after the login
   * @throws Exception
   */
  public String executeFormLogin(HttpClient httpclient, String url, String username,
      String password, boolean redirect) throws Exception {

    HttpPost postMethod = new HttpPost(url);

    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair("j_username", username));
    nvps.add(new BasicNameValuePair("j_password", password));

    postMethod.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    HttpResponse response = httpclient.execute(postMethod);

    EntityUtils.consume(response.getEntity());

    String location = "No Redirect";
    if (redirect) {
      // Verify redirect to servlet
      int status = response.getStatusLine().getStatusCode();
      assertTrue("Form login did not result in redirect: " + status,
          status == HttpServletResponse.SC_MOVED_TEMPORARILY);
      Header header = response.getFirstHeader("Location");

      location = header.getValue();

    } else {
      // Verify we got a 200 from the servlet
      int status = response.getStatusLine().getStatusCode();
      assertTrue("Form login did not result in redirect: " + status,
          status == HttpServletResponse.SC_OK);
    }

    return location;
  }

  /**
   *
   * @param client
   *          HttpClient object to execute login
   * @param location
   *          page url
   * @return response from the page
   */
  public HttpResponse accessPage(HttpClient client, String location) {
    HttpResponse response = null;
    try {
      // Get method on form login page
      HttpGet getMethod = new HttpGet(location);
      response = client.execute(getMethod);
    } catch (IOException e) {
      fail("Caught unexpected exception: " + e);
    }
    return response;
  }

}
