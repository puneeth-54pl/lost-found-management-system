package com.lostfound.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Item implements Serializable {
    private int id;
    private int userId;
    private String itemType; // LOST or FOUND
    private String itemName;
    private String description;
    private String category;
    private String location;
    private Date lostFoundDate;
    private String status; // PENDING, APPROVED, REJECTED, RETURNED
    private String contactInfo;
    private Timestamp createdAt;
    
    // Helper field for display
    private String userName;

    public Item() {}

    public Item(int id, int userId, String itemType, String itemName, String description, String category, String location, Date lostFoundDate, String status, String contactInfo, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.location = location;
        this.lostFoundDate = lostFoundDate;
        this.status = status;
        this.contactInfo = contactInfo;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Date getLostFoundDate() { return lostFoundDate; }
    public void setLostFoundDate(Date lostFoundDate) { this.lostFoundDate = lostFoundDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
