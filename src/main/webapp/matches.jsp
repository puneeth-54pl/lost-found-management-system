<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.model.Item" %>
<%@ page import="java.util.List" %>

<%
    List<Item> matches = (List<Item>) request.getAttribute("matches");
    Item postedItem = (Item) request.getAttribute("postedItem");
    
    if (matches == null || postedItem == null) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
%>

<div class="row mb-4">
    <div class="col-12 text-center">
        <div class="alert alert-success">
            <h4 class="alert-heading">Item Posted Successfully!</h4>
            <p>Your item <strong><%= postedItem.getItemName() %></strong> has been submitted for approval.</p>
        </div>
        <h3 class="mt-4"><i class="fas fa-bolt text-warning"></i> Potential Matches Found!</h3>
        <p class="lead">We found existing items that might match what you reported.</p>
    </div>
</div>

<div class="row">
    <% for (Item item : matches) { %>
    <div class="col-md-4 mb-4">
        <div class="card h-100 border-warning">
            <div class="card-header bg-warning text-dark">
                Potential Match
            </div>
            <% if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) { %>
            <img src="<%= item.getImageUrl() %>" class="card-img-top" alt="<%= item.getItemName() %>" style="height: 150px; object-fit: cover;">
            <% } %>
            <div class="card-body">
                <h5 class="card-title"><%= item.getItemName() %></h5>
                <span class="badge <%= "LOST".equals(item.getItemType()) ? "bg-danger" : "bg-success" %> mb-2">
                    <%= item.getItemType() %>
                </span>
                <p class="card-text"><%= item.getDescription() %></p>
                <ul class="list-unstyled text-muted small">
                    <li><i class="fas fa-map-marker-alt"></i> <%= item.getLocation() %></li>
                    <li><i class="far fa-calendar-alt"></i> <%= item.getLostFoundDate() %></li>
                    <li><i class="fas fa-tag"></i> <%= item.getCategory() %></li>
                </ul>
            </div>
            <div class="card-footer bg-white">
                <button type="button" class="btn btn-outline-primary w-100" data-bs-toggle="modal" data-bs-target="#matchModal<%= item.getId() %>">
                    View & Claim
                </button>
            </div>
        </div>
    </div>
    
    <!-- Modal -->
    <div class="modal fade" id="matchModal<%= item.getId() %>" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><%= item.getItemName() %></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p><strong>Posted By:</strong> <%= item.getUserName() %></p>
                    <%-- Contact Hidden --%>
                    <hr>
                    <p class="text-muted">If this is the item you are looking for/found, please proceed below.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <% if ("FOUND".equals(item.getItemType())) { %>
                    <form action="claims" method="post">
                        <input type="hidden" name="action" value="submit">
                        <input type="hidden" name="item_id" value="<%= item.getId() %>">
                        <button type="submit" class="btn btn-primary">Claim This Item</button>
                    </form>
                    <% } else { %>
                    <button type="button" class="btn btn-info disabled">Please Contact Admin to Report Found Match</button>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
    <% } %>
</div>

<div class="row mt-4">
    <div class="col-12 text-center">
        <a href="dashboard.jsp" class="btn btn-secondary btn-lg">Continue to Dashboard</a>
    </div>
</div>

<%@ include file="footer.jsp" %>
