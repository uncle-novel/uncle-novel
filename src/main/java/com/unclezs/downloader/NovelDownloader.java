package com.unclezs.downloader;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.unclezs.crawl.TextNovelSpider;
import com.unclezs.downloader.config.DownloadConfig;
import com.unclezs.downloader.config.DownloaderState;
import com.unclezs.model.AnalysisConfig;
import com.unclezs.model.Article;
import com.unclezs.model.Chapter;
import com.unclezs.utils.EpubUtil;
import com.unclezs.utils.RequestUtil;
import com.unclezs.utils.TextUtil;
import com.unclezs.utils.thead.CountableThreadPool;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 文本小说下载器
 *
 * @author uncle
 * @date 2020/4/17
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NovelDownloader extends AbstractDownloader implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 要下载的章节列表
     */
    private List<Chapter> chapters;
    /**
     * 下载配置
     */
    private DownloadConfig setting;
    /**
     * 解析配置
     */
    private transient TextNovelSpider spider;
    /**
     * 缩略图
     */
    private String cover;
    private String title;
    private AnalysisConfig config;
    private transient boolean finished = false;


    public NovelDownloader(List<Chapter> chapters, DownloadConfig setting, String title, AnalysisConfig config) {
        this.chapters = chapters;
        this.setting = setting;
        this.title = TextUtil.removeInvalidSymbol(title);
        this.config = config;
        this.fileName =
            com.unclezs.utils.FileUtil.checkExistAndRename(FileUtil.file(setting.getPath(), title), true).getName();
        this.setStartDynamic(config.getStartDynamic().get());
    }

    /**
     * 下载所有章节(多线程),动态网页单线程借助webview抓取
     *
     * @param contentSpider 如果是动态网页就传入根据webview抓取的方法
     */
    private void download(Function<String, String> contentSpider) {
        if (spider == null) {
            this.spider = new TextNovelSpider(config);
        }
        long startTime = System.currentTimeMillis();
        for (int i = current.get(); i < chapters.size(); i++) {
            final int cur = i;
            if (state != DownloaderState.RUNNING) {
                return;
            }
            try {
                pool.execute(() -> {
                    Chapter chapter = chapters.get(cur);
                    try {
                        String content;
                        if (contentSpider != null) {
                            String html = contentSpider.apply(chapter.getUrl());
                            content = spider.contentByHtml(html);
                        } else {
                            content = spider.content(chapter.getUrl());
                        }
                        //没有获取到正文，则视为失败
                        if (StrUtil.isBlank(content)) {
                            throw new IOException();
                        }
                        content = TextUtil.removeTitle(content, chapter.getName());
                        content = chapter.getName() + "\r\n" + content + "\r\n\r\n";
                        String chapterFileName =
                            setting.isMerge() ? String.valueOf(cur) : TextUtil.removeInvalidSymbol(chapter.getName());
                        FileUtil.writeUtf8String(content, FileUtil.file(saveFile, chapterFileName + ".txt"));
                        ThreadUtil.sleep(setting.getDelay(), TimeUnit.SECONDS);
                    } catch (IOException e) {
                        error.incrementAndGet();
                    }
                    current.incrementAndGet();
                });
            } catch (Exception ignored) {
            }
        }
        //销毁线程池
        pool.waitToOver();
        pool.shutdown();
        //合并文件
        if (setting.isMerge()) {
            merge();
            changeType();
        }
        finished = true;
        log.info("小说【{}】下载完成 耗时【{}s】", getTitle(), (System.currentTimeMillis() - startTime) / 1000L);
    }

    /**
     * 合并文件,将多线程下载的多个文件合并成为一本小说
     */
    private void merge() {
        File file = com.unclezs.utils.FileUtil.checkExistAndRename(
            FileUtil.file(saveFile.getParent(), getTitle().concat(".txt")), true);
        String[] acceptFile = saveFile.list((dir, name) -> name.endsWith(".txt"));
        if (acceptFile != null) {
            //按名字排序
            Arrays.sort(acceptFile, (a, b) -> {
                int aa = Integer.parseInt(a.split("\\.")[0]);
                int bb = Integer.parseInt(b.split("\\.")[0]);
                return aa - bb;
            });
            for (String chapterFileName : acceptFile) {
                File chapterFile = FileUtil.file(saveFile, chapterFileName);
                FileUtil.appendUtf8String(FileUtil.readUtf8String(chapterFile), file);
                FileUtil.del(chapterFile);
            }
        }
        FileUtil.del(this.saveFile);
        this.saveFile = file;
    }


    @Override
    public boolean finished() {
        return finished;
    }

    /**
     * 格式转换 txt -> e book
     */
    private void changeType() {
        try {
            //格式转化
            switch (setting.getType()) {
                case MOBI:
                    toEbook(false);
                    break;
                case EPUB:
                    toEbook(true);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("格式转换失败 小说:{},格式{},原因:{}", getTitle(), getSetting().getType(), e.getMessage());
        }
    }

    /**
     * 转化为e book
     *
     * @param isEpub /
     */
    private void toEbook(boolean isEpub) throws Exception {
        crawlCover();
        File res;
        if (isEpub) {
            res = EpubUtil.toEpub(this.saveFile.getAbsolutePath(), getPath(), new Article(this.cover), true);
        } else {
            res = EpubUtil.toMobi(this.saveFile.getAbsolutePath(), getPath(), new Article(this.cover), true);
        }
        //删除txt
        FileUtil.del(this.saveFile);
        this.saveFile = res;
    }

    /**
     * 抓取封面
     */
    private void crawlCover() {
        try {
            String coverLink = TextNovelSpider.getCover(getTitle());
            this.cover = com.unclezs.utils.FileUtil.getCurrentDir() + "images/" + getTitle() + ".jpeg";
            RequestUtil.download(coverLink, cover, false);
        } catch (Exception e) {
            this.cover = "";
            log.error("封面获取失败 小说:{},原因:{}", getTitle(), e.getMessage());
        }
    }


    @Override
    public void start(Function<String, String> contentSpider) {
        if (state == DownloaderState.RUNNING) {
            return;
        }
        state = DownloaderState.RUNNING;
        this.pool = new CountableThreadPool(startDynamic ? 1 : setting.getThreadNum());
        this.saveFile = FileUtil.file(setting.getPath(), fileName);
        download(contentSpider);
    }

    @Override
    public void pause() {
        state = DownloaderState.PAUSE;
    }

    @Override
    public String processText() {
        if (this.total() <= current()) {
            return "转码中";
        } else {
            return super.processText();
        }
    }

    @Override
    public int total() {
        return chapters.size();
    }

    @Override
    public void stop() {
        state = DownloaderState.STOP;
        if (pool != null) {
            pool.shutdown();
        }
        FileUtil.del(saveFile);
    }


    @Override
    @JSONField(serialize = false)
    public String getPath() {
        return setting.getPath();
    }

    @Override
    public String getType() {
        return setting.getType().toString();
    }
}
