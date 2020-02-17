package com.unclezs.downloader;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.unclezs.adapter.DownloadAdapter;
import com.unclezs.crawl.AudioNovelSpider;
import com.unclezs.model.AudioBook;
import com.unclezs.model.AudioChapter;
import com.unclezs.model.DownloadConfig;
import cn.hutool.core.io.FileUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 音频下载器
 *
 * @author unclezs.com
 * @date 2019.07.08 20:23
 */
public class AudioDownloader implements DownloadAdapter {
    /**
     * 下载配置
     */
    private DownloadConfig config;
    private AudioBook book;
    /**
     * 完成数量
     */
    private List<Integer> overNum;
    private List<String> taskUrl;
    private ExecutorService service;
    /**
     * 是否开启手机模式下载
     */
    private boolean isPhone;
    private List<Boolean> isShutdown = new ArrayList<>();

    public AudioDownloader(DownloadConfig config, AudioBook book, boolean isPhone) {
        this.config = config;
        this.isPhone = isPhone;
        this.book = book;
        this.overNum = Collections.synchronizedList(new ArrayList<>(book.getChapters().size()));
        //获取任务列表
        this.taskUrl = new ArrayList<>(book.getChapters().size());
        for (AudioChapter chapter : book.getChapters()) {
            this.taskUrl.add(chapter.getUrl());
        }
    }

    @Override
    public void start() {
        //保存封面
        ThreadUtil.execute(() -> {
            String img = "."+File.separator+"image"+File.separator+"audio"+File.separator + IdUtil.simpleUUID() + ".jpg";
            HttpUtil.downloadFile(book.getImageUrl(), img);
            book.setImageUrl(img);
        });
        //更新路径
        config.setPath(config.getPath() + book.getTitle());
        //计算需要线程数量
        int threadNum = (int) Math.ceil(book.getChapters().size() * 1.0 / config.getPerThreadDownNum());
        //最大50开辟50个线程的线程池
        service = ThreadUtil.newExecutor(threadNum % 50);
        //任务分发
        int st;
        int end = 0;
        //任务监控
        List<Future<String>> task = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            //标志正在运行
            isShutdown.add(true);
            //最后一次不足每个线程下载章节数量，则全部下载
            if (i == threadNum - 1) {
                st = end;
                end = book.getChapters().size();
            } else {//每次增加配置数量
                st = end;
                end += config.getPerThreadDownNum();
            }
            final int taskId = i;
            //开始下标
            final int sIndex = st;
            //结束下标
            final int eIndex = end;
            task.add(service.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    AudioNovelSpider spider = new AudioNovelSpider();
                    spider.setPhone(isPhone);
                    for (int j = sIndex; j < eIndex; j++) {
                        if (!isShutdown.get(taskId)) {
                            return null;//监听中途停止
                        }
                        String src = spider.getSrc(taskUrl.get(j));
                        String path = getDownloadPath(j, src);
                        for (int k = 0; k < 10; k++) {//重试10次，全部失败则下载失败
                            try {
                                download(path, src);
                                break;
                            } catch (Exception e) {
                                if (k == 9) {
                                    System.out.println("下载失败" + src);
                                }
                                Thread.sleep(config.getSleepTime());
                            }
                        }
                        overNum.add(j);
                        Thread.sleep(config.getSleepTime());
                    }
                    return null;
                }
            }));
        }
        for (Future<String> future : task) {
            try {
                //阻塞等待完成
                future.get();
            } catch (Exception e) {
                //下载失败
                System.out.println("线程异常终止");
            }
        }
        //销毁线程池
        service.shutdown();
    }

    //下载器
    private void download(String path, String src) throws IOException {
        File file = FileUtil.file(path);
        URL url = new URL(src);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        //根据是否为手机模式设置UA
        if (isPhone) {
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 9; zh-CN; MI MAX 3 Build/PKQ1.190118.001) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.108 UCBrowser/12.5.0.1030 Mobile Safari/537.36");
        } else {
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        }
        IoUtil.copy(connection.getInputStream(), FileUtil.getOutputStream(path));
        connection.disconnect();
    }

    @Override
    public void stop() {
        //全部标志为停止
        for (int i = 0; i < isShutdown.size(); i++) {
            isShutdown.set(i, false);
        }
        service.shutdown();
        service.shutdownNow();
    }

    @Override
    public int getOverNum() {
        return overNum.size();
    }

    @Override
    public int getMaxNum() {
        return taskUrl.size();
    }

    @Override
    public String getImgPath() {
        return book.getImageUrl();
    }

    @Override
    public String getTitle() {
        return book.getTitle();
    }

    @Override
    public String getPath() {
        return config.getPath();
    }

    @Override
    public String getType() {
        return "音频文件";
    }

    //获取下载路径及文件名字
    private String getDownloadPath(int index, String src) {
        String suffix = ".mp3";
        if (src.contains("m4a")) {
            suffix = ".m4a";
        }
        return config.getPath() +File.separator+ book.getChapters().get(index).getTitle() + suffix;
    }
}
