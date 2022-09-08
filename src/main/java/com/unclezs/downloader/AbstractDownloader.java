package com.unclezs.downloader;

import com.unclezs.downloader.config.DownloaderState;
import com.unclezs.utils.thead.CountableThreadPool;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抽象公共成员
 *
 * @author uncle
 * @date 2020/4/17 17:01
 */
@Getter
@Setter
public abstract class AbstractDownloader implements Downloader, Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 线程池
     */
    transient CountableThreadPool pool;


    transient DownloaderState state = DownloaderState.FREE;
    /**
     * 保存到的文件
     */
    transient File saveFile;
    /**
     * 当前下载了的数量
     */
    AtomicInteger current = new AtomicInteger(0);
    Long startTime = System.currentTimeMillis();
    String fileName;
    /**
     * 失败数量
     */
    AtomicInteger error = new AtomicInteger(0);
    boolean startDynamic = false;
    String target = getClass().getName();

    @Override
    public int current() {
        return current.get();
    }

    @Override
    public int errorNum() {
        return error.get();
    }

    /**
     * 是否完成
     *
     * @return /
     */
    public abstract boolean finished();

    public String processText() {
        return String.format("%s/%s", current(), total());
    }
}
