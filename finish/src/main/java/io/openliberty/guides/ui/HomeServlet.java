// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
// end::copyright[]
// tag::Servlet[]
package io.openliberty.guides.ui;

import java.io.IOException;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/home")
// tag::AuthenticationMechanism[]
@FormAuthenticationMechanismDefinition(
    // tag::loginToContinue[]
    // tag::errorPage[]
    loginToContinue = @LoginToContinue(errorPage = "/error.html", 
    // end::errorPage[]
                                        // tag::loginPage[]
                                       loginPage = "/welcome.html"))
                                        // end::loginPage[]
    // end::loginToContinue[]
// end::AuthenticationMechanism[]
// tag::ServletSecurity[]
// tag::HttpConstraint[]
@ServletSecurity(value = @HttpConstraint(rolesAllowed = { "user", "admin" },
// end::HttpConstraint[]
  // tag::TransportGuarantee[]
  transportGuarantee = ServletSecurity.TransportGuarantee.CONFIDENTIAL)) 
  // end::TransportGuarantee[]
// end::ServletSecurity[]
// tag::HomeServlet[]
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    private SecurityContext securityContext;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    // tag::doGet[]
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // tag::CallerInRole[]
        if (securityContext.isCallerInRole(Utils.ADMIN)) {
            response.sendRedirect("/admin.jsf");
        // end::CallerInRole[]
        } else if  (securityContext.isCallerInRole(Utils.USER)) {
            response.sendRedirect("/user.jsf");
        }
    }
    // end::doGet[]

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        doGet(request, response);
    }
}
// end::HomeServlet[]
// end::Servlet[]
