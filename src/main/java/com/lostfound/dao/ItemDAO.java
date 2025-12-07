package com.lostfound.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lostfound.model.Item;
import com.lostfound.util.DBConnection;

public class ItemDAO {

    public int addItem(Item item) {
        String sql = "INSERT INTO items (user_id, item_type, item_name, description, category_id, location_id, lost_found_date, status, contact_info, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, item.getUserId());
            pstmt.setString(2, item.getItemType());
            pstmt.setString(3, item.getItemName());
            pstmt.setString(4, item.getDescription());
            pstmt.setInt(5, item.getCategoryId());
            pstmt.setInt(6, item.getLocationId());
            pstmt.setDate(7, item.getLostFoundDate());
            pstmt.setString(8, item.getStatus());
            pstmt.setString(9, item.getContactInfo());
            pstmt.setString(10, item.getImageUrl());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Item> getAllApprovedItems(int limit, int offset) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, u.username, c.name as category_name, l.name as location_name " +
                     "FROM items i " +
                     "JOIN users u ON i.user_id = u.id " +
                     "LEFT JOIN categories c ON i.category_id = c.id " +
                     "LEFT JOIN locations l ON i.location_id = l.id " +
                     "WHERE i.status = 'LISTED' " +
                     "ORDER BY i.created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Item> getItemsByStatus(String status) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, u.username, c.name as category_name, l.name as location_name " +
                     "FROM items i " +
                     "JOIN users u ON i.user_id = u.id " +
                     "LEFT JOIN categories c ON i.category_id = c.id " +
                     "LEFT JOIN locations l ON i.location_id = l.id " +
                     "WHERE i.status = ? ORDER BY i.created_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Item> getUserItems(int userId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, u.username, c.name as category_name, l.name as location_name " +
                     "FROM items i " +
                     "JOIN users u ON i.user_id = u.id " +
                     "LEFT JOIN categories c ON i.category_id = c.id " +
                     "LEFT JOIN locations l ON i.location_id = l.id " +
                     "WHERE i.user_id = ? ORDER BY i.created_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public Item getItemById(int itemId) {
        String sql = "SELECT i.*, u.username, c.name as category_name, l.name as location_name " +
                     "FROM items i " +
                     "JOIN users u ON i.user_id = u.id " +
                     "LEFT JOIN categories c ON i.category_id = c.id " +
                     "LEFT JOIN locations l ON i.location_id = l.id " +
                     "WHERE i.id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItem(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Item> searchItems(String query, int categoryId, int locationId, int limit, int offset) {
        List<Item> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT i.*, u.username, c.name as category_name, l.name as location_name " +
                "FROM items i " +
                "JOIN users u ON i.user_id = u.id " +
                "LEFT JOIN categories c ON i.category_id = c.id " +
                "LEFT JOIN locations l ON i.location_id = l.id " +
                "WHERE i.status = 'LISTED'");

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (i.item_name LIKE ? OR i.description LIKE ?)");
        }
        if (categoryId > 0) {
            sql.append(" AND i.category_id = ?");
        }
        if (locationId > 0) {
            sql.append(" AND i.location_id = ?");
        }

        sql.append(" ORDER BY i.created_at DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (query != null && !query.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + query + "%");
                pstmt.setString(paramIndex++, "%" + query + "%");
            }
            if (categoryId > 0) {
                pstmt.setInt(paramIndex++, categoryId);
            }
            if (locationId > 0) {
                pstmt.setInt(paramIndex++, locationId);
            }
            
            pstmt.setInt(paramIndex++, limit);
            pstmt.setInt(paramIndex++, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean updateItemStatus(int itemId, String status) {
        String sql = "UPDATE items SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, itemId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Matching Logic: Find potential matches for a newly added item
    public List<Item> findPotentialMatches(Item newItem) {
        List<Item> matches = new ArrayList<>();
        String targetType = newItem.getItemType().equals("LOST") ? "FOUND" : "LOST";
        
        // Simple matching: Same category AND (Same location OR Nearby Date)
        // For simplicity, we'll just check Category + Location
        String sql = "SELECT i.*, u.username, c.name as category_name, l.name as location_name " +
                     "FROM items i " +
                     "JOIN users u ON i.user_id = u.id " +
                     "LEFT JOIN categories c ON i.category_id = c.id " +
                     "LEFT JOIN locations l ON i.location_id = l.id " +
                     "WHERE i.item_type = ? " +
                     "AND i.category_id = ? " +
                     "AND i.location_id = ? " +
                     "AND i.status != 'REJECTED' " + 
                     "ORDER BY i.created_at DESC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, targetType);
            pstmt.setInt(2, newItem.getCategoryId());
            pstmt.setInt(3, newItem.getLocationId());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    matches.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setItemType(rs.getString("item_type"));
        item.setItemName(rs.getString("item_name"));
        item.setDescription(rs.getString("description"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setLocationId(rs.getInt("location_id"));
        item.setCategoryName(rs.getString("category_name"));
        item.setLocationName(rs.getString("location_name"));
        item.setLostFoundDate(rs.getDate("lost_found_date"));
        item.setStatus(rs.getString("status"));
        item.setContactInfo(rs.getString("contact_info"));
        item.setImageUrl(rs.getString("image_url"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        item.setUserName(rs.getString("username"));
        return item;
    }
}
