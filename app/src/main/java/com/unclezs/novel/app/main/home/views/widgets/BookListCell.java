package com.unclezs.novel.app.main.home.views.widgets;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.components.ImageViewPlus;
import com.unclezs.novel.app.framework.components.Tag;
import com.unclezs.novel.app.framework.support.LocalizedSupport;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import java.util.ArrayList;
import java.util.List;
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
public class BookListCell extends ListCell<Novel> implements LocalizedSupport {

  public static final String NO_COVER_IMAGE = "assets/images/no-cover.png";
  public static final Image NO_COVER = new Image(ResourceUtils.externalForm(NO_COVER_IMAGE), true);
  private final HBox cell = NodeHelper.addClass(new HBox(), "cell");
  private final ImageView cover;
  private final Label title = NodeHelper.addClass(new Label(), "title");
  private final Label author = new Label(CharSequenceUtil.EMPTY, new Label(localized("novel.author").concat(StrUtil.COLON)));
  private final Label desc = new Label(CharSequenceUtil.EMPTY, new Label(localized("novel.desc").concat(StrUtil.COLON)));
  private final Label latestChapter = new Label(CharSequenceUtil.EMPTY, new Label(localized("novel.chapter.latest").concat(StrUtil.COLON)));
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
    cover.setImage(NO_COVER);
    if (UrlUtils.isHttpUrl(novel.getCoverUrl())) {
      Image image = new Image(novel.getCoverUrl(), true);
      image.progressProperty().addListener(e -> {
        if (image.getProgress() == 1 && !image.isError()) {
          cover.setImage(image);
        }
      });
    }
    // 更新小说信息
    String unknown = localized("unknown");
    this.title.setText(CharSequenceUtil.blankToDefault(novel.getTitle(), unknown));
    this.author.setText(CharSequenceUtil.blankToDefault(novel.getAuthor(), unknown));
    this.desc.setText(CharSequenceUtil.blankToDefault(novel.getIntroduce(), localized("none")));
    this.latestChapter.setText(CharSequenceUtil.blankToDefault(novel.getLatestChapterName(), unknown));
    // 更新标签
    List<Tag> novelTags = new ArrayList<>();
    // 分类
    if (CharSequenceUtil.isNotBlank(novel.getCategory())) {
      novelTags.add(new Tag(novel.getCategory()));
    }
    // 连载状态
    if (CharSequenceUtil.isNotBlank(novel.getState())) {
      novelTags.add(new Tag(novel.getState()));
    }
    // 字数
    if (CharSequenceUtil.isNotBlank(novel.getWordCount())) {
      novelTags.add(new Tag(novel.getWordCount()));
    }
    tags.getChildren().setAll(novelTags);
  }
}
