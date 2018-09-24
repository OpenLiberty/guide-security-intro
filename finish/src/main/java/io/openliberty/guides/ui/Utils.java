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
package io.openliberty.guides.ui;

import java.io.PrintWriter;

public class Utils {

  public static final String ADMIN = "admin";
  public static final String USER = "user";
  public static final String UNKNOWN = "unknown";
  
  public static void contructHTML(PrintWriter pw, String title, String role, String contextName) {
    pw.write("<html>");
    pw.write("<title>" + title + "</title>");
    pw.write("<h2>" + title + "</h2>");
    pw.write("<body>");
    pw.write("Username: " + contextName + "<br/>");
    if (role != null)
    	pw.write("Role: " + role + "<br/>");
    pw.write("<br/><a href=\"/\">Home</a>");
    pw.write("</body>");
    pw.write("</html>");
  }

}

