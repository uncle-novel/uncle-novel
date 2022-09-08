package com.unclezs.downloader;

import java.util.function.Function;

/**
 * 下载适配器
 *
 * @author unclezs.com
 * @date 2019.07.07 09:56
 */
public interface Downloader {
    /**
     * 开始任务
     */
    void start(Function<String, String> contentSpider);

    /**
     * 停止任务
     */
    void stop();

    /**
     * 暂停
     */
    void pause();

    /**
     * 获取完成数量
     *
     * @return /
     */
    int current();

    /**
     * 总计数量
     *
     * @return /
     */
    int total();

    /**
     * 总计数量
     *
     * @return /
     */
    int errorNum();

    /**
     * 缩略图本地地址
     *
     * @return /
     */
    String getCover();

    /**
     * 任务标题
     *
     * @return /
     */
    String getTitle();

    /**
     * 本地保存路径
     *
     * @return /
     */
    String getPath();

    /**
     * 文件类型
     *
     * @return /
     */
    String getType();

}
