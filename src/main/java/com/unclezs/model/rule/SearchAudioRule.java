package com.unclezs.model.rule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 有声搜索规则
 *
 * @author uncle
 * @date 2020/4/18 11:50
 */
@Data
public class SearchAudioRule implements Serializable, Rule {
    private static final long serialVersionUID = 1L;
    /**
     * 站点host 不带www
     * 如 http://www.unclezs.com  => unclezs.com
     */
    @TableId(type = IdType.NONE)
    private String site;
    /**
     * 站点名称
     */
    private String name;
    private String searchUrl;
    private String searchKey;
    private String method = "GET";
    /**
     * 搜索结果集
     */
    private String searchList;
    private String cover;
    /**
     * 目录地址规则
     */
    private String catalogList;
    /**
     * 章节名称
     */
    private String catalogName;
    /**
     * 章节地址
     */
    private String catalogUrl;

    private String speak;
    private String title;
    private String author;
    private String url;
    /**
     * 是否启用移动UA
     */
    private boolean client;
    /**
     * 精确搜索
     */
    private boolean strict;

    /**
     * 下一页
     */
    private String nextPage;

    /**
     * 权重
     */
    private Integer weight;
    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 编码
     */
    private String charset;
}
