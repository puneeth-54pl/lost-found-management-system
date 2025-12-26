<%@ include file="header.jsp" %>

<%
    if (currentUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h4 class="mb-0">Post Lost or Found Item</h4>
            </div>
            <div class="card-body p-4">
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger">
                        <%= request.getAttribute("error") %>
                    </div>
                <% } %>
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
                            <select class="form-select" name="category" required>
                                <option value="Electronics">Electronics</option>
                                <option value="Documents">Documents</option>
                                <option value="Accessories">Accessories</option>
                                <option value="Clothing">Clothing</option>
                                <option value="Keys">Keys</option>
                                <option value="Others">Others</option>
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
                        <label class="form-label">Upload Image (Optional)</label>
                        <input type="file" class="form-control" name="imageFile" accept="image/*">
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Location</label>
                            <input type="text" class="form-control" name="location" placeholder="Where was it lost/found?" required>
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
