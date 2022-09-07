package com.unclezs.novel.app.main.core.loader;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileMode;
import cn.hutool.core.lang.PatternPool;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.app.main.db.beans.Book;
import com.unclezs.novel.app.main.db.beans.TxtTocRule;
import com.unclezs.novel.app.main.util.EncodingDetect;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author blog.unclezs.com
 * @since 2021/5/7 14:14
 */
@Slf4j
public class TxtLoader extends AbstractBookLoader {

  /**
   * 默认从文件中获取数据的长度
   */
  private static final int BUFFER_SIZE = 512 * 1024;
  /**
   * 没有标题的时候，每个章节的最大长度
   */
  private static final int MAX_LENGTH_WITH_NO_CHAPTER = 10 * 1024;
  private File bookFile;
  private String charset;

  /**
   * 1. 检查文件中是否存在章节名
   * <p>
   * 2. 判断文件中使用的章节名类型的正则表达式
   *
   * @return 规则列表的索引 -1则不存在
   */
  public static int checkChapterType(File file, List<TxtTocRule> rules) {
    try (RandomAccessFile ras = FileUtil.createRandomAccessFile(file, FileMode.r)) {
      // 首先获取128k的数据
      byte[] buffer = new byte[BUFFER_SIZE / 4];
      int length = ras.read(buffer, 0, buffer.length);
      // 进行章节匹配
      int index = 0;
      for (TxtTocRule rule : rules) {
        Pattern pattern = PatternPool.get(rule.getRule(), Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(new String(buffer, 0, length, EncodingDetect.getEncode(file)));
        // 如果匹配存在，那么就表示当前章节使用这种匹配方式
        if (matcher.find()) {
          // 重置指针位置
          ras.seek(0);
          return index;
        }
        index++;
      }
    } catch (Exception e) {
      log.error("解析TXT目录失败", e);
    }
    return -1;
  }

  @Override
  public void setBook(Book book) {
    super.setBook(book);
    this.bookFile = new File(getBook().getUrl());
    charset = this.getBook().getCharset();
    if (charset == null) {
      this.charset = EncodingDetect.getEncode(bookFile);
    }
  }

  @Override
  public List<Chapter> toc() {
    if (CollectionUtils.isEmpty(toc)) {
      try {
        toc = loadChapters(PatternPool.get(book.getTxtTocRule(), Pattern.MULTILINE));
      } catch (IOException e) {
        log.error("本地小说章节解析失败：{}", bookFile, e);
        e.printStackTrace();
      }
    }
    return toc;
  }

  /**
   * 加载正文
   *
   * @param index 章节索引
   * @return 正文
   */
  @Override
  public String loadContent(int index) {
    Chapter chapter = toc().get(index);
    try (RandomAccessFile ras = FileUtil.createRandomAccessFile(bookFile, FileMode.r)) {
      ras.seek(chapter.getFrom());
      byte[] bytes = new byte[(int) (chapter.getTo() - chapter.getFrom())];
      ras.read(bytes);
      String content = new String(bytes, charset);
      return content.trim();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
    return null;
  }

  /**
   * 是否已经被缓存
   *
   * @param index 索引
   * @return txt本地默认全部被缓存
   */
  @Override
  public boolean isCached(int index) {
    return true;
  }

  /**
   * 1. 检查文件中是否存在章节名 2. 判断文件中使用的章节名类型的正则表达式
   *
   * @return 是否存在章节名
   */
  public boolean checkHasChapter(RandomAccessFile bookStream, Pattern pattern) throws IOException {
    // 首先获取128k的数据
    byte[] buffer = new byte[BUFFER_SIZE / 4];
    int length = bookStream.read(buffer, 0, buffer.length);
    // 进行章节匹配
    Matcher matcher = pattern.matcher(new String(buffer, 0, length, charset));
    // 如果匹配存在，那么就表示当前章节使用这种匹配方式
    if (matcher.find()) {
      // 重置指针位置
      bookStream.seek(0);
      return true;
    }
    // 重置指针位置
    bookStream.seek(0);
    return false;
  }


  /**
   * todo
   * <p>
   * 1. 序章的添加
   * <p>
   * 2. 章节存在的书本的虚拟分章效果
   */
  public List<Chapter> loadChapters(Pattern pattern) throws IOException {
    List<Chapter> toc = new ArrayList<>();
    // 获取文件流
    RandomAccessFile bookStream = FileUtil.createRandomAccessFile(bookFile, FileMode.r);
    // 寻找匹配文章标题的正则表达式，判断是否存在章节名
    boolean hasChapter = checkHasChapter(bookStream, pattern);
    // 加载章节
    byte[] buffer = new byte[BUFFER_SIZE];
    // 获取到的块起始点，在文件中的位置
    long curOffset = 0;
    // block的个数
    int blockPos = 0;
    // 读取的长度
    int length;
    int allLength = 0;

    // 获取文件中的数据到buffer，直到没有数据为止
    while ((length = bookStream.read(buffer, 0, buffer.length)) > 0) {
      ++blockPos;
      // 如果存在Chapter
      if (hasChapter) {
        // 将数据转换成String
        String blockContent = new String(buffer, 0, length, charset);
        int lastN = blockContent.lastIndexOf("\n");
        if (lastN != 0) {
          blockContent = blockContent.substring(0, lastN);
          length = blockContent.getBytes(charset).length;
          allLength = allLength + length;
          bookStream.seek(allLength);
        }
        // 当前Block下使过的String的指针
        int seekPos = 0;
        // 进行正则匹配
        Matcher matcher = pattern.matcher(blockContent);
        // 如果存在相应章节
        while (matcher.find()) {
          // 获取匹配到的字符在字符串中的起始位置
          int chapterStart = matcher.start();
          // 如果 seekPos == 0 && nextChapterPos != 0 表示当前block处前面有一段内容
          // 第一种情况一定是序章 第二种情况可能是上一个章节的内容
          if (seekPos == 0 && chapterStart != 0) {
            // 获取当前章节的内容
            String chapterContent = blockContent.substring(seekPos, chapterStart);
            // 设置指针偏移
            seekPos += chapterContent.length();
            // 如果当前没有章节，那么就是序章
            if (toc.isEmpty()) {
              // 创建当前章节
              Chapter chapter = new Chapter();
              chapter.setName(matcher.group());
              chapter.setFrom((long) chapterContent.getBytes(charset).length);
              toc.add(chapter);
            } else {  // 否则就block分割之后，上一个章节的剩余内容
              // 获取上一章节
              Chapter lastChapter = toc.get(toc.size() - 1);
              // 将当前段落添加上一章去
              lastChapter.setTo(lastChapter.getTo() + chapterContent.getBytes(charset).length);
              lastChapter.setFrom(lastChapter.getFrom() + lastChapter.getName().getBytes(charset).length);
              // 创建当前章节
              Chapter curChapter = new Chapter();
              curChapter.setName(matcher.group());
              curChapter.setFrom(lastChapter.getTo());
              toc.add(curChapter);
            }
          } else {
            // 是否存在章节
            if (!toc.isEmpty()) {
              // 获取章节内容
              String chapterContent = blockContent.substring(seekPos, matcher.start());
              seekPos += chapterContent.length();
              // 获取上一章节
              Chapter lastChapter = toc.get(toc.size() - 1);
              lastChapter.setTo(lastChapter.getFrom() + chapterContent.getBytes(charset).length);
              lastChapter.setFrom(lastChapter.getFrom() + lastChapter.getName().getBytes(charset).length);
              // 创建当前章节
              Chapter curChapter = new Chapter();
              curChapter.setName(matcher.group());
              curChapter.setFrom(lastChapter.getTo());
              toc.add(curChapter);
            } else { //如果章节不存在则创建章节
              Chapter curChapter = new Chapter();
              curChapter.setName(matcher.group());
              curChapter.setFrom(0L);
              curChapter.setTo(0L);
              toc.add(curChapter);
            }
          }
        }
      } else {
        //进行本地虚拟分章
        //章节在buffer的偏移量
        int chapterOffset = 0;
        //当前剩余可分配的长度
        int strLength = length;
        //分章的位置
        int chapterPos = 0;

        while (strLength > 0) {
          ++chapterPos;
          //是否长度超过一章
          if (strLength > MAX_LENGTH_WITH_NO_CHAPTER) {
            //在buffer中一章的终止点
            int end = length;
            //寻找换行符作为终止点
            for (int i = chapterOffset + MAX_LENGTH_WITH_NO_CHAPTER; i < length; ++i) {
              if (buffer[i] == 0x0a) {
                end = i;
                break;
              }
            }
            Chapter chapter = new Chapter();
            chapter.setName("第" + blockPos + "章" + "(" + chapterPos + ")");
            chapter.setFrom(curOffset + chapterOffset + 1);
            chapter.setTo(curOffset + end);
            toc.add(chapter);
            //减去已经被分配的长度
            strLength = strLength - (end - chapterOffset);
            //设置偏移的位置
            chapterOffset = end;
          } else {
            Chapter chapter = new Chapter();
            chapter.setName("第" + blockPos + "章" + "(" + chapterPos + ")");
            chapter.setFrom(curOffset + chapterOffset + 1);
            chapter.setTo(curOffset + length);
            toc.add(chapter);
            strLength = 0;
          }
        }
      }
      //block的偏移点
      curOffset += length;
      if (hasChapter) {
        //设置上一章的结尾
        Chapter lastChapter = toc.get(toc.size() - 1);
        lastChapter.setTo(curOffset);
      }
      // 当添加的block太多的时候，执行GC
      if (blockPos % 15 == 0) {
        System.gc();
        System.runFinalization();
      }
    }
    for (int i = 0; i < toc.size(); i++) {
      Chapter bean = toc.get(i);
      bean.setOrder(i);
    }
    IoUtil.close(bookStream);
    System.gc();
    System.runFinalization();
    return toc;
  }
}
