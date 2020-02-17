package com.unclezs.crawl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.EscapeUtil;
import com.unclezs.utils.OsUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *本地小说解析器
 *@author unclezs.com
 *@date 2019.06.22 16:30
 */
public class LocalNovelLoader implements Serializable {
    public static final long serialVersionUID = 123456L;
    private String regex = "([ ]?第[一二三四五六七八九十1234567890 ]{1,10}[章卷节].+?)\r\n";
    private String path;
    private String[] content;
    private String name;
    private boolean isExist = false;//是否存在
    private List<String> chapters = new ArrayList<>();

    public LocalNovelLoader(String path) {
        this.path = path;
        this.name = path.substring(path.lastIndexOf('\\') + 1, path.lastIndexOf('.'));
        this.initLoad();
    }

    private void initLoad() {
        try {
            getChapters();
            isExist = true;
        } catch (Exception e) {
            isExist = false;
        }
    }

    //只加载一次
    public List<String> getChapters() throws Exception {
        if (chapters.size() != 0) {
            return chapters;
        }
        String content = loadFile();
        List<String> chapterName = new ArrayList<>();//章节名字
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            chapterName.add(m.group(1));
        }
        this.chapters = chapterName;
        return chapterName;
    }

    //只加载一次
    public String getContent(int index) throws Exception {
        if (content == null) {
            loadFile();
        }
        return content[index + 1];
    }

    //加载读取文件
    private String loadFile() throws Exception {
        String encode = OsUtil.codeFile(path);
        if (encode.contains("2312")) {
            encode = "GBK";
        }
        String text = FileUtil.readString(path, encode);
        content = text.split(regex);
        return text;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    //根据下标加载章节内容
    public Map<String, String> getCPageByIndex(int index) throws Exception {
        Map<String, String> map = new HashMap<>();
        //加载章节
        String buffer = "\r\n\r\n" + getContent(index);//正文
        map.put("content", buffer);
        //加载标题
        map.put("title", chapters.get(index));
        return map;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //释放文本
    public void free() {
        this.chapters = null;
        this.content = null;
    }

    public boolean isExist() {
        return isExist;
    }
}
