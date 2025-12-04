package com.lostfound.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.lostfound.model.Match;
import com.lostfound.util.DBConnection;

public class MatchDAO {

    public boolean addMatch(Match match) {
        String sql = "INSERT INTO matches (lost_item_id, found_item_id, match_score, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, match.getLostItemId());
            pstmt.setInt(2, match.getFoundItemId());
            pstmt.setInt(3, match.getMatchScore());
            pstmt.setString(4, match.getStatus());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
