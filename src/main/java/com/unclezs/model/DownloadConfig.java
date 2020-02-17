package com.unclezs.model;

/*
 *@author unclezs.com
 *@date 2019.07.05 19:33
 */
public class DownloadConfig {
    private String path;//下载路径
    private int perThreadDownNum;//每个西线程多少章节
    private Integer sleepTime;//每章节延迟
    private boolean mergeFile;//下载完成后是否合并
    private String format;//下载格式

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public int getPerThreadDownNum() {
        return perThreadDownNum;
    }

    public void setPerThreadDownNum(int perThreadDownNum) {
        this.perThreadDownNum = perThreadDownNum;
    }

    public Integer getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Integer sleepTime) {
        this.sleepTime = sleepTime;
    }

    public boolean isMergeFile() {
        return mergeFile;
    }

    public void setMergeFile(boolean mergeFile) {
        this.mergeFile = mergeFile;
    }

    @Override
    public String toString() {
        return "DownloadConfig{" +
                "path='" + path + '\'' +
                ", perThreadDownNum='" + perThreadDownNum + '\'' +
                ", sleepTime=" + sleepTime +
                ", mergeFile=" + mergeFile +
                '}';
    }
}
