<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.dao.ItemDAO" %>
<%@ page import="com.lostfound.model.Item" %>
<%@ page import="java.util.List" %>

<div class="hero-section text-center rounded-3">
    <h1 class="display-4 fw-bold">Lost Something? Found Something?</h1>
    <p class="lead">Connect with the community to recover lost items or return found ones.</p>
    <div class="mt-4">
        <a href="post_item.jsp" class="btn btn-light btn-lg px-4 me-2">Post an Item</a>
        <a href="items?action=search" class="btn btn-outline-light btn-lg px-4">Search Items</a>
    </div>
</div>

<div class="row mb-4">
    <div class="col-12 text-center">
        <h2 class="mb-4">Recent Items</h2>
    </div>
</div>

<div class="row">
    <%
        ItemDAO itemDAO = new ItemDAO();
        List<Item> recentItems = itemDAO.getAllApprovedItems();
        int count = 0;
        for (Item item : recentItems) {
            if (count >= 6) break; // Show only top 6
            count++;
    %>
    <div class="col-md-4 mb-4">
        <div class="card h-100">
            <div class="card-body">
                <span class="badge <%= "LOST".equals(item.getItemType()) ? "bg-danger" : "bg-success" %> status-badge">
                    <%= item.getItemType() %>
                </span>
                <h5 class="card-title mt-2"><%= item.getItemName() %></h5>
                <h6 class="card-subtitle mb-2 text-muted"><i class="fas fa-map-marker-alt me-1"></i><%= item.getLocation() %></h6>
                <p class="card-text text-truncate"><%= item.getDescription() %></p>
                <p class="card-text"><small class="text-muted">Date: <%= item.getLostFoundDate() %></small></p>
            </div>
            <div class="card-footer bg-white border-top-0">
                <a href="items?action=search&query=<%= item.getItemName() %>" class="btn btn-outline-primary w-100">View Details</a>
            </div>
        </div>
    </div>
    <%
        }
        if (recentItems.isEmpty()) {
    %>
        <div class="col-12 text-center">
            <p class="text-muted">No items posted yet.</p>
        </div>
    <% } %>
</div>

<%@ include file="footer.jsp" %>
