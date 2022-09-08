package com.unclezs.model;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 有声小说实体
 *
 * @author unclezs.com
 * @date 2019.07.07 22:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioBook {
    @TableId(type = IdType.NONE)
    private String id = IdUtil.simpleUUID();
    /**
     * 作者
     */
    private String author;
    /**
     * 播音
     */
    private String speak;
    /**
     * 书名
     */
    private String title;
    /**
     * 缩略图
     */
    private String cover;
    /**
     * 目录地址
     */
    private String url;
    /**
     * 上次听到的章节
     */
    private int lastChapterIndex;
    /**
     * 上次听到的位置
     */
    private double lastTime;
    /**
     * 章节列表
     */
    @TableField(exist = false)
    private List<AudioChapter> chapters;
    /**
     * 上次听到的一章节名字
     */
    private String lastChapterName;

    private String updateTime = DateUtil.now();

    public AudioBook(String author, String speak, String title, String cover, String url) {
        this.author = author;
        this.speak = speak;
        this.title = title;
        this.cover = cover;
        this.url = url;
    }

    public String getLastChapterName() {
        if (chapters != null && chapters.size() > 0) {
            return chapters.get(lastChapterIndex).getTitle();
        }
        return null;
    }

    public String lastChapterName() {
        return lastChapterName;
    }

}
