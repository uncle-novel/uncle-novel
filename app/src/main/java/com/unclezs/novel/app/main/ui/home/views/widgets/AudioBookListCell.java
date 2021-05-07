package com.unclezs.novel.app.main.ui.home.views.widgets;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.framework.components.LoadingImageView;
import com.unclezs.novel.app.framework.components.Tag;
import com.unclezs.novel.app.framework.support.LocalizedSupport;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.model.AudioBook;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 有声书架列表cell
 *
 * @author blog.unclezs.com
 * @date 2021/5/5 19:51
 */
public class AudioBookListCell extends ListCell<AudioBook> implements LocalizedSupport {

  private final HBox cell = NodeHelper.addClass(new HBox(), "cell");
  private final Label title = NodeHelper.addClass(new Label(), "title");
  private final Label author = new Label(CharSequenceUtil.EMPTY, new Label(localized("novel.author").concat(StrUtil.COLON)));
  private final Label broadcast = new Label(CharSequenceUtil.EMPTY, new Label(localized("novel.speaker").concat(StrUtil.COLON)));
  private final Label latestChapter = new Label(CharSequenceUtil.EMPTY, new Label("上次听到".concat(StrUtil.COLON)));
  private final HBox tags = NodeHelper.addClass(new HBox(), "tags");
  private final LoadingImageView cover;
  private final ContextMenu contextMenu;
  private final Consumer<AudioBook> onPrimaryClick;

  public AudioBookListCell(ContextMenu contextMenu, Consumer<AudioBook> onPrimaryClick) {
    this.cover = new LoadingImageView(BookListCell.NO_COVER, 65, 85);
    VBox infoBox = new VBox(title, author, broadcast, latestChapter, tags);
    infoBox.setSpacing(4);
    infoBox.setAlignment(Pos.CENTER_LEFT);
    cell.getChildren().addAll(cover, infoBox);
    this.contextMenu = contextMenu;
    this.onPrimaryClick = onPrimaryClick;
  }

  @Override
  protected void updateItem(AudioBook book, boolean empty) {
    super.updateItem(book, empty);
    if (book == null || empty) {
      setGraphic(null);
      setText(null);
      setContextMenu(null);
      setOnMouseClicked(null);
    } else {
      init(book);
      if (getGraphic() == null) {
        setGraphic(cell);
      }
      EventUtils.setOnMousePrimaryClick(this, e -> onPrimaryClick.accept(book));
      setContextMenu(contextMenu);
    }
  }

  /**
   * 初始化
   *
   * @param book 小说信息
   */
  private void init(AudioBook book) {
    // 更新封面
    cover.setImage(book.getCover());
    // 更新小说信息
    String unknown = localized("unknown");
    this.title.setText(CharSequenceUtil.blankToDefault(book.getName(), unknown));
    this.author.setText(CharSequenceUtil.blankToDefault(book.getAuthor(), unknown));
    this.latestChapter.setText(CharSequenceUtil.blankToDefault(book.getCurrentChapterName(), unknown));
    this.broadcast.setText(CharSequenceUtil.blankToDefault(book.getBroadcast(), unknown));
    // 更新标签
    List<Tag> novelTags = new ArrayList<>();
    tags.getChildren().setAll(novelTags);
  }
}
