<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.dao.ItemDAO" %>
<%@ page import="com.lostfound.model.Item" %>
<%@ page import="java.util.List" %>

<%
    if (currentUser == null || !"admin".equals(currentUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<div class="row mb-4">
    <div class="col-12">
        <h2>Admin Dashboard</h2>
        <p class="text-muted">Review pending items and manage content.</p>
    </div>
</div>

<div class="card mb-4">
    <div class="card-header bg-warning text-dark">
        <h5 class="mb-0"><i class="fas fa-clock me-2"></i>Pending Approvals</h5>
    </div>
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th>User</th>
                        <th>Type</th>
                        <th>Item Details</th>
                        <th>Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        ItemDAO itemDAO = new ItemDAO();
                        List<Item> pendingItems = itemDAO.getItemsByStatus("PENDING");
                        
                        for (Item item : pendingItems) {
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
                            <small class="text-muted"><i class="fas fa-map-marker-alt"></i> <%= item.getLocation() %></small>
                        </td>
                        <td><%= item.getLostFoundDate() %></td>
                        <td>
                            <form action="items" method="post" class="d-inline">
                                <input type="hidden" name="action" value="update_status">
                                <input type="hidden" name="item_id" value="<%= item.getId() %>">
                                <input type="hidden" name="status" value="APPROVED">
                                <button type="submit" class="btn btn-sm btn-success me-1" title="Approve"><i class="fas fa-check"></i></button>
                            </form>
                            <form action="items" method="post" class="d-inline">
                                <input type="hidden" name="action" value="update_status">
                                <input type="hidden" name="item_id" value="<%= item.getId() %>">
                                <input type="hidden" name="status" value="REJECTED">
                                <button type="submit" class="btn btn-sm btn-danger" title="Reject"><i class="fas fa-times"></i></button>
                            </form>
                        </td>
                    </tr>
                    <% 
                        }
                        if (pendingItems.isEmpty()) {
                    %>
                    <tr>
                        <td colspan="5" class="text-center py-4 text-muted">No pending items to review.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
