package com.unclezs.model;

/*
 *@author unclezs.com
 *@date 2019.07.07 20:38
 */
public class AudioChapter {
    private String url;
    private String title;

    public AudioChapter(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "AudioChapter{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
