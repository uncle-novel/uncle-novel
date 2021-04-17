package com.unclezs.novel.app.main.home.views.widgets;

import cn.hutool.core.text.CharSequenceUtil;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.components.ImageViewPlus;
import com.unclezs.novel.app.framework.components.Tag;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import java.util.Objects;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 搜索小说结果
 *
 * @author blog.unclezs.com
 * @date 2021/4/16 22:39
 */
public class BookListCell extends ListCell<Novel> {

  public static final Image NO_COVER = new Image(ResourceUtils.externalForm("assets/images/no-cover.png"), true);
  private final HBox cell = NodeHelper.addClass(new HBox(), "cell");
  private final ImageView cover;
  private final Label title = NodeHelper.addClass(new Label(), "title");
  private final Label author = new Label();
  private final Label desc = new Label();
  private final Label latestChapter = new Label();
  private final HBox tags = NodeHelper.addClass(new HBox(), "tags");
  private Novel novel;

  public BookListCell(ListView<Novel> listView) {
    this.cover = new ImageViewPlus(NO_COVER);
    VBox info = new VBox();
    desc.prefWidthProperty().bind(listView.widthProperty().subtract(100));
    info.getChildren().addAll(title, author, latestChapter, desc, tags);
    cell.getChildren().addAll(cover, info);
  }


  @Override
  protected void updateItem(Novel novel, boolean empty) {
    super.updateItem(novel, empty);
    if (novel == null || empty) {
      setGraphic(null);
    } else if (!Objects.equals(novel, this.novel)) {
      init(novel);
      setGraphic(cell);
    }
    this.novel = novel;
  }

  /**
   * 初始化
   *
   * @param novel 小说信息
   */
  private void init(Novel novel) {
    // 更新封面
    if (UrlUtils.isHttpUrl(novel.getCoverUrl())) {
      Image image = new Image(novel.getCoverUrl(), true);
      cover.setImage(image);
      image.errorProperty().addListener(e -> cover.setImage(NO_COVER));
    } else {
      cover.setImage(NO_COVER);
    }
    // 更新小说信息
    this.title.setText(CharSequenceUtil.blankToDefault(novel.getTitle(), "未知"));
    this.author.setText("作者：".concat(CharSequenceUtil.blankToDefault(novel.getAuthor(), "未知")));
    this.desc.setText("简介：".concat(CharSequenceUtil.blankToDefault(novel.getIntroduce(), "无")));
    this.latestChapter.setText("最新章节：".concat(CharSequenceUtil.blankToDefault(novel.getLatestChapterName(), "无")));
    // 更新标签
    tags.getChildren().clear();
    // 分类
    if (CharSequenceUtil.isNotBlank(novel.getCategory())) {
      tags.getChildren().add(new Tag(novel.getCategory()));
    }
    // 字数
    if (CharSequenceUtil.isNotBlank(novel.getWordCount())) {
      tags.getChildren().add(new Tag(novel.getWordCount().concat("字")));
    }
    // 连载状态
    if (CharSequenceUtil.isNotBlank(novel.getState())) {
      tags.getChildren().add(new Tag(novel.getState()));
    }
  }
}
