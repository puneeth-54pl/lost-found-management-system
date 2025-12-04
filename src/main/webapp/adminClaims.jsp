<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.model.Claim" %>
<%@ page import="java.util.List" %>

<%
    if (currentUser == null || !"admin".equals(currentUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Claim> claims = (List<Claim>) request.getAttribute("claims");
%>

<div class="row mb-4">
    <div class="col-12">
        <h2>Admin - Manage Claims</h2>
        <p class="text-muted">Review and approve/reject user claims for found items.</p>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>User</th>
                        <th>Item Name</th>
                        <th>Type</th>
                        <th>Claim Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (claims != null && !claims.isEmpty()) {
                            for (Claim claim : claims) {
                    %>
                    <tr>
                        <td><%= claim.getUserName() %></td>
                        <td><strong><%= claim.getItemName() %></strong></td>
                        <td>
                            <span class="badge <%= "LOST".equals(claim.getItemType()) ? "bg-danger" : "bg-success" %>">
                                <%= claim.getItemType() %>
                            </span>
                        </td>
                        <td><%= claim.getClaimDate() %></td>
                        <td>
                            <% 
                                String statusClass = "bg-warning text-dark";
                                if ("APPROVED".equals(claim.getStatus())) statusClass = "bg-success";
                                else if ("REJECTED".equals(claim.getStatus())) statusClass = "bg-danger";
                            %>
                            <span class="badge <%= statusClass %>"><%= claim.getStatus() %></span>
                        </td>
                        <td>
                            <% if ("PENDING".equals(claim.getStatus())) { %>
                                <form action="claims" method="post" class="d-inline">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="claim_id" value="<%= claim.getId() %>">
                                    <input type="hidden" name="status" value="APPROVED">
                                    <button type="submit" class="btn btn-sm btn-success me-1" title="Approve">
                                        <i class="fas fa-check"></i> Approve
                                    </button>
                                </form>
                                <form action="claims" method="post" class="d-inline">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="claim_id" value="<%= claim.getId() %>">
                                    <input type="hidden" name="status" value="REJECTED">
                                    <button type="submit" class="btn btn-sm btn-danger" title="Reject">
                                        <i class="fas fa-times"></i> Reject
                                    </button>
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
                        <td colspan="6" class="text-center py-4 text-muted">No claims found.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
