package com.leolxthy.blog.Entity;

import lombok.Data;

@Data
public class TableColumn {
    private String label;
    private String width;
    private boolean display = true;

    public TableColumn(String label, String width) {
        this.label = label;
        this.width = width;
    }

    public TableColumn(boolean display, String width, String label) {
        this.display = display;
        this.width = width;
        this.label = label;
    }
}
