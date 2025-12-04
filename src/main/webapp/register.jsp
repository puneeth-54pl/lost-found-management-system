<%@ include file="header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card mt-5">
            <div class="card-header bg-success text-white text-center">
                <h4>Register</h4>
            </div>
            <div class="card-body p-4">
                <form action="auth" method="post">
                    <input type="hidden" name="action" value="register">
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" name="username" required>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email Address</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    <div class="mb-3">
                        <label for="confirm_password" class="form-label">Confirm Password</label>
                        <input type="password" class="form-control" id="confirm_password" name="confirm_password" required>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-success">Register</button>
                    </div>
                </form>
            </div>
            <div class="card-footer text-center">
                <small>Already have an account? <a href="login.jsp">Login here</a></small>
            </div>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
