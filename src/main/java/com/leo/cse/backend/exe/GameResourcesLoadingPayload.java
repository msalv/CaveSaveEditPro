package com.leo.cse.backend.exe;

public class GameResourcesLoadingPayload {
    public final String title;
    public final String description;
    public final int progress;
    public final int total;

    public GameResourcesLoadingPayload(String title, String description, int progress, int total) {
        this.title = title;
        this.description = description;
        this.progress = progress;
        this.total = total;
    }
}
