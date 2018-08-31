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
package io.openliberty.guides.ui;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.faces.view.ViewScoped;

import io.openliberty.guides.ui.models.SystemModel;
import io.openliberty.guides.ui.util.ServiceUtils;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.Serializable;

@Named
@ViewScoped
public class ApplicationBean implements Serializable {

  private static final long serialVersionUID = 1L;

  private String hostname;

  public String getOs() {

    if (ServiceUtils.responseOk()) {
      JsonObject properties = ServiceUtils.getProperties();
      return properties.getString("os.name");
    }
    return "You are not authorized to access the system service.";
  }

  public String getInventorySize() {

    if (ServiceUtils.invOk()) {
      JsonObject properties = ServiceUtils.getInventory();
      return String.valueOf(properties.getInt("total"));
    }
    return "You are not authorized to access the inventory service.";
  }

  public List<SystemModel> getInventoryList() {

    if (ServiceUtils.invOk()) {
      JsonArray systems = ServiceUtils.getInventory().getJsonArray("systems");
      return systems.stream().map(s -> new SystemModel((JsonObject) s))
                    .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;

    if (ServiceUtils.invOk()) {
      ServiceUtils.addSystem(hostname);
    }
  }
}
