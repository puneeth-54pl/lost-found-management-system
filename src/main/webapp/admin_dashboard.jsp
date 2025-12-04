<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.model.Item" %>
<%@ page import="java.util.List" %>

<%
    if (currentUser == null || !"admin".equals(currentUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Item> items = (List<Item>) request.getAttribute("items");
    String currentStatus = (String) request.getAttribute("currentStatus");
    if (currentStatus == null) currentStatus = "PENDING_APPROVAL";
%>

<div class="row mb-4">
    <div class="col-12">
        <h2>Admin Dashboard</h2>
        <p class="text-muted">Review pending items and manage content.</p>
    </div>
</div>

<!-- Status Tabs -->
<ul class="nav nav-tabs mb-4">
    <li class="nav-item">
        <a class="nav-link <%= "PENDING_APPROVAL".equals(currentStatus) ? "active" : "" %>" href="admin?status=PENDING_APPROVAL">Pending Approval</a>
    </li>
    <li class="nav-item">
        <a class="nav-link <%= "LISTED".equals(currentStatus) ? "active" : "" %>" href="admin?status=LISTED">Listed Items</a>
    </li>
    <li class="nav-item">
        <a class="nav-link <%= "REJECTED".equals(currentStatus) ? "active" : "" %>" href="admin?status=REJECTED">Rejected Items</a>
    </li>
</ul>

<div class="card mb-4 shadow-sm">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>User</th>
                        <th>Type</th>
                        <th>Item Details</th>
                        <th>Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (items != null && !items.isEmpty()) {
                            for (Item item : items) {
                    %>
                    <tr>
                        <td><%= item.getUserName() %></td>
                        <td>
                            <span class="badge <%= "LOST".equals(item.getItemType()) ? "bg-danger" : "bg-success" %>">
                                <%= item.getItemType() %>
                            </span>
                        </td>
                        <td>
                            <strong><%= item.getItemName() %></strong><br>
                            <small><%= item.getDescription() %></small><br>
                            <small class="text-muted">
                                <i class="fas fa-map-marker-alt"></i> <%= item.getLocationName() != null ? item.getLocationName() : "Unknown" %> | 
                                <i class="fas fa-tag"></i> <%= item.getCategoryName() != null ? item.getCategoryName() : "Uncategorized" %>
                            </small>
                        </td>
                        <td><%= item.getLostFoundDate() %></td>
                        <td>
                            <span class="badge bg-secondary"><%= item.getStatus() %></span>
                        </td>
                        <td>
                            <% if ("PENDING_APPROVAL".equals(item.getStatus())) { %>
                                <form action="admin" method="post" class="d-inline">
                                    <input type="hidden" name="action" value="update_status">
                                    <input type="hidden" name="item_id" value="<%= item.getId() %>">
                                    <input type="hidden" name="status" value="LISTED">
                                    <button type="submit" class="btn btn-sm btn-success me-1" title="Approve"><i class="fas fa-check"></i> Approve</button>
                                </form>
                                <form action="admin" method="post" class="d-inline">
                                    <input type="hidden" name="action" value="update_status">
                                    <input type="hidden" name="item_id" value="<%= item.getId() %>">
                                    <input type="hidden" name="status" value="REJECTED">
                                    <button type="submit" class="btn btn-sm btn-danger" title="Reject"><i class="fas fa-times"></i> Reject</button>
                                </form>
                            <% } else if ("LISTED".equals(item.getStatus())) { %>
                                <form action="admin" method="post" class="d-inline">
                                    <input type="hidden" name="action" value="update_status">
                                    <input type="hidden" name="item_id" value="<%= item.getId() %>">
                                    <input type="hidden" name="status" value="REJECTED">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Reject/Remove"><i class="fas fa-ban"></i> Remove</button>
                                </form>
                            <% } else { %>
                                <span class="text-muted">-</span>
                            <% } %>
                        </td>
                    </tr>
                    <% 
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="6" class="text-center py-4 text-muted">No items found with status: <%= currentStatus %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
