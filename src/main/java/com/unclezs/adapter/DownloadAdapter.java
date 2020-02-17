package com.unclezs.adapter;

/*
 *下载是配器
 *@author unclezs.com
 *@date 2019.07.07 09:56
 */
public interface DownloadAdapter {
    void start();//开始任务
    void stop();//停止任务
    int getOverNum();//获取完成数量
    int getMaxNum();//总计数量
    String getImgPath();//缩略图本地地址
    String getTitle();//标题
    String getPath();//本地保存路径
    String getType();//文件类型
}
