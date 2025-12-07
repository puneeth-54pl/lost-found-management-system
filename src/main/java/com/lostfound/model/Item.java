package com.lostfound.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Item implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private String itemType; // LOST or FOUND
	private String itemName;
	private String description;
	private int categoryId;
	private int locationId;
	private String categoryName; // For display
	private String locationName; // For display
	private Date lostFoundDate;
	private String status; // PENDING_APPROVAL, LISTED, RESOLVED, REJECTED
	private String contactInfo;
	private String imageUrl;
	private Timestamp createdAt;

	// Helper field for display
	private String userName;

	public Item() {
	}

	public Item(int id, int userId, String itemType, String itemName, String description, int categoryId,
			int locationId, Date lostFoundDate, String status, String contactInfo, String imageUrl, Timestamp createdAt) {
		this.id = id;
		this.userId = userId;
		this.itemType = itemType;
		this.itemName = itemName;
		this.description = description;
		this.categoryId = categoryId;
		this.locationId = locationId;
		this.lostFoundDate = lostFoundDate;
		this.status = status;
		this.contactInfo = contactInfo;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
	}

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Date getLostFoundDate() {
		return lostFoundDate;
	}

	public void setLostFoundDate(Date lostFoundDate) {
		this.lostFoundDate = lostFoundDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
