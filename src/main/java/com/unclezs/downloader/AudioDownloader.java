package com.unclezs.downloader;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.unclezs.crawl.AudioNovelSpider;
import com.unclezs.downloader.config.DownloaderState;
import com.unclezs.gui.utils.ApplicationUtil;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.model.AudioBook;
import com.unclezs.model.AudioChapter;
import com.unclezs.model.rule.SearchAudioRule;
import com.unclezs.utils.RequestUtil;
import com.unclezs.utils.TextUtil;
import com.unclezs.utils.UrlUtil;
import com.unclezs.utils.thead.CountableThreadPool;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 音频下载器
 *
 * @author unclezs.com
 * @date 2020.04.29 20:23
 */
@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class AudioDownloader extends AbstractDownloader implements Serializable {

    private AudioBook book;
    private int threadNum = 0;
    private int delay;
    private String path;
    private transient SearchAudioRule searchAudioRule = null;

    public AudioDownloader(int threadNum, int delay, String path, AudioBook book) {
        this.book = book;
        this.delay = delay;
        this.path = path;
        this.threadNum = threadNum;
        this.fileName = com.unclezs.utils.FileUtil.checkExistAndRename(
            FileUtil.file(path, TextUtil.removeInvalidSymbol(book.getTitle())), true).getName();
    }

    @Override
    public void start(Function<String, String> htmlSpider) {
        if (state == DownloaderState.RUNNING) {
            return;
        }
        state = DownloaderState.RUNNING;
        //获取规则
        for (SearchAudioRule rule : DataManager.application.getAudioRules()) {
            if (book.getUrl().contains(UrlUtil.getSite(rule.getSite()))) {
                searchAudioRule = rule;
                break;
            }
        }
        //没有响应的规则
        if (searchAudioRule == null) {
            state = DownloaderState.FREE;
            return;
        }
        //线程池初始化
        if (pool == null) {
            this.pool = new CountableThreadPool(threadNum);
        }
        //文件位置
        this.saveFile = FileUtil.file(path, fileName);
        //下载封面
        if (UrlUtil.isHttpUrl(book.getCover())) {
            try {
                book.setCover(ApplicationUtil.saveImage(book.getCover(), book.getTitle()));
            } catch (IOException e) {
                log.info("封面下载失败 小说:{} 封面:{}", book.getTitle(), book.getCover());
                book.setCover("");
            }
        }
        List<AudioChapter> chapters = book.getChapters();
        AudioNovelSpider spider = new AudioNovelSpider();
        for (int i = current(); i < chapters.size(); i++) {
            final int cur = i;
            if (state != DownloaderState.RUNNING) {
                return;
            }
            try {
                pool.execute(() -> {
                    AudioChapter chapter = chapters.get(cur);
                    try {
                        String src = spider.getAudioLink(chapter.getUrl());
                        RequestUtil.download(src, getChapterFilePath(chapter, src), searchAudioRule.isClient());
                        ThreadUtil.sleep(delay, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        error.incrementAndGet();
                    }
                    current.incrementAndGet();
                });
            } catch (Exception ignored) {
            }
        }
        pool.waitToOver();
        pool.shutdown();
    }

    @Override
    public void stop() {
        state = DownloaderState.STOP;
        if (pool != null) {
            pool.shutdown();
        }
    }

    @Override
    public void pause() {
        state = DownloaderState.PAUSE;
    }

    @Override
    public int total() {
        return book.getChapters().size();
    }

    @Override
    public String getCover() {
        return book.getCover();
    }

    @Override
    public String getTitle() {
        return book.getTitle();
    }

    @Override
    public String getType() {
        return "audio";
    }

    @Override
    public boolean finished() {
        return this.total() <= current();
    }

    /**
     * 获取下载路径及文件名字
     *
     * @param chapter 章节
     * @param src     真实音频链接
     * @return /
     */
    private String getChapterFilePath(AudioChapter chapter, String src) {
        String suffix = ".mp3";
        if (src.contains("m4a")) {
            suffix = ".m4a";
        }
        return String.format("%s/%s.%s", saveFile.getAbsolutePath(), TextUtil.removeInvalidSymbol(chapter.getTitle()),
            suffix);
    }
}
