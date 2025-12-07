package com.lostfound.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

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
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
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
        } else if ("mark_found".equals(action)) {
            markAsFound(request, response);
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

            // Input validation
            if (itemName == null || itemName.trim().isEmpty() || itemName.length() > 100) {
                request.setAttribute("error", "Item name is required and must be less than 100 characters.");
                request.getRequestDispatcher("post_item.jsp").forward(request, response);
                return;
            }

            if (description == null || description.trim().isEmpty() || description.length() > 500) {
                request.setAttribute("error", "Description is required and must be less than 500 characters.");
                request.getRequestDispatcher("post_item.jsp").forward(request, response);
                return;
            }

            if (contactInfo == null || contactInfo.trim().isEmpty() || contactInfo.length() > 255) {
                request.setAttribute("error", "Contact information is required and must be less than 255 characters.");
                request.getRequestDispatcher("post_item.jsp").forward(request, response);
                return;
            }

            // Basic XSS protection
            itemName = itemName.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            description = description.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            contactInfo = contactInfo.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

            // Handle image upload
            String imageUrl = null;
            Part filePart = request.getPart("item_image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = filePart.getSubmittedFileName();
                if (fileName != null && !fileName.isEmpty()) {
                    // Check file size (10MB limit)
                    long fileSize = filePart.getSize();
                    if (fileSize > 10 * 1024 * 1024) {
                        request.setAttribute("error", "Image file is too large. Maximum size is 10MB.");
                        request.getRequestDispatcher("post_item.jsp").forward(request, response);
                        return;
                    }

                    // Validate file type
                    String contentType = filePart.getContentType();
                    if (contentType != null && (contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") || contentType.equals("image/gif") ||
                        contentType.equals("image/webp"))) {

                        // Generate unique filename
                        String extension = getFileExtension(fileName);
                        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;

                        // Get upload path
                        String uploadPath = getServletContext().getRealPath("/uploads/items");
                        Path uploadDir = Paths.get(uploadPath);
                        if (!Files.exists(uploadDir)) {
                            Files.createDirectories(uploadDir);
                        }

                        // Save file
                        Path filePath = uploadDir.resolve(uniqueFileName);
                        Files.copy(filePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        // Set relative URL for database
                        imageUrl = "uploads/items/" + uniqueFileName;
                    } else {
                        request.setAttribute("error", "Invalid image format. Only JPEG, PNG, GIF, and WebP are allowed.");
                        request.getRequestDispatcher("post_item.jsp").forward(request, response);
                        return;
                    }
                }
            }

            Item item = new Item();
            item.setUserId(user.getId());
            item.setItemType(itemType);
            item.setItemName(itemName);
            item.setCategoryId(categoryId);
            item.setDescription(description);
            item.setLocationId(locationId);
            item.setLostFoundDate(Date.valueOf(dateStr));
            item.setContactInfo(contactInfo);
            item.setImageUrl(imageUrl);
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

        try {
            int itemId = Integer.parseInt(request.getParameter("item_id"));

            // SECURITY: Verify ownership - users can only delete their own items, admins can delete any
            if (!"admin".equals(user.getRole())) {
                Item item = itemDAO.getItemById(itemId);
                if (item == null || item.getUserId() != user.getId()) {
                    response.sendRedirect("dashboard.jsp?error=" + java.net.URLEncoder.encode("You can only delete your own items", "UTF-8"));
                    return;
                }
            }

            if (itemDAO.deleteItem(itemId)) {
                String message = "Item deleted successfully";
                if ("admin".equals(user.getRole())) {
                    response.sendRedirect("admin_dashboard.jsp?success=" + java.net.URLEncoder.encode(message, "UTF-8"));
                } else {
                    response.sendRedirect("dashboard.jsp?success=" + java.net.URLEncoder.encode(message, "UTF-8"));
                }
            } else {
                String message = "Failed to delete item";
                if ("admin".equals(user.getRole())) {
                    response.sendRedirect("admin_dashboard.jsp?error=" + java.net.URLEncoder.encode(message, "UTF-8"));
                } else {
                    response.sendRedirect("dashboard.jsp?error=" + java.net.URLEncoder.encode(message, "UTF-8"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?error=" + java.net.URLEncoder.encode("Invalid request", "UTF-8"));
        }
    }

    private void markAsFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int itemId = Integer.parseInt(request.getParameter("item_id"));

            // SECURITY: Verify ownership - only item owner can mark as found
            Item item = itemDAO.getItemById(itemId);
            if (item == null) {
                response.sendRedirect("dashboard.jsp?error=" + java.net.URLEncoder.encode("Item not found", "UTF-8"));
                return;
            }

            if (item.getUserId() != user.getId()) {
                response.sendRedirect("dashboard.jsp?error=" + java.net.URLEncoder.encode("You can only mark your own items as found", "UTF-8"));
                return;
            }

            // Only LOST items can be marked as found
            if (!"LOST".equals(item.getItemType())) {
                response.sendRedirect("dashboard.jsp?error=" + java.net.URLEncoder.encode("Only lost items can be marked as found", "UTF-8"));
                return;
            }

            // Only LISTED items can be marked as found (not already resolved/rejected)
            if (!"LISTED".equals(item.getStatus()) && !"PENDING_APPROVAL".equals(item.getStatus())) {
                response.sendRedirect("dashboard.jsp?error=" + java.net.URLEncoder.encode("This item cannot be marked as found", "UTF-8"));
                return;
            }

            if (itemDAO.updateItemStatus(itemId, "RESOLVED")) {
                String message = "Item marked as found successfully!";
                response.sendRedirect("dashboard.jsp?success=" + java.net.URLEncoder.encode(message, "UTF-8"));
            } else {
                response.sendRedirect("dashboard.jsp?error=" + java.net.URLEncoder.encode("Failed to mark item as found", "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?error=" + java.net.URLEncoder.encode("Invalid request", "UTF-8"));
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
                if (page < 1) page = 1; // Prevent negative pages
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
                if (page < 1) page = 1; // Prevent negative pages
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

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
