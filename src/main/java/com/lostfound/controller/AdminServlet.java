package com.lostfound.controller;

import java.io.IOException;
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

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ItemDAO itemDAO;

    public void init() {
        itemDAO = new ItemDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String status = request.getParameter("status");
        if (status == null) status = "PENDING_APPROVAL";

        List<Item> items = itemDAO.getItemsByStatus(status);
        request.setAttribute("items", items);
        request.setAttribute("currentStatus", status);
        request.getRequestDispatcher("admin_dashboard.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("update_status".equals(action)) {
            int itemId = Integer.parseInt(request.getParameter("item_id"));
            String status = request.getParameter("status");
            itemDAO.updateItemStatus(itemId, status);
            response.sendRedirect("admin?status=PENDING_APPROVAL");
        }
    }
}
