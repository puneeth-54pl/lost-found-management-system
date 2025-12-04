<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.model.Item" %>
<%@ page import="java.util.List" %>

<%
    // Reuse the logic from index.jsp or just display results if passed
    // But since ItemServlet forwards to index.jsp for search now (in my previous update), 
    // this file might be redundant or needs to be aligned.
    // However, the user might still be hitting this if they go to items?action=search directly without params?
    // Actually, my ItemServlet update forwards to index.jsp for search results.
    // But let's fix this file just in case it's used or linked.
    
    List<Item> searchResults = (List<Item>) request.getAttribute("items"); // Servlet sets "items", not "results" in my update
    if (searchResults == null) {
        searchResults = (List<Item>) request.getAttribute("results"); // Fallback if old code used
    }
%>

<div class="row mb-4">
    <div class="col-12">
        <h2>Search Results</h2>
        <a href="items" class="btn btn-outline-secondary mb-3">&larr; Back to All Items</a>
    </div>
</div>

<div class="row">
    <%
        if (searchResults != null && !searchResults.isEmpty()) {
            for (Item item : searchResults) {
    %>
    <div class="col-md-6 mb-4">
        <div class="card h-100 shadow-sm">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                    <h5 class="card-title"><%= item.getItemName() %></h5>
                    <span class="badge <%= "LOST".equals(item.getItemType()) ? "bg-danger" : "bg-success" %>">
                        <%= item.getItemType() %>
                    </span>
                </div>
                <h6 class="card-subtitle mb-2 text-muted">
                    <%= item.getCategoryName() != null ? item.getCategoryName() : "Uncategorized" %> | 
                    <i class="fas fa-map-marker-alt"></i> <%= item.getLocationName() != null ? item.getLocationName() : "Unknown" %>
                </h6>
                <p class="card-text"><%= item.getDescription() %></p>
                <p class="mb-1"><strong>Contact:</strong> <%= item.getContactInfo() %></p>
                <small class="text-muted">Posted by <%= item.getUserName() %> on <%= item.getLostFoundDate() %></small>
            </div>
        </div>
    </div>
    <%
            }
        } else {
    %>
        <div class="col-12 text-center py-5">
            <h4 class="text-muted">No items found matching your criteria.</h4>
        </div>
    <%
        }
    %>
</div>

<%@ include file="footer.jsp" %>
