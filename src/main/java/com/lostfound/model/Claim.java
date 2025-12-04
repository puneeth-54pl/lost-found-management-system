package com.lostfound.model;

import java.sql.Timestamp;

public class Claim {
    private int id;
    private int itemId;
    private int userId;
    private String status;
    private Timestamp claimDate;
    
    // For display purposes
    private String itemName;
    private String userName;
    private String itemType;

    public Claim() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getClaimDate() { return claimDate; }
    public void setClaimDate(Timestamp claimDate) { this.claimDate = claimDate; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
}
