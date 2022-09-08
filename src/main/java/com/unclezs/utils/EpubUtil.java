package com.unclezs.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.unclezs.constrant.Charsets;
import com.unclezs.crawl.LocalNovelLoader;
import com.unclezs.model.Article;
import com.unclezs.model.Chapter;

import java.io.File;
import java.io.InputStream;
import java.util.stream.Collectors;

/**
 * @author uncle
 * @date 2020/4/29 16:32
 */
public class EpubUtil {
    private static final String OPF = "epub/opf.ftl";
    private static final String NCX = "epub/ncx.ftl";
    private static final String CATALOG = "epub/catalog.ftl";
    private static final String CONTENT = "epub/content.ftl";
    private static final String CSS = "h2,h3,h4 { text-align: center; }";
    private static final String CONTAINER = "META-INF/container.xml";
    private static final String MINE_TYPE = "application/epub+zip";
    private static final String OPF_FILENAME = "opf.opf";
    private static final String NCX_FILENAME = "ncx.ncx";
    private static final String CATALOG_FILENAME = "catalog.htm";
    private static final String CMD = "./lib/kindlegen " + OPF_FILENAME + " -c1 -o out.mobi";

    /**
     * txt转成e book
     * https://www.jianshu.com/p/d2edab6750df
     *
     * @param path     txt文件路径
     * @param savePath 保存的位置
     * @param article  文章信息 需要封面、作者  封面必须为本地文件
     */
    private static void toeBook(String path, String savePath, Article article) {
        //解析txt得到小说信息
        LocalNovelLoader loader = new LocalNovelLoader();
        loader.load(path);
        article.setTitle(loader.getTitle());
        article.setChapters(loader.chapters().stream().map(Chapter::getName).collect(Collectors.toList()));
        savePath = com.unclezs.utils.FileUtil.getPath(savePath);
        //创建基本的文件
        FileUtil.writeUtf8String(
            FileUtil.readString(EpubUtil.class.getResource("/templates/epub/container.xml"), Charsets.UTF8),
            savePath + CONTAINER);
        FileUtil.writeUtf8String(CSS, savePath.concat("style.css"));
        FileUtil.writeUtf8String(MINE_TYPE, savePath.concat("minetype"));
        //拷贝封面
        if (StrUtil.isNotBlank(article.getCover())) {
            FileUtil.copy(article.getCover(), savePath.concat("cover.jpeg"), true);
        }
        //创建NCX
        TemplateUtil.process(Dict.create().set("article", article), NCX, FileUtil.file(savePath.concat(NCX_FILENAME)));
        //创建OPF
        TemplateUtil.process(Dict.create().set("article", article), OPF, FileUtil.file(savePath.concat(OPF_FILENAME)));
        //创建catalog.html
        TemplateUtil.process(Dict.create().set("article", article), CATALOG,
            FileUtil.file(savePath.concat(CATALOG_FILENAME)));
        for (int i = 0; i < article.getChapters().size(); i++) {
            String content = loader.getContent(i).replace("\r\n", "<br/>");
            String name = article.getChapters().get(i);
            TemplateUtil.process(Dict.create().set("content", content).set("name", name), CONTENT,
                FileUtil.file(String.format("%shtml/%d.html", savePath, i)));
        }
    }

    /**
     * 转成mobi
     *
     * @param rename 文件存在是否需要重新命名
     */
    public static File toMobi(String path, String savePath, Article article, boolean rename) throws Exception {
        String tmpdir = savePath + RandomUtil.randomString(5);
        toeBook(path, tmpdir, article);
        Process exec = Runtime.getRuntime().exec(CMD, null, FileUtil.file(tmpdir));
        InputStream stream = exec.getInputStream();
        while (stream.read() != -1) ;
        stream.close();
        exec.waitFor();
        File tmpFile = FileUtil.file(tmpdir, "out.mobi");
        File targetFile =
            com.unclezs.utils.FileUtil.checkExistAndRename(savePath + article.getTitle().concat(".mobi"), rename);
        FileUtil.move(tmpFile, targetFile, true);
        FileUtil.del(tmpdir);
        return targetFile;
    }

    /**
     * 转成epub
     *
     * @param rename 文件存在是否需要重新命名
     */
    public static File toEpub(String path, String savePath, Article article, boolean rename) {
        String tmpdir = savePath + RandomUtil.randomString(5);
        toeBook(path, tmpdir, article);
        File zip =
            com.unclezs.utils.FileUtil.checkExistAndRename(savePath + article.getTitle().concat(".epub"), rename);
        zip = ZipUtil.zip(tmpdir, zip.getAbsolutePath());
        FileUtil.del(tmpdir);
        return zip;
    }


    public static void main(String[] args) throws Exception {
        toMobi("D:\\java\\NovelHarvester\\完美世界.txt", "D:\\java\\NovelHarvester\\tmp\\1.txt", new Article(""),
            false);
    }
}
