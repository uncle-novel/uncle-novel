package com.unclezs.novel.app.main.core;

import com.unclezs.novel.analyzer.util.regex.RegexUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.main.model.ChapterProperty;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * 章节排序比较器
 *
 * @author blog.unclezs.com
 * @since 2020/12/21 17:54
 */
public class ChapterComparator implements Comparator<ChapterProperty> {

  @Override
  public int compare(ChapterProperty o1, ChapterProperty o2) {
    String one = UrlUtils.getUrlLastPathNotSuffix(o1.getChapter().getUrl());
    String two = UrlUtils.getUrlLastPathNotSuffix(o2.getChapter().getUrl());
    if (RegexUtils.isNumber(one) && RegexUtils.isNumber(two)) {
      BigInteger v1 = new BigInteger(one);
      BigInteger v2 = new BigInteger(two);
      return v1.compareTo(v2);
    }
    return one.compareTo(two);
  }
}
