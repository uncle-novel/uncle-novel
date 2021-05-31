package com.unclezs.novel.app.main.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/4/28 12:54
 */
@Data
@AllArgsConstructor
@DatabaseTable(tableName = "search_engine")
public class SearchEngine implements Serializable {

  public static final String STYLESHEET_PATH = "css/home/views/webview/";
  private static final String BAIDU_SEARCH_URL = "https://www.baidu.com/s?wd=title: (阅读 \"{{keyword}}\" (最新章节) -(官方网站))";
  private static final String GOOGLE_SEARCH_URL = "https://www.google.com.hk/search?q={{keyword}} 小说最新章节";
  private static final String BY_SEARCH_URL = "https://cn.bing.com/search?q={{keyword}} 小说最新章节";
  @DatabaseField(generatedId = true)
  private int id;
  /**
   * 是否启用
   */
  @DatabaseField
  private Boolean enabled;
  /**
   * 搜索引擎名称
   */
  @DatabaseField
  private String name;
  /**
   * 搜索引擎链接
   */
  @DatabaseField
  private String url;
  /**
   * 美化样式
   */
  @DatabaseField
  private String stylesheet;

  public SearchEngine() {
    this.enabled = true;
  }

  public SearchEngine(String name, String url, String stylesheet) {
    this();
    this.name = name;
    this.url = url;
    this.stylesheet = stylesheet;
  }

  /**
   * @return 默认搜索引擎
   */
  public static List<SearchEngine> getDefault() {
    List<SearchEngine> defaults = new ArrayList<>();
    defaults.add(new SearchEngine("百度", BAIDU_SEARCH_URL, STYLESHEET_PATH.concat("baidu.css")));
    defaults.add(new SearchEngine("谷歌", GOOGLE_SEARCH_URL, STYLESHEET_PATH.concat("google.css")));
    defaults.add(new SearchEngine("必应", BY_SEARCH_URL, STYLESHEET_PATH.concat("bing.css")));
    return defaults;
  }

  /**
   * 获取主域名
   *
   * @return xx.com
   */
  public String getDomain() {
    return UrlUtils.getHost(getUrl());
  }
}
