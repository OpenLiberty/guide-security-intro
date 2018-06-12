package io.openliberty.guides.eventapp.ui;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;


import io.openliberty.guides.eventapp.models.User;
import io.openliberty.guides.eventapp.facelets.PageDispatcher;
import io.openliberty.guides.eventapp.lists.UserList;
import io.openliberty.guides.eventapp.ui.util.SessionUtils;
import io.openliberty.guides.eventapp.resources.UserService;
import com.ibm.websphere.security.web.WebSecurityHelper;

import com.ibm.websphere.security.jwt.*;

import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
// import org.glassfish.soteria.identitystores.annotation.EmbeddedIdentityStoreDefinition;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;

@ManagedBean
@ViewScoped
@FormAuthenticationMechanismDefinition(
  loginToContinue = @LoginToContinue(
    loginPage = "/login.jsf",
    errorPage = "/loginerror.jsf",
    useForwardToLogin = true)
  )

public class LoginBean {

  private String username;
  private String password;

  List<String> revokeList = readConfig();

  @ManagedProperty(value = "#{pageDispatcher}")
  public PageDispatcher pageDispatcher;

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

  public PageDispatcher getPageDispatcher() {
    return pageDispatcher;
  }

  public void setPageDispatcher(PageDispatcher pageDispatcher) {
    this.pageDispatcher = pageDispatcher;
  }

  public String doLogIn() {
    String returnPage = "eventmanager.jsf";

    HttpServletRequest request = SessionUtils.getRequest();

    // do filter
    if (revokeList.contains(username)) {
      System.out.println("User is blocked.");
      pageDispatcher.showLogBlocked();
      return returnPage;
    }

    // do login
    try {
      request.login(this.username, this.password);
    } catch (ServletException e) {
      System.out.println("Login failed.");
      pageDispatcher.showLogError();
      return returnPage;
    }

    String remoteUser = request.getRemoteUser();
    String role = getRole(request);
    System.out.println("AFTER LOGIN, REMOTE USER: " + remoteUser + " " + role);

   // update session
    if (remoteUser != null && remoteUser.equals(username)){
        updateSessionUser(request, role);
    } else {
      System.out.println("Update Sessional User Failed.");
    }

    pageDispatcher.showMainPage();
    return returnPage;
  }


  private String getRole(HttpServletRequest request) {
    // to check if remote user is granted admin role
    boolean isAdmin = request.isUserInRole("eventAdministrator");
    if (isAdmin) {
      return "eventAdministrator";
    }
    return "registeredUser";
  }

  /**
   * updateSessionUser() : Update currently logged in user's info
   */
  private void updateSessionUser(HttpServletRequest request, String role) {

    // get the current session
    HttpSession ses = request.getSession(false);
    if (ses == null) {
      System.out.println("Session is timeout.");
    }

    User user = new User(username, "", password, role);
    ses.setAttribute("user", user); // important to set it here!

    UserList userlist = UserService.userList;
    User element = userlist.getUser(username);

    if (element == null) {
      userlist.addUser(user);
      System.out.println("created and added new login user " + username
          + " "+ password + " " + role);
    }
  }

  /**
   * readConfig() : Reads revoked user list file and creates a revoked user
   * list.
   */
  private List<String> readConfig() {
    List<String> list = new ArrayList<String>();
    String filename = "revokedUsers.lst";
    String filepath = System.getProperty("user.dir").split("target")[0] + filename;
    System.out.println(filepath);

    // get the revoked user list file and open it.
    BufferedReader in;
    try {
      in = new BufferedReader(new FileReader(filepath));
    } catch (FileNotFoundException fnfe) {
      return list;
    }
    // read all the revoked users and add to revokeList.
    String userName;
    try {
      while ((userName = in.readLine()) != null)
        list.add(userName);
    } catch (IOException ioe) {
      return list;
    }
    return list;
  }

}
