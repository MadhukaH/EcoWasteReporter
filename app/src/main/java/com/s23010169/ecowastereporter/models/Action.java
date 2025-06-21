package com.s23010169.ecowastereporter.models;

public class Action {
    private final int iconResId;
    private final String title;
    private final Runnable onClick;

    public Action(int iconResId, String title, Runnable onClick) {
        this.iconResId = iconResId;
        this.title = title;
        this.onClick = onClick;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    public Runnable getOnClick() {
        return onClick;
    }
} 