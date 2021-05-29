package com.unclezs.novel.app.main.ui.home.views.widgets;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.components.LoadingImageView;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.Tag;
import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.support.LocalizedSupport;
import com.unclezs.novel.app.framework.util.Choosers;
import com.unclezs.novel.app.framework.util.DesktopUtils;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.util.BookHelper;
import java.io.File;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

/**
 * 书籍详情模态框
 *
 * @author blog.unclezs.com
 * @date 2021/4/24 17:05
 */
public class BookDetailModal extends VBox implements LocalizedSupport {


  public static final String DEFAULT_STYLE_CLASS = "book-detail";
  private final boolean editable;
  private final Novel novel;
  /**
   * 是否为有声
   */
  private final boolean audio;
  @Getter
  private IconButton bookshelf;
  @Getter
  private IconButton analysis;
  @Getter
  private IconButton download;
  @Getter
  private IconButton toc;
  @Setter
  @Getter
  private ModalBox containerModal;

  public BookDetailModal(Novel novel, boolean audio, boolean editable) {
    this.audio = audio;
    this.novel = novel;
    this.editable = editable;
    NodeHelper.addClass(this, DEFAULT_STYLE_CLASS);
    // 封面
    LoadingImageView cover = new LoadingImageView(BookListCell.NO_COVER, 100, 140);
    cover.setImage(novel.getCoverUrl());
    if (editable) {
      cover.setOnMouseClicked(e -> {
        File file = Choosers.chooseImage("小说封面");
        if (file != null) {
          novel.setCoverUrl(file.getAbsolutePath());
          cover.setImage(novel.getCoverUrl());
        }
      });
    }
    // 信息
    HBox title = createItem(null, novel.getTitle(), Type.EDITABLE, novel::setTitle, "title");
    HBox author = createItem(localized("novel.author"), novel.getAuthor(), Type.EDITABLE, novel::setAuthor);
    HBox site = createItem("来源", novel.getUrl(), Type.LINK);
    HBox wordCount = createItem("字数", novel.getWordCount());
    HBox category = createItem("分类", novel.getCategory(), Type.EDITABLE, novel::setCategory);
    HBox state = createItem("状态", novel.getState(), Type.TAG);
    HBox updateTime = createItem("更新时间", novel.getUpdateTime(), Type.EDITABLE, novel::setUpdateTime);
    HBox latestChapter = createItem(localized("novel.chapter.latest"), novel.getLatestChapterName(), Type.EDITABLE, novel::setLatestChapterName);
    HBox desc = createItem(null, CharSequenceUtil.blankToDefault(novel.getIntroduce(), "暂无简介"), "desc");
    VBox detailContainer = new VBox(title, author);
    // 播音
    if (StringUtils.isNotBlank(novel.getBroadcast())) {
      HBox broadcast = createItem(localized("novel.speaker"), novel.getBroadcast(), Type.EDITABLE, novel::setBroadcast);
      detailContainer.getChildren().add(broadcast);
    }
    detailContainer.getChildren().addAll(site, latestChapter, wordCount, category, state, updateTime);
    NodeHelper.addClass(detailContainer, "info");
    // 有声小说不显示
    if (audio) {
      detailContainer.getChildren().remove(wordCount);
    }

    // 容器
    HBox container = new HBox(detailContainer, cover);
    getChildren().addAll(container, desc);
  }

  public BookDetailModal(Novel novel) {
    this(novel, false);
  }

  public BookDetailModal(Novel novel, boolean audio) {
    this(novel, audio, false);
  }

  public void show() {
    ModalBox detailModal = ModalBox.none().body(this).title("小说详情").cancel("关闭");
    this.setContainerModal(detailModal);
    detailModal.show();
  }

  /**
   * 添加操作按钮
   *
   * @param actions 按钮
   * @return this
   */
  public BookDetailModal withActions(Action... actions) {
    HBox actionsBox = NodeHelper.addClass(new HBox(), "actions");
    for (Action action : actions) {
      switch (action) {
        case ANALYSIS:
          analysis = NodeHelper.addClass(new IconButton("解析下载"), "btn");
          analysis.setOnMouseClicked(e -> {
            containerModal.disabledAnimateClose().close();
            BookHelper.submitAnalysis(novel);
          });
          actionsBox.getChildren().add(analysis);
          break;
        case BOOKSHELF:
          bookshelf = NodeHelper.addClass(new IconButton("加入书架"), "btn");
          bookshelf.setOnMouseClicked(e -> BookHelper.addBookShelf(audio, novel, null, () -> containerModal.disabledAnimateClose().close()));
          actionsBox.getChildren().add(bookshelf);
          break;
        case TOC:
          toc = NodeHelper.addClass(new IconButton("查看目录"), "btn");
          actionsBox.getChildren().add(toc);
          break;
        case DOWNLOAD:
          download = NodeHelper.addClass(new IconButton("直接下载"), "btn");
          download.setOnMouseClicked(e -> {
            containerModal.disabledAnimateClose().close();
            BookHelper.submitDownload(novel, null, null);
          });
          actionsBox.getChildren().add(download);
          break;
        default:
      }
    }
    getChildren().add(actionsBox);
    return this;
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
    return createItem(labelText, contentText, Type.LABEL, className);
  }

  /**
   * 创建信息列
   *
   * @param labelText   标签
   * @param contentText 内容
   * @param className   css类名
   * @return 信息列
   */
  private HBox createItem(String labelText, String contentText, Type type, String... className) {
    return createItem(labelText, contentText, type, null, className);
  }

  /**
   * 创建信息列
   *
   * @param labelText   标签
   * @param contentText 内容
   * @param type        盒子类型
   * @param className   css类名
   * @return 信息列
   */
  private HBox createItem(String labelText, String contentText, Type type, Consumer<String> setter, String... className) {
    HBox box = NodeHelper.addClass(new HBox(), "item");
    if (labelText != null) {
      Label label = NodeHelper.addClass(new Label(labelText.concat(StrUtil.COLON)), "item-label");
      box.getChildren().add(label);
    }
    if (StringUtils.isNotBlank(contentText) || type == Type.EDITABLE) {
      Node content;
      switch (type) {
        case TAG:
          content = new Tag(contentText);
          break;
        case LINK:
          Hyperlink hyperlink = new Hyperlink(contentText);
          if (UrlUtils.isHttpUrl(contentText)) {
            hyperlink.setOnAction(e -> DesktopUtils.openBrowse(contentText));
          }
          content = hyperlink;
          break;
        case EDITABLE:
          content = createEditorBox(contentText, setter);
          break;
        case LABEL:
        default:
          content = new Label(contentText);
      }
      box.getChildren().add(NodeHelper.addClass(content, "item-content"));
    }
    if (className.length > 0) {
      NodeHelper.addClass(box, className);
    }
    return box;
  }

  /**
   * 创建可以编辑的节点
   *
   * @param text 初始文字
   * @return 节点
   */
  private Node createEditorBox(String text, Consumer<String> setter) {
    if (!editable) {
      return new Label(text);
    }
    // 显示label
    Label label = new Label(text);
    Icon icon = new Icon(IconFont.EDIT);
    HBox titleBox = NodeHelper.addClass(new HBox(label, icon), "editable-box");
    // 编辑框
    TextField editor = new TextField(text);
    editor.setPromptText("请输入要修改的内容");
    StackPane panel = new StackPane(titleBox);
    // 事件绑定
    EventUtils.setOnMousePrimaryClick(icon, e -> panel.getChildren().setAll(editor));
    editor.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        label.setText(editor.getText());
        if (setter != null) {
          setter.accept(editor.getText());
        }
        panel.getChildren().setAll(titleBox);
      }
    });
    return panel;
  }

  /**
   * 创建盒子的类型
   */
  private enum Type {
    /**
     * 盒子类型
     */
    TAG,
    EDITABLE,
    LINK,
    LABEL
  }

  /**
   * 操作按钮类型
   */
  public enum Action {
    /**
     * 书架
     */
    BOOKSHELF,
    /**
     * 解析下载
     */
    ANALYSIS,
    /**
     * 目录
     */
    TOC,
    /**
     * 直接下载
     */
    DOWNLOAD
  }
}
