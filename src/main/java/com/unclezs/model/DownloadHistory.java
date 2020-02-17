package com.unclezs.model;

/*
 *@author unclezs.com
 *@date 2019.07.06 19:55
 */
public class DownloadHistory {
    private String id;//编号
    private String type;//类型
    private String path;//路径
    private String title;//书名
    private String time;//下载时间
    private String imgPath;//图片路径

    public DownloadHistory(String type, String path, String title, String time, String imgPath) {
        this.type = type;
        this.path = path;
        this.title = title;
        this.time = time;
        this.imgPath = imgPath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "DownloadHistory{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
