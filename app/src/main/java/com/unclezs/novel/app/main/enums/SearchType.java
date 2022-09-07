package com.unclezs.novel.app.main.enums;

import cn.hutool.core.text.CharSequenceUtil;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.app.framework.support.LocalizedSupport;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @since 2021/4/24 2:48
 */
public enum SearchType implements LocalizedSupport {
  /**
   * 全部
   */
  ALL("novel.all"),
  /**
   * 书名
   */
  NAME("novel.name"),
  /**
   * 作者
   */
  AUTHOR("novel.author"),
  /**
   * 播音
   */
  SPEAKER("novel.speaker");
  @Getter
  private final String desc;

  SearchType(String desc) {
    this.desc = localized(desc);
  }

  public static SearchType fromValue(String desc) {
    for (SearchType searchType : values()) {
      if (searchType.desc.equals(desc)) {
        return searchType;
      }
    }
    throw new IllegalArgumentException("搜索类型值未找到:" + desc);
  }


  /**
   * 关键词是否匹配
   *
   * @param searchType 搜索类型
   * @param keyword    关键词
   * @param novel      小说信息
   * @return true 匹配
   */
  public static boolean match(String searchType, String keyword, Novel novel) {
    return match(fromValue(searchType), keyword, novel);
  }

  /**
   * 关键词是否匹配
   *
   * @param searchType 搜索类型
   * @param keyword    关键词
   * @param novel      小说信息
   * @return true 匹配
   */
  public static boolean match(SearchType searchType, String keyword, Novel novel) {
    switch (searchType) {
      case NAME:
        if (!CharSequenceUtil.containsIgnoreCase(novel.getTitle(), keyword)) {
          return false;
        }
        break;
      case AUTHOR:
        if (CharSequenceUtil.isNotBlank(novel.getAuthor())
          && !novel.getAuthor().contains(keyword) && !CharSequenceUtil.containsIgnoreCase(novel.getTitle(), keyword)) {
          return false;
        }
        break;
      case SPEAKER:
        if (CharSequenceUtil.isNotBlank(novel.getBroadcast()) && !novel.getBroadcast().contains(keyword)
          && !CharSequenceUtil.containsIgnoreCase(novel.getTitle(), keyword)) {
          return false;
        }
        break;
      default:
    }
    return true;
  }
}
