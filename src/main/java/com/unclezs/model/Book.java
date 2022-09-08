package com.unclezs.model;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 小说信息
 *
 * @author unclezs.com
 * @date 2019.06.23 08:44
 */
@Data
@NoArgsConstructor
public class Book implements Serializable {
    /**
     * 名字
     */
    private String name;
    /**
     * 路径网络或者本地
     */
    private String path;
    /**
     * 图片
     */
    private String cover;
    /**
     * id
     */
    @TableId(type = IdType.NONE)
    private String id;
    /**
     * 当前阅读章节
     */
    private Integer chapterIndex;
    /**
     * 是否为网络书籍
     */
    private boolean web;
    /**
     * 上次阅读滑块位置
     */
    private Double location = 0.0001;

    /**
     * 正在阅读的章节列表，初始化时候显示
     * 0,1,2
     */
    private String readingChapter = "0,1";

    /**
     * 格式化之后存储的章节JSON位置 详情见loader
     */
    private String chapterPath;

    /**
     * 自动更新
     */
    private boolean autoUpdate;

    public Book(String name, String path, String cover) {
        this.name = name;
        this.path = path;
        this.cover = cover;
        this.chapterIndex = 0;
        this.id = IdUtil.simpleUUID();
    }

    public Double getLocation() {
        return location == 0 ? 0.00001 : location;
    }
}
