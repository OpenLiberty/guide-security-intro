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
package io.openliberty.guides.frontend;

import java.util.Set;
import java.util.HashSet;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;
import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static javax.security.enterprise.AuthenticationStatus.SEND_CONTINUE;
import static javax.security.enterprise.AuthenticationStatus.SEND_FAILURE;
import static javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;




import io.openliberty.guides.frontend.SessionUtils;

@ManagedBean
@ViewScoped

@FacesConfig(version = JSF_2_3)
@Named
@RequestScoped
  public class LoginBean {

    @Inject 
    private SecurityContext securityContext;

    @Inject
    private FacesContext facesContext;

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

        private static HttpServletResponse getResponseFrom(FacesContext context) {
        return (HttpServletResponse) context
            .getExternalContext()
            .getResponse();
    }
    
    private static HttpServletRequest getRequestFrom(FacesContext context) {
        return (HttpServletRequest) context
            .getExternalContext()
            .getRequest();
    }
    
    private static void addError(FacesContext context, String message) {
        context
            .addMessage(
                null, 
                new FacesMessage(SEVERITY_ERROR, message, null));
    }

    public void doLogin() {
        Credential credential = new UsernamePasswordCredential(this.username, this.password);

        AuthenticationStatus status = securityContext.authenticate(
            getRequestFrom(facesContext),
            getResponseFrom(facesContext),
            withParams().credential(credential));
        if (status.equals(SEND_CONTINUE)) {
    
            facesContext.responseComplete();
            //         try{
            //     facesContext.getCurrentInstance().getExternalContext().redirect("index.jsf");
            //     return;
            // } catch (IOException e){
            //  //error
            // }

            
        } else if (status.equals(SEND_FAILURE)) {
            addError(facesContext, "Authentication failed");
        }


        // HttpServletRequest request = SessionUtils.getRequest();
        // try {
        //     request.logout();
        //     request.login(this.username, this.password);
        // } catch (ServletException e) {
        //     return "no";
        // }
        // return "yes";
        
        // do login
        // try {
        //     request.logout();
        //     request.login(this.username, this.password);
            
        // } catch (ServletException e) {
        //     System.out.println("Login failed.");

        //     return "error.jsf";
        // }

        // return "index.jsf";
    }

}
