package com.example.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object representing the user's complete shopping cart summary.
 */
public class CartResponse {

    private List<CartItemDto> items = new ArrayList<>();
    private int totalItems = 0;
    private double totalAmount = 0.0;

    public CartResponse() {
    }

    public CartResponse(List<CartItemDto> items, int totalItems, double totalAmount) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalAmount = Math.round(totalAmount * 100.0) / 100.0;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = Math.round(totalAmount * 100.0) / 100.0;
    }
}
