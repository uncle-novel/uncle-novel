package com.unclezs.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author unclezs.com
 * @date 2019.06.26 21:33
 */
@Data
@NoArgsConstructor
public class Chapter {
    /**
     * 章节名字
     */
    private String name;
    /**
     * 章节url
     */
    private String url;

    /**
     * 章节内容位置
     */
    private String contentPath;

    public Chapter(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
