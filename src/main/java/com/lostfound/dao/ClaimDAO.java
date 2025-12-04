package com.lostfound.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lostfound.model.Claim;
import com.lostfound.util.DBConnection;

public class ClaimDAO {

    public boolean createClaim(int itemId, int userId) {
        String sql = "INSERT INTO claims (item_id, user_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, userId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Claim> getUserClaims(int userId) {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT c.*, i.item_name, i.item_type FROM claims c " +
                     "JOIN items i ON c.item_id = i.id " +
                     "WHERE c.user_id = ? ORDER BY c.claim_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Claim claim = new Claim();
                    claim.setId(rs.getInt("id"));
                    claim.setItemId(rs.getInt("item_id"));
                    claim.setUserId(rs.getInt("user_id"));
                    claim.setStatus(rs.getString("status"));
                    claim.setClaimDate(rs.getTimestamp("claim_date"));
                    claim.setItemName(rs.getString("item_name"));
                    claim.setItemType(rs.getString("item_type"));
                    claims.add(claim);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return claims;
    }

    public List<Claim> getAllClaims() {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT c.*, i.item_name, i.item_type, u.username FROM claims c " +
                     "JOIN items i ON c.item_id = i.id " +
                     "JOIN users u ON c.user_id = u.id " +
                     "ORDER BY c.claim_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Claim claim = new Claim();
                claim.setId(rs.getInt("id"));
                claim.setItemId(rs.getInt("item_id"));
                claim.setUserId(rs.getInt("user_id"));
                claim.setStatus(rs.getString("status"));
                claim.setClaimDate(rs.getTimestamp("claim_date"));
                claim.setItemName(rs.getString("item_name"));
                claim.setItemType(rs.getString("item_type"));
                claim.setUserName(rs.getString("username"));
                claims.add(claim);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return claims;
    }

    public boolean updateClaimStatus(int claimId, String status) {
        String sql = "UPDATE claims SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, claimId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getItemIdByClaim(int claimId) {
        String sql = "SELECT item_id FROM claims WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, claimId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("item_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean hasUserClaimedItem(int userId, int itemId) {
        String sql = "SELECT COUNT(*) FROM claims WHERE user_id = ? AND item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, itemId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
