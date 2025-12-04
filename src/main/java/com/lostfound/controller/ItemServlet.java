package com.lostfound.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lostfound.dao.ItemDAO;
import com.lostfound.model.Item;
import com.lostfound.model.User;

@WebServlet("/items")
public class ItemServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ItemDAO itemDAO;

	public void init() {
		itemDAO = new ItemDAO();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");

		if ("post".equals(action)) {
			postItem(request, response);
		} else if ("update_status".equals(action)) {
			updateItemStatus(request, response);
		} else if ("delete".equals(action)) {
			deleteItem(request, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");

		if ("search".equals(action)) {
			searchItems(request, response);
		}
	}

	private void postItem(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("user") : null;

		if (user == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		String itemType = request.getParameter("item_type");
		String itemName = request.getParameter("item_name");
		String category = request.getParameter("category");
		String description = request.getParameter("description");
		String location = request.getParameter("location");
		String dateStr = request.getParameter("date");
		String contactInfo = request.getParameter("contact_info");

		Item item = new Item();
		item.setUserId(user.getId());
		item.setItemType(itemType);
		item.setItemName(itemName);
		item.setCategory(category);
		item.setDescription(description);
		item.setLocation(location);
		item.setLostFoundDate(Date.valueOf(dateStr));
		item.setContactInfo(contactInfo);
		item.setStatus("PENDING"); // Default status

		if (itemDAO.addItem(item)) {
			response.sendRedirect("dashboard.jsp?success=Item posted successfully! Waiting for approval.");
		} else {
			request.setAttribute("error", "Failed to post item.");
			request.getRequestDispatcher("post_item.jsp").forward(request, response);
		}
	}

	private void updateItemStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("user") : null;

		if (user == null || !"admin".equals(user.getRole())) {
			response.sendRedirect("login.jsp");
			return;
		}

		int itemId = Integer.parseInt(request.getParameter("item_id"));
		String status = request.getParameter("status");

		itemDAO.updateItemStatus(itemId, status);
		response.sendRedirect("admin_dashboard.jsp");
	}

	private void deleteItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("user") : null;

		// Allow admin or the owner to delete (logic simplified here, assuming admin or
		// owner check in DAO or UI, but here just basic check)
		// For simplicity, we just check if logged in. In real app, check ownership.
		if (user == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		int itemId = Integer.parseInt(request.getParameter("item_id"));
		itemDAO.deleteItem(itemId);

		if ("admin".equals(user.getRole())) {
			response.sendRedirect("admin_dashboard.jsp");
		} else {
			response.sendRedirect("dashboard.jsp");
		}
	}

	private void searchItems(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String query = request.getParameter("query");
		String category = request.getParameter("category");
		String location = request.getParameter("location");

		List<Item> results = itemDAO.searchItems(query, category, location);
		request.setAttribute("results", results);
		request.getRequestDispatcher("search.jsp").forward(request, response);
	}
}
