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
        int currentPage = 1;
        int pageSize = 9;
        if (request.getParameter("page") != null) {
            try {
                currentPage = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        
        ItemDAO itemDAO = new ItemDAO();
        List<Item> recentItems = itemDAO.getAllApprovedItems(currentPage, pageSize);
        int totalItems = itemDAO.getTotalApprovedItemsCount();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        for (Item item : recentItems) {
            // No count break needed as DAO handles limit
    %>
    <div class="col-md-4 mb-4">
        <div class="card h-100">
            <% if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) { %>
            <img src="<%= item.getImageUrl() %>" class="card-img-top" alt="<%= item.getItemName() %>" style="height: 200px; object-fit: cover;">
            <% } else { %>
            <div class="card-img-top bg-light d-flex align-items-center justify-content-center" style="height: 200px;">
                <i class="fas fa-image fa-3x text-muted"></i>
            </div>
            <% } %>
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
                <button type="button" class="btn btn-outline-primary w-100" data-bs-toggle="modal" data-bs-target="#itemModal<%= item.getId() %>">
                    View Details
                </button>
                <% if (currentUser != null && "FOUND".equals(item.getItemType()) && item.getUserId() != currentUser.getId()) { %>
                <form action="claims" method="post" class="mt-2">
                    <input type="hidden" name="action" value="submit">
                    <input type="hidden" name="item_id" value="<%= item.getId() %>">
                    <button type="submit" class="btn btn-success w-100">Claim This Item</button>
                </form>
                <% } %>
            </div>
        </div>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="itemModal<%= item.getId() %>" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><%= item.getItemName() %></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p><strong>Type:</strong> <%= item.getItemType() %></p>
                    <p><strong>Category:</strong> <%= item.getCategory() %></p>
                    <p><strong>Location:</strong> <%= item.getLocation() %></p>
                    <p><strong>Date:</strong> <%= item.getLostFoundDate() %></p>
                    <p><strong>Description:</strong> <%= item.getDescription() %></p>
                    <p><strong>Posted By:</strong> <%= item.getUserName() %></p>
                    <%-- Contact Info Hidden for Mediator Privacy --%>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
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



<% if (totalPages > 1) { %>
<div class="row mt-4">
    <div class="col-12 d-flex justify-content-center">
        <nav aria-label="Page navigation">
            <ul class="pagination">
                <li class="page-item <%= (currentPage == 1) ? "disabled" : "" %>">
                    <a class="page-link" href="?page=<%= currentPage - 1 %>" tabindex="-1">Previous</a>
                </li>
                <% for (int i = 1; i <= totalPages; i++) { %>
                <li class="page-item <%= (currentPage == i) ? "active" : "" %>">
                    <a class="page-link" href="?page=<%= i %>"><%= i %></a>
                </li>
                <% } %>
                <li class="page-item <%= (currentPage == totalPages) ? "disabled" : "" %>">
                    <a class="page-link" href="?page=<%= currentPage + 1 %>">Next</a>
                </li>
            </ul>
        </nav>
    </div>
</div>
<% } %>

<%@ include file="footer.jsp" %>
