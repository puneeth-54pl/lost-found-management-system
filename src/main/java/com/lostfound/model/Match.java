package com.lostfound.model;

import java.sql.Timestamp;

public class Match {
    private int id;
    private int lostItemId;
    private int foundItemId;
    private int matchScore;
    private String status;
    private Timestamp createdAt;
    
    // Optional: Full Item objects for display
    private Item lostItem;
    private Item foundItem;

    public Match() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLostItemId() { return lostItemId; }
    public void setLostItemId(int lostItemId) { this.lostItemId = lostItemId; }

    public int getFoundItemId() { return foundItemId; }
    public void setFoundItemId(int foundItemId) { this.foundItemId = foundItemId; }

    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int matchScore) { this.matchScore = matchScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Item getLostItem() { return lostItem; }
    public void setLostItem(Item lostItem) { this.lostItem = lostItem; }

    public Item getFoundItem() { return foundItem; }
    public void setFoundItem(Item foundItem) { this.foundItem = foundItem; }
}
