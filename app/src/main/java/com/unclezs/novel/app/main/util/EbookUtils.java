package com.unclezs.novel.app.main.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.ZipUtil;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.SystemUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.manager.ResourceManager;
import java.io.File;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Ebook生成工具
 *
 * @author blog.unclezs.com
 * @date 2021/5/1 10:25
 */
@Slf4j
@UtilityClass
public class EbookUtils {

  public static final String EBOOK_TMP_SUFFIX = "_Ebook";
  private static final String KINDLEGEN = "kindlegen";
  private static final String KINDLEGEN_EXE = "kindlegen.exe";
  private static final String KINDLEGEN_LINUX = "kindlegen_linux";
  private static final String EPUB_SUFFIX = ".epub";
  private static final String MOBI_SUFFIX = ".mobi";
  private static final String MIMETYPE = "mimetype";
  private static final String EPUB_TEMPLATES = "templates/epub/";
  private static final String OUT_PATH_STYLE_CSS = "style/style.css";
  private static final String OUT_PATH_CONTAINER_XML = "META-INF/container.xml";
  private static final String OUT_PATH_COVER = "cover.jpeg";
  private static final String OUT_PATH_CONTENT_OPF = "content.opf";
  private static final String OUT_PATH_TOC_NCX = "toc.ncx";
  private static final String OUT_PATH_TOC_HTML = "text/toc.html";
  /**
   * EPUB模板文件
   */
  private static final String TEMPLATE_CHAPTER = EPUB_TEMPLATES.concat("text/chapter.html.vm");
  private static final String TEMPLATE_TOC_HTML = EPUB_TEMPLATES.concat("text/toc.html.vm");
  private static final String TEMPLATE_CONTENT_OPF = EPUB_TEMPLATES.concat("content.opf.vm");
  private static final String TEMPLATE_TOC_NCX = EPUB_TEMPLATES.concat("toc.ncx.vm");
  private static final String TEMPLATE_MIMETYPE = EPUB_TEMPLATES.concat(MIMETYPE);
  private static final String TEMPLATE_STYLESHEET = EPUB_TEMPLATES.concat(OUT_PATH_STYLE_CSS);
  private static final String TEMPLATE_CONTAINER_XML = EPUB_TEMPLATES.concat(OUT_PATH_CONTAINER_XML);

  /**
   * 生成epub
   *
   * @param outDir 输出目录(带小说名)
   */
  public static void toEpub(File outDir) {
    File tmpDir = FileUtil.file(outDir.getAbsolutePath().concat(EBOOK_TMP_SUFFIX));
    // 生成epub文件
    ZipUtil.zip(tmpDir.getAbsolutePath(), outDir.getAbsolutePath().concat(EPUB_SUFFIX));
  }

  /**
   * 生成mobi
   *
   * @param outDir 输出文件位置
   */
  @SuppressWarnings("all")
  public static void toMobi(File outDir) {
    File tmpDir = FileUtil.file(outDir.getAbsolutePath().concat(EBOOK_TMP_SUFFIX));
    // kindlegen可执行文件
    String gen = null;
    if (SystemUtils.isWindows()) {
      gen = ResourceManager.binFile(KINDLEGEN_EXE).getAbsolutePath();
    } else {
      gen = SystemUtils.isLinux() ? ResourceManager.binFile(KINDLEGEN_LINUX).getAbsolutePath() : ResourceManager.binFile(KINDLEGEN).getAbsolutePath();
    }
    FileUtil.file(gen).setExecutable(true, false);
    // content.opf文件
    String opf = FileUtil.file(tmpDir, OUT_PATH_CONTENT_OPF).getAbsolutePath();
    // cmd命令
    String filename = outDir.getName().concat(MOBI_SUFFIX);
    try {
      // kindlegen的输出目录与opf输入目录一样
      String command = String.format("%s %s -c1 -o %s", gen, opf, filename);
      Process process = Runtime.getRuntime().exec(command);
      // 读取输出流等待执行完成
      String result = RuntimeUtil.getResult(process);
      process.waitFor();
      // 移动文件到输出目录
      File mobiFile = FileUtil.file(tmpDir, filename);
      if (mobiFile.exists()) {
        FileUtil.move(FileUtil.file(tmpDir, filename), FileUtil.file(outDir.getParent(), filename), true);
      } else {
        log.warn("mobi文件不存在");
      }
      log.trace("生成mobi时kindlegen输出：{}", result);
    } catch (IOException e) {
      log.error("生成mobi文件失败：{}", outDir, e);
    } catch (InterruptedException e) {
      log.error("生成mobi时文件被中断：{}", outDir, e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * 生成ebook目录结构及文件
   *
   * @param novel           小说
   * @param outDir          输出目录
   * @param generateChapter 是否生成章节
   * @return 生成的文件夹位置
   */
  public static File toEbook(Novel novel, File outDir, boolean generateChapter) {
    File tmpDir = FileUtil.file(outDir.getAbsolutePath().concat(EBOOK_TMP_SUFFIX));
    // META-INF/container.xml
    FileUtil.writeFromStream(ResourceUtils.stream(TEMPLATE_CONTAINER_XML), FileUtil.file(tmpDir, OUT_PATH_CONTAINER_XML));
    // mimetype
    FileUtil.writeFromStream(ResourceUtils.stream(TEMPLATE_MIMETYPE), FileUtil.file(tmpDir, MIMETYPE));
    // stylesheet
    FileUtil.writeFromStream(ResourceUtils.stream(TEMPLATE_STYLESHEET), FileUtil.file(tmpDir, OUT_PATH_STYLE_CSS));
    // 封面
    generateCover(novel.getCoverUrl(), FileUtil.file(tmpDir, OUT_PATH_COVER));
    // content.opf
    VelocityUtils.render(TEMPLATE_CONTENT_OPF, novel, FileUtil.file(tmpDir, OUT_PATH_CONTENT_OPF));
    // toc.ncx
    VelocityUtils.render(TEMPLATE_TOC_NCX, novel, FileUtil.file(tmpDir, OUT_PATH_TOC_NCX));
    // 目录
    VelocityUtils.render(TEMPLATE_TOC_HTML, novel, FileUtil.file(tmpDir, OUT_PATH_TOC_HTML));
    // 生成章节
    if (generateChapter && CollectionUtils.isNotEmpty(novel.getChapters())) {
      novel.getChapters().forEach(chapter -> generateChapter(chapter, outDir));
    }
    return tmpDir;
  }

  /**
   * 生成章节
   *
   * @param chapter 章节
   * @param outDir  输出目录
   */
  public static void generateChapter(Chapter chapter, File outDir) {
    File tmpDir = FileUtil.file(outDir.getAbsolutePath().concat(EBOOK_TMP_SUFFIX));
    VelocityUtils.render(TEMPLATE_CHAPTER, chapter, FileUtil.file(tmpDir, String.format("text/%d.html", chapter.getOrder())));
  }

  /**
   * 生成封面
   *
   * @param coverUrl 封面链接
   * @param out      输出文件
   */
  private static void generateCover(String coverUrl, File out) {
    if (StringUtils.isNotBlank(coverUrl)) {
      try {
        byte[] cover;
        if (UrlUtils.isHttpUrl(coverUrl)) {
          // 远程图片
          cover = Http.bytes(RequestParams.create(coverUrl));
        } else {
          // 本地图片
          cover = FileUtil.readBytes(FileUtil.file(coverUrl));
        }
        FileUtil.writeBytes(cover, out);
      } catch (Exception e) {
        log.error("epub封面生成失败：封面地址：{}", coverUrl, e);
      }
    }
  }
}
