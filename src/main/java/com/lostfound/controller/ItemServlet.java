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

import com.lostfound.dao.CategoryDAO;
import com.lostfound.dao.ItemDAO;
import com.lostfound.dao.LocationDAO;
import com.lostfound.dao.MatchDAO;
import com.lostfound.model.Category;
import com.lostfound.model.Item;
import com.lostfound.model.Location;
import com.lostfound.model.Match;
import com.lostfound.model.User;

@WebServlet("/items")
public class ItemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ItemDAO itemDAO;
    private MatchDAO matchDAO;
    private CategoryDAO categoryDAO;
    private LocationDAO locationDAO;

    public void init() {
        itemDAO = new ItemDAO();
        matchDAO = new MatchDAO();
        categoryDAO = new CategoryDAO();
        locationDAO = new LocationDAO();
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
        } else if ("form".equals(action)) {
            showPostForm(request, response);
        } else {
            // Default: List approved items (Home Page)
            listItems(request, response);
        }
    }

    private void showPostForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Category> categories = categoryDAO.getAllCategories();
        List<Location> locations = locationDAO.getAllLocations();
        request.setAttribute("categories", categories);
        request.setAttribute("locations", locations);
        request.getRequestDispatcher("post_item.jsp").forward(request, response);
    }

    private void postItem(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String itemType = request.getParameter("item_type");
            String itemName = request.getParameter("item_name");
            int categoryId = Integer.parseInt(request.getParameter("category_id"));
            String description = request.getParameter("description");
            int locationId = Integer.parseInt(request.getParameter("location_id"));
            String dateStr = request.getParameter("date");
            String contactInfo = request.getParameter("contact_info");

            Item item = new Item();
            item.setUserId(user.getId());
            item.setItemType(itemType);
            item.setItemName(itemName);
            item.setCategoryId(categoryId);
            item.setDescription(description);
            item.setLocationId(locationId);
            item.setLostFoundDate(Date.valueOf(dateStr));
            item.setContactInfo(contactInfo);
            item.setStatus("PENDING_APPROVAL");

            int newItemId = itemDAO.addItem(item);
            if (newItemId > 0) {
                item.setId(newItemId);
                
                // Trigger Matching Logic
                List<Item> matches = itemDAO.findPotentialMatches(item);
                for (Item matchItem : matches) {
                    Match match = new Match();
                    if (item.getItemType().equals("LOST")) {
                        match.setLostItemId(item.getId());
                        match.setFoundItemId(matchItem.getId());
                    } else {
                        match.setLostItemId(matchItem.getId());
                        match.setFoundItemId(item.getId());
                    }
                    match.setMatchScore(100); // Simple exact match on cat/loc
                    match.setStatus("POTENTIAL");
                    matchDAO.addMatch(match);
                }
                
                String message = "Item posted successfully! Waiting for approval.";
                if (!matches.isEmpty()) {
                    message += " We found " + matches.size() + " potential match(es)! Check your dashboard.";
                }
                
                response.sendRedirect("dashboard.jsp?success=" + java.net.URLEncoder.encode(message, "UTF-8"));
            } else {
                request.setAttribute("error", "Failed to post item.");
                request.getRequestDispatcher("post_item.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid input data.");
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

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int itemId = Integer.parseInt(request.getParameter("item_id"));
        // In a real app, verify ownership here
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
        int categoryId = 0;
        int locationId = 0;
        try {
            if (request.getParameter("category_id") != null && !request.getParameter("category_id").isEmpty()) {
                categoryId = Integer.parseInt(request.getParameter("category_id"));
            }
            if (request.getParameter("location_id") != null && !request.getParameter("location_id").isEmpty()) {
                locationId = Integer.parseInt(request.getParameter("location_id"));
            }
        } catch (NumberFormatException e) {
            // Ignore invalid IDs
        }
        
        int page = 1;
        int limit = 10;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int offset = (page - 1) * limit;

        List<Item> results = itemDAO.searchItems(query, categoryId, locationId, limit, offset);
        request.setAttribute("items", results);
        request.setAttribute("currentPage", page);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
    
    private void listItems(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = 1;
        int limit = 10;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int offset = (page - 1) * limit;
        
        List<Item> items = itemDAO.getAllApprovedItems(limit, offset);
        List<Category> categories = categoryDAO.getAllCategories();
        List<Location> locations = locationDAO.getAllLocations();
        
        request.setAttribute("items", items);
        request.setAttribute("categories", categories);
        request.setAttribute("locations", locations);
        request.setAttribute("currentPage", page);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
