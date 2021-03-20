package com.unclezs.novel.app.jfx.launcher.model;

import lombok.Data;

import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2021/03/21 11:35
 */
@Data
public class Manifest {
    /**
     * 是否为增量更新
     */
    private boolean incremental = true;
    /**
     * 服务器地址
     */
    private String serverUri;
    /**
     * 更新日志链接
     */
    private String changeLogUri;
    /**
     * 更新内容
     */
    private List<String> changeLog;
    /**
     * app.xml文件位置
     */
    private String appUri;
    /**
     * 依赖
     */
    private List<String> libs;
    private List<String> natives;

    public static void main(String[] args) {


    }

}
