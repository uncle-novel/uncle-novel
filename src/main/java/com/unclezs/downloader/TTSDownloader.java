package com.unclezs.downloader;

import cn.hutool.core.io.FileUtil;
import com.unclezs.crawl.LocalNovelLoader;
import com.unclezs.crawl.NovelLoader;
import com.unclezs.crawl.WebNovelLoader;
import com.unclezs.downloader.config.DownloaderState;
import com.unclezs.model.Book;
import com.unclezs.model.Chapter;
import com.unclezs.utils.MSTTSSpeech;
import com.unclezs.utils.TextUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * TTS 文本合成语音任务
 *
 * @author uncle
 * @date 2020/5/17 22:43
 */
@Slf4j
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TTSDownloader extends AbstractDownloader {
    private Book book;
    private String path;
    private transient NovelLoader loader;
    private int total = 1;
    private transient MSTTSSpeech speech = new MSTTSSpeech();

    public TTSDownloader(Book book, String path) {
        this.book = book;
        this.path = path;
        this.fileName = com.unclezs.utils.FileUtil.checkExistAndRename(
            FileUtil.file(path, TextUtil.removeInvalidSymbol(book.getName())), true).getName();
    }

    @Override
    public void start(Function<String, String> contentSpider) {
        state = DownloaderState.RUNNING;
        if (book.isWeb()) {
            loader = new WebNovelLoader();
        } else {
            loader = new LocalNovelLoader();
        }
        loader.load(book);
        total = loader.chapters().size();
        for (int i = current(); i < loader.chapters().size(); i++) {
            if (state != DownloaderState.RUNNING) {
                break;
            }
            try {
                Chapter chapter = loader.chapters().get(i);
                String content = chapter.getName() + loader.content(i);
                String s =
                    String.format("%s%s/%s.mp3", path, fileName, TextUtil.removeInvalidSymbol(chapter.getName()));
                FileUtil.touch(s);
                speech.saveToWav(content, s);
            } catch (Exception e) {
                error.incrementAndGet();
                log.error("文本合成语音失败了:{}", e.getMessage());
            }
            current.incrementAndGet();
        }
        //合成同时也缓存了
        if (loader instanceof WebNovelLoader) {
            loader.store();
        }
    }

    @Override
    public boolean finished() {
        return current() >= total();
    }

    @Override
    public void stop() {
        state = DownloaderState.STOP;
    }

    @Override
    public void pause() {
        state = DownloaderState.PAUSE;
    }

    @Override
    public int total() {
        return total;
    }

    @Override
    public String getCover() {
        return book.getCover();
    }

    @Override
    public String getTitle() {
        return book.getName();
    }

    @Override
    public String getType() {
        return "audio";
    }

}
