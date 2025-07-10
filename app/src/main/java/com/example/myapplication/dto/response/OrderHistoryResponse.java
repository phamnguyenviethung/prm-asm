package com.example.myapplication.dto.response;

import java.util.List;

public class OrderHistoryResponse {
    private List<OrderHistoryItem> items;
    private int totalItems;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private boolean hasPreviousPage;
    private boolean hasNextPage;

    public OrderHistoryResponse() {
    }

    public List<OrderHistoryItem> getItems() {
        return items;
    }

    public void setItems(List<OrderHistoryItem> items) {
        this.items = items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}
