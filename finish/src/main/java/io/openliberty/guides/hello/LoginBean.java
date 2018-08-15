// // tag::copyright[]
// /*******************************************************************************
//  * Copyright (c) 2018 IBM Corporation and others.
//  * All rights reserved. This program and the accompanying materials
//  * are made available under the terms of the Eclipse Public License v1.0
//  * which accompanies this distribution, and is available at
//  * http://www.eclipse.org/legal/epl-v10.html
//  *
//  * Contributors:
//  *     IBM Corporation - Initial implementation
//  *******************************************************************************/
// // end::copyright[]
// // tag::jwt[]
// package io.openliberty.guides.hello;
//
// import java.util.Set;
// import java.util.HashSet;
// import javax.servlet.*;
// import javax.servlet.http.*;
// import javax.faces.bean.ManagedBean;
// import javax.faces.bean.ViewScoped;
//
// import javax.faces.context.FacesContext;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpSession;
//
//
// @ManagedBean
// @ViewScoped
// public class LoginBean {
//
//     private String username;
//     private String password;
//
//     public void setUsername(String username) {
//         this.username = username;
//     }
//
//     public void setPassword(String password) {
//         this.password = password;
//     }
//
//     public String getUsername() {
//         return username;
//     }
//
//     public String getPassword() {
//         return password;
//     }
//
//     public String doLogin() throws Exception {
//
//         FacesContext context = FacesContext.getCurrentInstance();
//         HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
//
//         // do login
//         try {
//             request.logout();
//             System.out.println(this.username + " " + this.password);
//             request.login(this.username, this.password);
//         } catch (ServletException e) {
//             System.out.println("Login failed.");
//             return "error.jsf";
//         }
//
//         // to get remote user using getRemoteUser()
//         String remoteUser = request.getRemoteUser();
//
//         return "servlet";
//     }
//
//
// }
// // end::jwt[]
