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
// tag::userbean[]
package io.openliberty.guides.ui;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;

@Named("userBean")
@RequestScoped

public class UserBean implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private SecurityContext securityContext;

  public String getUsername() {
    return securityContext.getCallerPrincipal().getName();
  }

  public String getRoles() {
  String roles = "";
    if (securityContext.isCallerInRole(Utils.ADMIN)) {
      roles = Utils.ADMIN;
    }
    if (securityContext.isCallerInRole(Utils.USER)) {
      if (!roles.isEmpty())
        roles += ", ";
      roles += Utils.USER;
    }
    return roles;
  }
}
// end::userbean[]
