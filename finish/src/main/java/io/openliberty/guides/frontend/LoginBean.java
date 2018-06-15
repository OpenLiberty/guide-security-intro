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
// tag::jwt[]
package io.openliberty.guides.frontend;

import java.util.Set;
import java.util.HashSet;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;


import io.openliberty.guides.frontend.SessionUtils;

@ManagedBean
@ViewScoped
// @BasicAuthenticationMechanismDefinition(
//     realmName = "myRealm")
@FormAuthenticationMechanismDefinition(
  loginToContinue = @LoginToContinue(
    loginPage = "/login.jsf",
    errorPage = "/loginerror.jsf",
    useForwardToLogin = true)
  )
  public class LoginBean {

    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String doLogin() throws Exception {
        HttpServletRequest request = SessionUtils.getRequest();

        // do login
        try {
            request.logout();
            request.login(this.username, this.password);
        } catch (ServletException e) {
            System.out.println("Login failed.");
            return "error.jsf";
        }

        return "index.xhtml";
    }

}
