<%@ include file="header.jsp" %>
<%@ page import="com.lostfound.model.Category" %>
<%@ page import="com.lostfound.model.Location" %>
<%@ page import="java.util.List" %>

<%
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<Location> locations = (List<Location>) request.getAttribute("locations");
%>

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card shadow">
            <div class="card-header bg-primary text-white">
                <h4 class="mb-0">Post Lost or Found Item</h4>
            </div>
            <div class="card-body p-4">
                <form action="items" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="post">
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label">I have...</label>
                            <select class="form-select" name="item_type" required>
                                <option value="LOST">Lost an item</option>
                                <option value="FOUND">Found an item</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Category</label>
                            <select class="form-select" name="category_id" required>
                                <option value="">Select Category</option>
                                <% if (categories != null) {
                                    for (Category cat : categories) { %>
                                        <option value="<%= cat.getId() %>"><%= cat.getName() %></option>
                                <%  } } %>
                            </select>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Item Name</label>
                        <input type="text" class="form-control" name="item_name" placeholder="e.g., iPhone 13, Blue Wallet" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control" name="description" rows="3" placeholder="Provide details like color, brand, distinct marks..." required></textarea>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Item Image (Optional)</label>
                        <input type="file" class="form-control" name="item_image" accept="image/*">
                        <div class="form-text">Upload an image of the item (JPEG, PNG, GIF, WebP - Max 10MB)</div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Location</label>
                            <select class="form-select" name="location_id" required>
                                <option value="">Select Location</option>
                                <% if (locations != null) {
                                    for (Location loc : locations) { %>
                                        <option value="<%= loc.getId() %>"><%= loc.getName() %></option>
                                <%  } } %>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Date</label>
                            <input type="date" class="form-control" name="date" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Contact Information</label>
                        <input type="text" class="form-control" name="contact_info" placeholder="Email or Phone number for others to contact you" required>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary btn-lg">Submit Post</button>
                        <a href="dashboard.jsp" class="btn btn-outline-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
