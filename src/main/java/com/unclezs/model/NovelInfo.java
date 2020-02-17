package com.unclezs.model;

/*
 *小说信息
 *@author unclezs.com
 *@date 2019.06.20 21:22
 */
public class NovelInfo {
    private String url;//目录链接
    private String author;//作者
    private String title;//名字

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "NovelInfo{" +
                "url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
