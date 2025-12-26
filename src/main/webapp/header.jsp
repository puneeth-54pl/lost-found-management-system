<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.lostfound.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lost & Found System</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="css/style.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top">
    <div class="container">
        <a class="navbar-brand" href="index.jsp"><i class="fas fa-search-location me-2"></i>Lost & Found</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="index.jsp">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="items?action=search">Search Items</a>
                </li>
                <% if (currentUser != null) { %>
                    <li class="nav-item">
                        <a class="nav-link" href="post_item.jsp">Post Item</a>
                    </li>
                <% } %>
            </ul>
            <ul class="navbar-nav">
                <% if (currentUser == null) { %>
                    <li class="nav-item">
                        <a class="nav-link" href="login.jsp">Login</a>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-primary ms-2" href="register.jsp">Register</a>
                    </li>
                <% } else { %>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                            Welcome, <%= currentUser.getUsername() %>
                        </a>
                        <ul class="dropdown-menu">
                            <% if ("admin".equals(currentUser.getRole())) { %>
                                <li><a class="dropdown-item" href="admin">Admin Dashboard</a></li>
                                <li><a class="dropdown-item" href="claims?action=admin">Manage Claims</a></li>
                            <% } else { %>
                                <li><a class="dropdown-item" href="dashboard.jsp">My Dashboard</a></li>
                                <li><a class="dropdown-item" href="claims?action=my">My Claims</a></li>
                            <% } %>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="auth?action=logout">Logout</a></li>
                        </ul>
                    </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4" style="min-height: 80vh;">
    <% if (request.getParameter("success") != null) { %>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <%= request.getParameter("success") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <%= request.getAttribute("error") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>
