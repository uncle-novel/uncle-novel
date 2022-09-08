package com.unclezs.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 下载记录
 *
 * @author unclezs.com
 * @date 2019.07.06 19:55
 */
@Data
public class DownloadRecord {
    @TableId(type = IdType.NONE)
    private String id;
    /**
     * 类型
     */
    private String type;
    /**
     * 路径
     */
    private String path;
    /**
     * 书名
     */
    private String title;
    /**
     * 下载时间
     */
    private String datetime;
    /**
     * 图片路径
     */
    private String cover;

    /**
     * 大小
     */
    private String size;
}
