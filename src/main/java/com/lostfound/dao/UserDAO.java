package com.lostfound.dao;

import com.lostfound.model.User;
import com.lostfound.util.DBConnection;
import com.lostfound.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

	public boolean registerUser(User user) {
		String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, PasswordUtil.hashPassword(user.getPassword())); // Hash the password
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getRole());

			int rows = pstmt.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public User checkLogin(String username, String password) {
		String sql = "SELECT * FROM users WHERE username = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, username);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String storedHash = rs.getString("password");
					// Verify the password against the stored hash
					if (PasswordUtil.verifyPassword(password, storedHash)) {
						User user = new User();
						user.setId(rs.getInt("id"));
						user.setUsername(rs.getString("username"));
						user.setPassword(storedHash); // Keep the hash in the object
						user.setEmail(rs.getString("email"));
						user.setRole(rs.getString("role"));
						user.setCreatedAt(rs.getTimestamp("created_at"));
						return user;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isUsernameTaken(String username) {
		String sql = "SELECT id FROM users WHERE username = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
