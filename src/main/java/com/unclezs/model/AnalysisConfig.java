package com.unclezs.model;

/**
 *解析小说配置文件
 *@author unclezs.com
 *@date 2019.07.02 23:37
 */
public class AnalysisConfig {
    private String chapterHead;//章节头
    private String chapterTail;//章节尾
    private String contentHead;//正文头
    private String contentTail;//正文尾
    private String cookies;
    private String userAgent;//浏览器标识
    private String adStr;//广告字符
    private String rule;
    private boolean chapterFilter;//章节是否过滤
    private boolean chapterSort;//章节是否排序
    private boolean ncrToZh;//ncr转中文
    private boolean traToSimple;//繁体转简体
    private boolean startDynamic;//启用动态网页爬取
    public AnalysisConfig() {
    }

    public boolean isStartDynamic() {
        return startDynamic;
    }

    public void setStartDynamic(boolean startDynamic) {
        this.startDynamic = startDynamic;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getChapterHead() {
        return chapterHead;
    }

    public void setChapterHead(String chapterHead) {
        this.chapterHead = chapterHead;
    }

    public String getChapterTail() {
        return chapterTail;
    }

    public void setChapterTail(String chapterTail) {
        this.chapterTail = chapterTail;
    }

    public String getContentHead() {
        return contentHead;
    }

    public void setContentHead(String contentHead) {
        this.contentHead = contentHead;
    }

    public String getContentTail() {
        return contentTail;
    }

    public void setContentTail(String contentTail) {
        this.contentTail = contentTail;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getAdStr() {
        return adStr;
    }

    public void setAdStr(String adStr) {
        this.adStr = adStr;
    }

    public boolean isChapterFilter() {
        return chapterFilter;
    }

    public void setChapterFilter(boolean chapterFilter) {
        this.chapterFilter = chapterFilter;
    }

    public boolean isChapterSort() {
        return chapterSort;
    }

    public void setChapterSort(boolean chapterSort) {
        this.chapterSort = chapterSort;
    }

    public boolean isNcrToZh() {
        return ncrToZh;
    }

    public void setNcrToZh(boolean ncrToZh) {
        this.ncrToZh = ncrToZh;
    }

    public boolean isTraToSimple() {
        return traToSimple;
    }

    public void setTraToSimple(boolean traToSimple) {
        this.traToSimple = traToSimple;
    }

    @Override
    public String toString() {
        return "AnalysisConfig{" +
                "chapterHead='" + chapterHead + '\'' +
                ", chapterTail='" + chapterTail + '\'' +
                ", contentHead='" + contentHead + '\'' +
                ", contentTail='" + contentTail + '\'' +
                ", cookies='" + cookies + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", adStr='" + adStr + '\'' +
                ", chapterFilter=" + chapterFilter +
                ", chapterSort=" + chapterSort +
                ", ncrToZh=" + ncrToZh +
                ", traToSimple=" + traToSimple +
                '}';
    }
}
