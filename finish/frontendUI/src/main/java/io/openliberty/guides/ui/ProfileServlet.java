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
package io.openliberty.guides.ui;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.HttpConstraint;
import javax.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.SecurityContext;
import javax.inject.Inject;


@WebServlet(urlPatterns="/profile")
@FormAuthenticationMechanismDefinition(loginToContinue = @LoginToContinue(
  errorPage = "/error.html", loginPage = "/login.html"))
@ServletSecurity(value = @HttpConstraint(rolesAllowed = { "user", "admin" },
transportGuarantee=ServletSecurity.TransportGuarantee.CONFIDENTIAL))


public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

   @Inject
   private SecurityContext securityContext;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

      PrintWriter pw = response.getWriter();

      if (securityContext.isCallerInRole("admin")) {
        pw.write("User has role 'admin'" + "\n");
      }
      else if (securityContext.isCallerInRole("user")){
        pw.write("User has role 'user'"  + "\n");
      }

      String contextName = null;
      if (securityContext.getCallerPrincipal() != null) {
          contextName = securityContext.getCallerPrincipal().getName();
      }
      pw.write("Username: " + contextName + "\n");


    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }
}
// end::security[]
