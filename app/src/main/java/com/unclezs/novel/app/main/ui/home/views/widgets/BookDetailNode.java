package com.unclezs.novel.app.main.ui.home.views.widgets;

import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.components.LoadingImageView;
import com.unclezs.novel.app.framework.components.Tag;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.support.LocalizedSupport;
import com.unclezs.novel.app.framework.util.DesktopUtils;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 书籍详情节点
 *
 * @author blog.unclezs.com
 * @date 2021/4/24 17:05
 */
public class BookDetailNode extends VBox implements LocalizedSupport {


  public static final String DEFAULT_STYLE_CLASS = "book-detail";

  public BookDetailNode(Novel novel) {
    NodeHelper.addClass(this, DEFAULT_STYLE_CLASS);
    // 封面
    LoadingImageView cover = new LoadingImageView(BookListCell.NO_COVER, 100, 140);
    cover.setImage(novel.getCoverUrl());
    // 信息
    HBox title = createItem(null, novel.getTitle(), "title");
    HBox author = createItem(localized("novel.author"), novel.getAuthor());
    HBox site = createItem("来源", novel.getUrl(), false, true);
    HBox wordCount = createItem("字数", novel.getWordCount());
    HBox category = createItem("分类", novel.getCategory());
    HBox state = createItem("状态", novel.getState(), true, false);
    HBox updateTime = createItem("更新时间", novel.getUpdateTime());
    HBox latestChapter = createItem(localized("novel.chapter.latest"), novel.getLatestChapterName());
    HBox desc = createItem(null, novel.getIntroduce(), "desc");
    VBox detailContainer = new VBox(title, author, site, latestChapter, wordCount, category, state, updateTime);
    NodeHelper.addClass(detailContainer, "info");
    // 操作按钮
    IconButton bookshelf = NodeHelper.addClass(new IconButton("加入书架"), "btn");
    IconButton analysis = NodeHelper.addClass(new IconButton("解析下载"), "btn");
    HBox actions = NodeHelper.addClass(new HBox(bookshelf, analysis), "actions");
    // 容器
    HBox container = new HBox(detailContainer, cover);
    getChildren().addAll(container, desc, actions);
  }

  /**
   * 创建信息列
   *
   * @param labelText   标签
   * @param contentText 内容
   * @param className   css类名
   * @return 信息列
   */
  private HBox createItem(String labelText, String contentText, String... className) {
    return createItem(labelText, contentText, false, false, className);
  }

  /**
   * 创建信息列
   *
   * @param labelText   标签
   * @param contentText 内容
   * @param tag         是否为标签
   * @param link        是否为链接
   * @param className   css类名
   * @return 信息列
   */
  private HBox createItem(String labelText, String contentText, boolean tag, boolean link, String... className) {
    HBox box = NodeHelper.addClass(new HBox(), "item");
    if (labelText != null) {
      Label label = NodeHelper.addClass(new Label(labelText.concat(StrUtil.COLON)), "item-label");
      box.getChildren().add(label);
    }
    Node content;
    if (tag) {
      content = new Tag(contentText);
    } else if (link) {
      Hyperlink hyperlink = new Hyperlink(contentText);
      if (UrlUtils.isHttpUrl(contentText)) {
        hyperlink.setOnAction(e -> DesktopUtils.openBrowse(contentText));
      }
      content = hyperlink;
    } else {
      content = new Label(contentText);
    }
    box.getChildren().add(NodeHelper.addClass(content, "item-content"));
    if (className.length > 0) {
      NodeHelper.addClass(box, className);
    }
    return box;
  }
}
