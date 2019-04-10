package ru.surdasha.cats.presentation.models;

public class CatUI {
    private long Id;
    private String serverId;
    private String url;
    private int imageHeight;
    private int imageWidth;
    private int screenImageWidth;
    private int screenImageHeight;
    private long tempDownloadId;


    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getScreenImageWidth() {
        return screenImageWidth;
    }

    public void setScreenImageWidth(int screenImageWidth) {
        this.screenImageWidth = screenImageWidth;
    }

    public int getScreenImageHeight() {
        return screenImageHeight;
    }

    public void setScreenImageHeight(int screenImageHeight) {
        this.screenImageHeight = screenImageHeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatUI catUI = (CatUI) o;
        return serverId.equals(catUI.getServerId());
    }

    public long getTempDownloadId() {
        return tempDownloadId;
    }

    public void setTempDownloadId(long tempDownloadId) {
        this.tempDownloadId = tempDownloadId;
    }
}
