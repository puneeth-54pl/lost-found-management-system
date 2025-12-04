package com.lostfound.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lostfound.dao.UserDAO;
import com.lostfound.model.User;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserDAO userDAO;

	public void init() {
		userDAO = new UserDAO();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");

		if ("register".equals(action)) {
			registerUser(request, response);
		} else if ("login".equals(action)) {
			loginUser(request, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("logout".equals(action)) {
			logoutUser(request, response);
		}
	}

	private void registerUser(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirm_password");

		if (!password.equals(confirmPassword)) {
			request.setAttribute("error", "Passwords do not match!");
			request.getRequestDispatcher("register.jsp").forward(request, response);
			return;
		}

		if (userDAO.isUsernameTaken(username)) {
			request.setAttribute("error", "Username already exists!");
			request.getRequestDispatcher("register.jsp").forward(request, response);
			return;
		}

		User newUser = new User(username, password, email, "user");
		if (userDAO.registerUser(newUser)) {
			response.sendRedirect("login.jsp?success=Registration successful! Please login.");
		} else {
			request.setAttribute("error", "Registration failed. Try again.");
			request.getRequestDispatcher("register.jsp").forward(request, response);
		}
	}

	private void loginUser(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String username = request.getParameter("username");
		String pass = request.getParameter("password");

		User user = userDAO.checkLogin(username, pass);

		if (user != null) {
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			if ("admin".equals(user.getRole())) {
				response.sendRedirect("admin_dashboard.jsp");
			} else {
				response.sendRedirect("dashboard.jsp");
			}
		} else {
			request.setAttribute("error", "Invalid username or password");
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}

	private void logoutUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		response.sendRedirect("index.jsp");
	}
}
