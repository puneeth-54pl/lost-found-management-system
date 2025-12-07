<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.dao.ItemDAO" %>
<%@ page import="com.lostfound.model.Item" %>
<%@ page import="java.util.List" %>

<%
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<div class="row mb-4">
    <div class="col-md-8">
        <h2>My Dashboard</h2>
        <p class="text-muted">Manage your posted items.</p>
    </div>
    <div class="col-md-4 text-end">
        <a href="post_item.jsp" class="btn btn-primary"><i class="fas fa-plus me-2"></i>Post New Item</a>
    </div>
</div>

<div class="card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Type</th>
                        <th>Item Name</th>
                        <th>Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        ItemDAO itemDAO = new ItemDAO();
                        List<Item> myItems = itemDAO.getUserItems(currentUser.getId());
                        
                        for (Item item : myItems) {
                    %>
                    <tr>
                        <td>
                            <span class="badge <%= "LOST".equals(item.getItemType()) ? "bg-danger" : "bg-success" %>">
                                <%= item.getItemType() %>
                            </span>
                        </td>
                        <td>
                            <strong><%= item.getItemName() %></strong><br>
                            <small class="text-muted"><%= item.getCategory() %></small>
                        </td>
                        <td><%= item.getLostFoundDate() %></td>
                        <td>
                            <% 
                                String statusClass = "bg-secondary";
                                if ("APPROVED".equals(item.getStatus())) statusClass = "bg-success";
                                else if ("REJECTED".equals(item.getStatus())) statusClass = "bg-danger";
                                else if ("RETURNED".equals(item.getStatus())) statusClass = "bg-info";
                            %>
                            <span class="badge <%= statusClass %>"><%= item.getStatus() %></span>
                        </td>
                        <td>
                            <form action="items" method="post" class="d-inline" onsubmit="return confirm('Are you sure you want to delete this item?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="item_id" value="<%= item.getId() %>">
                                <button type="submit" class="btn btn-sm btn-outline-danger"><i class="fas fa-trash"></i></button>
                            </form>
                        </td>
                    </tr>
                    <% 
                        } 
                        if (myItems.isEmpty()) {
                    %>
                    <tr>
                        <td colspan="5" class="text-center py-4 text-muted">You haven't posted any items yet.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
