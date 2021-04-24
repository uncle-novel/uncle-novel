package com.unclezs.novel.app.main.enums;

import com.unclezs.novel.app.framework.support.LocalizedSupport;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/4/24 2:48
 */
public enum SearchType implements LocalizedSupport {
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
}
