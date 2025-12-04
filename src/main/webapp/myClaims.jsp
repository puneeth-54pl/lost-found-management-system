<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.dao.ClaimDAO" %>
<%@ page import="com.lostfound.model.Claim" %>
<%@ page import="java.util.List" %>

<%
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Claim> claims = (List<Claim>) request.getAttribute("claims");
%>

<div class="row mb-4">
    <div class="col-md-8">
        <h2>My Claims</h2>
        <p class="text-muted">Track the status of items you've claimed.</p>
    </div>
    <div class="col-md-4 text-end">
        <a href="items" class="btn btn-outline-secondary">Back to Items</a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Item Name</th>
                        <th>Type</th>
                        <th>Claim Date</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (claims != null && !claims.isEmpty()) {
                            for (Claim claim : claims) {
                    %>
                    <tr>
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
                    </tr>
                    <% 
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="4" class="text-center py-4 text-muted">You haven't claimed any items yet.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
