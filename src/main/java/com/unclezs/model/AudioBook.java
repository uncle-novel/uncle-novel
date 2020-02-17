package com.unclezs.model;

import java.util.List;

/**
 * 有声小说实体
 *
 * @author unclezs.com
 * @date 2019.07.07 22:57
 */
public class AudioBook {
    private String author;//作者
    private String broadCasting;//播音
    private String title;//书名
    private String imageUrl;//缩略图
    private String url;//目录地址
    private int lastIndex;//上次听到的章节
    private double lastLocation;//上次听到的位置
    private List<AudioChapter> chapters;//章节列表
    private int id = -1;
    private String lastChapter;//上次听到的一章节名字

    public AudioBook() {
    }

    public AudioBook(String author, String broadCasting, String title, String imageUrl, String url) {
        this.author = author;
        this.broadCasting = broadCasting;
        this.title = title;
        this.imageUrl = imageUrl;
        this.url = url;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public double getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(double lastLocation) {
        this.lastLocation = lastLocation;
    }

    public List<AudioChapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<AudioChapter> chapters) {
        this.chapters = chapters;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBroadCasting() {
        return broadCasting;
    }

    public void setBroadCasting(String broadCasting) {
        this.broadCasting = broadCasting;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "AudioBook{" +
                "author='" + author + '\'' +
                ", broadCasting='" + broadCasting + '\'' +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", url='" + url + '\'' +
                ", lastIndex=" + lastIndex +
                ", lastLocation=" + lastLocation +
                ", id=" + id +
                ", lastChapter='" + lastChapter + '\'' +
                '}';
    }
}
