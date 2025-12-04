<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.model.Item" %>
<%@ page import="com.lostfound.model.Category" %>
<%@ page import="com.lostfound.model.Location" %>
<%@ page import="java.util.List" %>

<%
    List<Item> items = (List<Item>) request.getAttribute("items");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<Location> locations = (List<Location>) request.getAttribute("locations");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    if (currentPage == null) currentPage = 1;
%>

<div class="hero-section text-center rounded-3 mb-5">
    <h1 class="display-4 fw-bold">Lost Something? Found Something?</h1>
    <p class="lead">Connect with the community to recover lost items or return found ones.</p>
    <div class="mt-4">
        <a href="items?action=form" class="btn btn-light btn-lg px-4 me-2">Post an Item</a>
    </div>
</div>

<!-- Search and Filter Section -->
<div class="card mb-4 shadow-sm">
    <div class="card-body">
        <form action="items" method="get" class="row g-3">
            <input type="hidden" name="action" value="search">
            <div class="col-md-4">
                <input type="text" class="form-control" name="query" placeholder="Search for items..." value="<%= request.getParameter("query") != null ? request.getParameter("query") : "" %>">
            </div>
            <div class="col-md-3">
                <select class="form-select" name="category_id">
                    <option value="">All Categories</option>
                    <% if (categories != null) {
                        for (Category cat : categories) { %>
                            <option value="<%= cat.getId() %>" <%= (request.getParameter("category_id") != null && request.getParameter("category_id").equals(String.valueOf(cat.getId()))) ? "selected" : "" %>><%= cat.getName() %></option>
                    <%  } } %>
                </select>
            </div>
            <div class="col-md-3">
                <select class="form-select" name="location_id">
                    <option value="">All Locations</option>
                    <% if (locations != null) {
                        for (Location loc : locations) { %>
                            <option value="<%= loc.getId() %>" <%= (request.getParameter("location_id") != null && request.getParameter("location_id").equals(String.valueOf(loc.getId()))) ? "selected" : "" %>><%= loc.getName() %></option>
                    <%  } } %>
                </select>
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-primary w-100">Search</button>
            </div>
        </form>
    </div>
</div>

<div class="row mb-4">
    <div class="col-12">
        <h2 class="mb-4">Listed Items</h2>
    </div>
</div>

<div class="row">
    <%
        if (items != null && !items.isEmpty()) {
            for (Item item : items) {
    %>
    <div class="col-md-4 mb-4">
        <div class="card h-100 shadow-sm">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <span class="badge <%= "LOST".equals(item.getItemType()) ? "bg-danger" : "bg-success" %>">
                        <%= item.getItemType() %>
                    </span>
                    <small class="text-muted"><%= item.getLostFoundDate() %></small>
                </div>
                <h5 class="card-title"><%= item.getItemName() %></h5>
                <h6 class="card-subtitle mb-2 text-muted">
                    <i class="fas fa-map-marker-alt me-1"></i><%= item.getLocationName() != null ? item.getLocationName() : "Unknown" %>
                    <span class="mx-1">|</span>
                    <i class="fas fa-tag me-1"></i><%= item.getCategoryName() != null ? item.getCategoryName() : "Uncategorized" %>
                </h6>
                <p class="card-text text-truncate"><%= item.getDescription() %></p>
                <p class="card-text"><small class="text-muted">Posted by: <%= item.getUserName() %></small></p>
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
                    <h5 class="modal-title"><%= item.getItemName() %> (<%= item.getItemType() %>)</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p><strong>Description:</strong> <%= item.getDescription() %></p>
                    <p><strong>Category:</strong> <%= item.getCategoryName() %></p>
                    <p><strong>Location:</strong> <%= item.getLocationName() %></p>
                    <p><strong>Date:</strong> <%= item.getLostFoundDate() %></p>
                    <p><strong>Contact Info:</strong> <%= item.getContactInfo() %></p>
                    <p><strong>Status:</strong> <%= item.getStatus() %></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <%
            }
        } else {
    %>
        <div class="col-12 text-center py-5">
            <p class="text-muted lead">No items found matching your criteria.</p>
        </div>
    <% } %>
</div>

<!-- Pagination -->
<nav aria-label="Page navigation" class="mt-4">
    <ul class="pagination justify-content-center">
        <li class="page-item <%= currentPage == 1 ? "disabled" : "" %>">
            <a class="page-link" href="items?page=<%= currentPage - 1 %><%= request.getQueryString() != null ? "&" + request.getQueryString().replaceAll("&?page=\\d+", "") : "" %>">Previous</a>
        </li>
        <li class="page-item active">
            <a class="page-link" href="#"><%= currentPage %></a>
        </li>
        <li class="page-item">
            <a class="page-link" href="items?page=<%= currentPage + 1 %><%= request.getQueryString() != null ? "&" + request.getQueryString().replaceAll("&?page=\\d+", "") : "" %>">Next</a>
        </li>
    </ul>
</nav>

<%@ include file="footer.jsp" %>
