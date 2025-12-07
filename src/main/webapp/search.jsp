<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.model.Item" %>
<%@ page import="java.util.List" %>

<div class="row mb-4">
    <div class="col-12">
        <div class="card bg-light">
            <div class="card-body">
                <form action="items" method="get" class="row g-3">
                    <input type="hidden" name="action" value="search">
                    <div class="col-md-4">
                        <input type="text" class="form-control" name="query" placeholder="Search keywords..." value="<%= request.getParameter("query") != null ? request.getParameter("query") : "" %>">
                    </div>
                    <div class="col-md-3">
                        <select class="form-select" name="category">
                            <option value="">All Categories</option>
                            <option value="Electronics">Electronics</option>
                            <option value="Documents">Documents</option>
                            <option value="Accessories">Accessories</option>
                            <option value="Clothing">Clothing</option>
                            <option value="Keys">Keys</option>
                            <option value="Others">Others</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <input type="text" class="form-control" name="location" placeholder="Location" value="<%= request.getParameter("location") != null ? request.getParameter("location") : "" %>">
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary w-100"><i class="fas fa-search"></i> Search</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <%
        List<Item> searchResults = (List<Item>) request.getAttribute("results");
        if (searchResults != null) {
            for (Item item : searchResults) {
    %>
    <div class="col-md-6 mb-4">
        <div class="card h-100">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                    <h5 class="card-title"><%= item.getItemName() %></h5>
                    <span class="badge <%= "LOST".equals(item.getItemType()) ? "bg-danger" : "bg-success" %>">
                        <%= item.getItemType() %>
                    </span>
                </div>
                <h6 class="card-subtitle mb-2 text-muted"><%= item.getCategory() %> | <i class="fas fa-map-marker-alt"></i> <%= item.getLocation() %></h6>
                <p class="card-text"><%= item.getDescription() %></p>
                <p class="mb-1"><strong>Contact:</strong> <%= item.getContactInfo() %></p>
                <small class="text-muted">Posted by <%= item.getUserName() %> on <%= item.getLostFoundDate() %></small>
            </div>
        </div>
    </div>
    <%
            }
            if (searchResults.isEmpty()) {
    %>
        <div class="col-12 text-center py-5">
            <h4 class="text-muted">No items found matching your criteria.</h4>
        </div>
    <%
            }
        }
    %>
</div>

<%@ include file="footer.jsp" %>
