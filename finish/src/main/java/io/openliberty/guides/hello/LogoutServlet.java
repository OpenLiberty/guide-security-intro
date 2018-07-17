import java.io.IOException;
import javax.ws.rs.GET;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // request.logout();
        System.out.println(request.getSession(false));

        request.getSession(false).invalidate();
        System.out.println(request.getSession(false));
        System.out.println("logged out and invalidated");
    			// HttpSession session = request.getSession(false);
    			// if (session != null){
    			// 		session.invalidate();

    			// }

        response.sendRedirect(request.getContextPath() + "/index.html");
    			
    }

}