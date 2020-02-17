package com.unclezs.downloader;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.unclezs.adapter.DownloadAdapter;
import com.unclezs.crawl.NovelSpider;
import com.unclezs.model.DownloadConfig;
import com.unclezs.utils.CharacterUtil;
import com.unclezs.utils.EpubUtil;
import com.unclezs.utils.OsUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 文本小说下载器
 */
public class NovelDownloader implements DownloadAdapter {
    private List<String> list;//URL列表
    private List<String> nameList;//章节名字列表
    private DownloadConfig config;//下载配置
    private NovelSpider spider;//解析配置
    private List overNum = Collections.synchronizedList(new ArrayList<>());//完成数量
    private ExecutorService service;
    private String imgPath;//缩略图
    private String path;//下载位置
    private List<Boolean> isShutdown = new ArrayList<>();//监听停止

    public NovelDownloader(List<String> list, List<String> nameList, DownloadConfig config, NovelSpider spider) {
        this.list = list;
        this.config = config;
        this.spider = spider;
        this.nameList = nameList;
    }

    @Override
    public void start() {
        //爬取图片
        //保存封面
        ThreadUtil.execute(() -> {
            String imgURl = spider.crawlDescImage(spider.getConfig().get("title"));
            imgPath = "." + File.separator + "image" + File.separator + spider.getConfig().get("title") + ".jpg";
            HttpUtil.downloadFile(imgURl, imgPath);
        });
        //获取目录
        // 分块目录位置
        String tempPath = config.getPath() +"block"+ File.separator + spider.getConfig().get("title") + File.separator;
        //下载位置
        path = config.getPath();
        File f = new File(tempPath);
        //删除原有的
        FileUtil.del(f);
        FileUtil.mkdir(f);
        //计算需要线程数量
        int threadNum = (int) Math.ceil(list.size() * 1.0 / config.getPerThreadDownNum());
        //如果不合并则单线程下载
        if (!config.isMergeFile()) {
            config.setPerThreadDownNum(list.size() + 1);
            threadNum = 1;
        }
        //最多50个线程
        service = ThreadUtil.newExecutor(threadNum % 50);
        //任务分块
        int st;
        int end = 0;
        //任务监控
        List<Future<String>> Task = new ArrayList<>();
        final int num = threadNum;
        for (int i = 0; i < threadNum; i++) {
            isShutdown.add(true);
            //最后一次不足每个线程下载章节数量，则全部下载
            if (i == threadNum - 1) {
                st = end;
                end = list.size();
            } else {//每次增加配置数量
                st = end;
                end += config.getPerThreadDownNum();
            }
            final int taskId = i;
            final int sIndex = st;
            final int eIndex = end;
            Task.add(service.submit(new Callable<String>() {
                @Override
                public String call() {
                    String path;
                    //分块下载
                    for (int i = sIndex; i < eIndex; i++) {
                        //是否合并，不合并则每个章节按照标题命名
                        if (config.isMergeFile() && num != 1) {
                            path = tempPath + sIndex + "-" + eIndex + ".txt";
                        } else if (config.isMergeFile() && num == 1) {
                            path = tempPath + i + "-" + i + ".txt";
                        } else {
                            path = tempPath + nameList.get(i) + ".txt";
                        }
                        try (PrintWriter out = new PrintWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(FileUtil.file(path), true), spider.getConfig().get("charset")), true)) {
                            StringBuffer content;
                            if (!isShutdown.get(taskId)) {
                                return null;
                            }
                            content = new StringBuffer();
                            for (int k = 0; k < 10; k++) {//重试10次
                                try {
                                    content.append(spider.getContent(list.get(i), spider.getConfig().get("charset")));
                                    break;
                                } catch (Exception e) {
                                    if (k == 9) {
                                        System.out.println("下载失败" + list.get(i));
                                    }
                                    Thread.sleep(config.getSleepTime());
                                }
                            }
                            if (spider.getConf().isNcrToZh()) {//NCR转中文
                                String tmp = CharacterUtil.NCR2Chinese(content.toString());
                                content = new StringBuffer();
                                content.append(tmp);
                            }
                            if (spider.getConf().isTraToSimple()) {//繁体转简体
                                String tmp = CharacterUtil.traditional2Simple(content.toString());
                                content = new StringBuffer();
                                content.append(tmp);
                            }
                            out.println(nameList.get(i));
                            out.println(content.toString());
                            //没有到最后一个
                            if (overNum.size() != (getMaxNum() - 1)) {
                                overNum.add(i);
                            }
                            Thread.sleep(config.getSleepTime());
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    return null;
                }
            }));
        }
        service.shutdown();
        for (Future<String> future : Task) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("线程异常终止");
            }
        }
        //文本转码
        if (config.isMergeFile()) {
            //下载完成合并
            OsUtil.mergeFiles(spider.getConfig().get("title"), config.getPath(), tempPath, spider.getConfig().get("charset"));
            //合并完成转码
            if (!config.getFormat().equals("txt")) {
                EpubUtil.Txt2Epub(config.getPath() + spider.getConfig().get("title"), spider.getConfig().get("title"), " ", config.getFormat());
            }
        }
        overNum.add("下载完成");
    }

    //已经下载数量
    @Override
    public int getOverNum() {
        return overNum.size();
    }

    //停止下载
    @Override
    public void stop() {
        if (service != null) {
            //全部标志为停止
            for (int i = 0; i < isShutdown.size(); i++) {
                isShutdown.set(i, false);
            }
            new File(config.getPath()).delete();
            service.shutdown();
            service.shutdownNow();
        }
    }

    //章节总数量
    @Override
    public int getMaxNum() {
        return list.size();
    }

    //获取爬虫
    public NovelSpider getSpider() {
        return spider;
    }

    //获取下载配置
    public DownloadConfig getConfig() {
        return config;
    }

    @Override
    public String getImgPath() {
        return imgPath;
    }

    @Override
    public String getTitle() {
        return spider.getConfig().get("title");
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getType() {
        return "文本文件";
    }
}
