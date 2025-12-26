package com.lostfound.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/dashboard.jsp", "/post_item.jsp", "/admin_dashboard.jsp", "/myClaims.jsp", "/adminClaims.jsp", "/matches.jsp"})
public class AuthFilter implements Filter {

    public void init(FilterConfig filterConfig) {}

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        if (loggedIn) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect("login.jsp");
        }
    }

    public void destroy() {}
}
