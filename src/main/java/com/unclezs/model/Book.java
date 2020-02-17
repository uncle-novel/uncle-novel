package com.unclezs.model;

/*
 *小说信息
 *@author unclezs.com
 *@date 2019.06.23 08:44
 */
public class Book {
    private String name;//名字
    private String path;//路径网络或者本地
    private String img;//图片
    private Integer id;//id
    private Integer cpage;//当前阅读章节
    private String charset;//编码
    private Integer isWeb;//是否为网络书籍
    private Double vValue;//上次阅读滑块位置

    public Book() {
    }


    public Book(String name, String path, String img) {
        this.name = name;
        this.path = path;
        this.img = img;
        this.isWeb = 0;
        this.cpage = 0;
        this.charset = "UTF-8";
        this.vValue=0.0;
    }

    public Book(String name, String path, String img, Integer id, Integer cpage, String charset, Integer isWeb, Double vValue) {
        this.name = name;
        this.path = path;
        this.img = img;
        this.id = id;
        this.cpage = cpage;
        this.charset = charset;
        this.isWeb = isWeb;
        this.vValue = vValue;
    }

    public Double getvValue() {
        return vValue;
    }

    public void setvValue(Double vValue) {
        this.vValue = vValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCpage() {
        return cpage;
    }

    public void setCpage(Integer cpage) {
        this.cpage = cpage;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getIsWeb() {
        return isWeb;
    }

    public void setIsWeb(Integer isWeb) {
        this.isWeb = isWeb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", img='" + img + '\'' +
                ", id=" + id +
                ", cpage=" + cpage +
                ", charset='" + charset + '\'' +
                ", isWeb=" + isWeb +
                ", vValue=" + vValue +
                '}';
    }
}
