package com.unclezs.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 小说信息
 *
 * @author unclezs.com
 * @date 2019.06.20 21:22
 */
@Data
@AllArgsConstructor
public class NovelInfo implements Serializable {
    /**
     * 目录链接
     */
    private String url;
    /**
     * 作者
     */
    private String author;
    /**
     * 名字
     */
    private String title;
    /**
     * 封面
     */
    private String cover;
}
