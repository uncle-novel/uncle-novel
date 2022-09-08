package com.unclezs.model.rule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索文本小说规则
 *
 * @author uncle
 * @date 2020/4/17 20:39
 */
@Data
public class SearchTextRule implements Rule, Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    @TableId(type = IdType.NONE)
    private String site;
    private String title;
    private String cover;
    private String author;
    /**
     * 模糊查询
     */
    private boolean strict = false;
    /**
     * 章节目录地址规则
     */
    private String url;
    /**
     * 关键词
     */
    private String searchKey;
    /**
     * 结果集规则
     */
    private String resultList;
    /**
     * 搜索链接
     */
    private String searchLink;

    /**
     * 权重越高 越先被搜索
     */
    private Integer weight;

    private String charset;
    private String method;

    private String nextPage;

    /**
     * 是否启用
     */
    private boolean enabled;
}
