package com.lostfound.dao;

import com.lostfound.model.Item;
import com.lostfound.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (user_id, item_type, item_name, description, category_id, location_id, lost_found_date, status, contact_info, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction for creating location if needed
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                int categoryId = getCategoryId(conn, item.getCategory());
                int locationId = getOrCreateLocationId(conn, item.getLocation());
                
                pstmt.setInt(1, item.getUserId());
                pstmt.setString(2, item.getItemType());
                pstmt.setString(3, item.getItemName());
                pstmt.setString(4, item.getDescription());
                if (categoryId != -1) pstmt.setInt(5, categoryId); else pstmt.setNull(5, Types.INTEGER);
                if (locationId != -1) pstmt.setInt(6, locationId); else pstmt.setNull(6, Types.INTEGER);
                pstmt.setDate(7, item.getLostFoundDate());
                pstmt.setString(8, item.getStatus());
                pstmt.setString(9, item.getContactInfo());
                pstmt.setString(10, item.getImageUrl());
                
                int rows = pstmt.executeUpdate();
                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getCategoryId(Connection conn, String name) throws SQLException {
        String sql = "SELECT id FROM categories WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }

    private int getOrCreateLocationId(Connection conn, String name) throws SQLException {
        String sqlSelect = "SELECT id FROM locations WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        
        String sqlInsert = "INSERT INTO locations (name) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // Common SQL part for fetching items with Category and Location names
    private static final String SELECT_BASE = 
        "SELECT i.*, u.username, c.name as category_name, l.name as location_name " +
        "FROM items i " +
        "JOIN users u ON i.user_id = u.id " +
        "LEFT JOIN categories c ON i.category_id = c.id " +
        "LEFT JOIN locations l ON i.location_id = l.id ";

    public List<Item> getAllApprovedItems(int page, int pageSize) {
        List<Item> items = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = SELECT_BASE + "WHERE i.status IN ('APPROVED', 'LISTED') ORDER BY i.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pageSize);
            pstmt.setInt(2, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Item> getItemsByStatus(String status) {
        List<Item> items = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE i.status = ? ORDER BY i.created_at DESC";
        boolean useGenericPending = "PENDING".equals(status);
        
        if (useGenericPending) {
             sql = SELECT_BASE + "WHERE i.status IN ('PENDING', 'PENDING_APPROVAL') ORDER BY i.created_at DESC";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (!useGenericPending) {
                pstmt.setString(1, status);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Item> getUserItems(int userId) {
        List<Item> items = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE i.user_id = ? ORDER BY i.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Item> searchItems(String query, String category, String location) {
        List<Item> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE + "WHERE i.status IN ('APPROVED', 'LISTED')");
        
        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (i.item_name LIKE ? OR i.description LIKE ?)");
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND c.name = ?");
        }
        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND l.name LIKE ?");
        }
        
        sql.append(" ORDER BY i.created_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (query != null && !query.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + query + "%");
                pstmt.setString(paramIndex++, "%" + query + "%");
            }
            if (category != null && !category.trim().isEmpty()) {
                pstmt.setString(paramIndex++, category);
            }
            if (location != null && !location.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + location + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Item> findPotentialMatches(Item newItem) {
        List<Item> matches = new ArrayList<>();
        String targetType = "LOST".equals(newItem.getItemType()) ? "FOUND" : "LOST";
        
        String sql = SELECT_BASE + 
                     "WHERE i.item_type = ? AND c.name = ? " + // Match Category Name
                     "AND (l.name LIKE ? OR DATEDIFF(i.lost_found_date, ?) BETWEEN -14 AND 14) " +
                     "AND i.status IN ('APPROVED', 'LISTED', 'PENDING', 'PENDING_APPROVAL')";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, targetType);
            pstmt.setString(2, newItem.getCategory()); 
            pstmt.setString(3, "%" + newItem.getLocation() + "%"); 
            pstmt.setDate(4, newItem.getLostFoundDate());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) matches.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    public boolean updateItemStatus(int itemId, String status) {
        String sql = "UPDATE items SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Item getItemById(int itemId) {
        String sql = SELECT_BASE + "WHERE i.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToItem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int getTotalApprovedItemsCount() {
        String sql = "SELECT COUNT(*) FROM items WHERE status IN ('APPROVED', 'LISTED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setItemType(rs.getString("item_type"));
        item.setItemName(rs.getString("item_name"));
        item.setDescription(rs.getString("description"));
        // IMPORTANT: Use alias names from JOIN
        item.setCategory(rs.getString("category_name")); 
        item.setLocation(rs.getString("location_name"));
        
        item.setLostFoundDate(rs.getDate("lost_found_date"));
        item.setStatus(rs.getString("status"));
        item.setContactInfo(rs.getString("contact_info"));
        item.setImageUrl(rs.getString("image_url"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        item.setUserName(rs.getString("username"));
        return item;
    }
}
