package com.lostfound.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lostfound.dao.ClaimDAO;
import com.lostfound.dao.ItemDAO;
import com.lostfound.model.Claim;
import com.lostfound.model.Item;
import com.lostfound.model.User;

@WebServlet("/claims")
public class ClaimServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ClaimDAO claimDAO;
    private ItemDAO itemDAO;

    public void init() {
        claimDAO = new ClaimDAO();
        itemDAO = new ItemDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("my".equals(action)) {
            showMyClaims(request, response, user);
        } else if ("admin".equals(action) && "admin".equals(user.getRole())) {
            showAdminClaims(request, response);
        } else {
            response.sendRedirect("items");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("submit".equals(action)) {
            submitClaim(request, response, user);
        } else if ("update".equals(action) && "admin".equals(user.getRole())) {
            updateClaimStatus(request, response);
        }
    }

    private void submitClaim(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        try {
            int itemId = Integer.parseInt(request.getParameter("item_id"));
            
            // SECURITY: Get item details to validate
            Item item = itemDAO.getItemById(itemId);
            
            if (item == null) {
                response.sendRedirect("items?error=" + java.net.URLEncoder.encode("Item not found", "UTF-8"));
                return;
            }
            
            // SECURITY CHECK 1: User cannot claim their own item
            if (item.getUserId() == user.getId()) {
                response.sendRedirect("items?error=" + java.net.URLEncoder.encode("You cannot claim your own item", "UTF-8"));
                return;
            }
            
            // SECURITY CHECK 2: Only FOUND items can be claimed
            if (!"FOUND".equals(item.getItemType())) {
                response.sendRedirect("items?error=" + java.net.URLEncoder.encode("Only FOUND items can be claimed", "UTF-8"));
                return;
            }
            
            // SECURITY CHECK 3: Only LISTED items can be claimed (not PENDING, REJECTED, or RESOLVED)
            if (!"LISTED".equals(item.getStatus()) && !"APPROVED".equals(item.getStatus())) {
                response.sendRedirect("items?error=" + java.net.URLEncoder.encode("This item is not available for claiming", "UTF-8"));
                return;
            }
            
            // Check if user already claimed this item
            if (claimDAO.hasUserClaimedItem(user.getId(), itemId)) {
                response.sendRedirect("items?error=" + java.net.URLEncoder.encode("You have already claimed this item", "UTF-8"));
                return;
            }
            
            if (claimDAO.createClaim(itemId, user.getId())) {
                response.sendRedirect("claims?action=my&success=" + java.net.URLEncoder.encode("Claim submitted successfully", "UTF-8"));
            } else {
                response.sendRedirect("items?error=" + java.net.URLEncoder.encode("Failed to submit claim", "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("items?error=" + java.net.URLEncoder.encode("Invalid request", "UTF-8"));
        }
    }

    private void showMyClaims(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        List<Claim> claims = claimDAO.getUserClaims(user.getId());
        request.setAttribute("claims", claims);
        request.getRequestDispatcher("myClaims.jsp").forward(request, response);
    }

    private void showAdminClaims(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Claim> claims = claimDAO.getAllClaims();
        request.setAttribute("claims", claims);
        request.getRequestDispatcher("adminClaims.jsp").forward(request, response);
    }

    private void updateClaimStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int claimId = Integer.parseInt(request.getParameter("claim_id"));
            String status = request.getParameter("status");
            
            if (claimDAO.updateClaimStatus(claimId, status)) {
                // If approved, update item status to RESOLVED
                if ("APPROVED".equals(status)) {
                    int itemId = claimDAO.getItemIdByClaim(claimId);
                    if (itemId > 0) {
                        itemDAO.updateItemStatus(itemId, "RESOLVED");
                    }
                }
                response.sendRedirect("claims?action=admin&success=" + java.net.URLEncoder.encode("Claim updated successfully", "UTF-8"));
            } else {
                response.sendRedirect("claims?action=admin&error=" + java.net.URLEncoder.encode("Failed to update claim", "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("claims?action=admin&error=" + java.net.URLEncoder.encode("Invalid request", "UTF-8"));
        }
    }
}
