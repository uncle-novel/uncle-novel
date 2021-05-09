package com.unclezs.novel.app.main.ui.home.views;

import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.app.framework.components.cell.BaseListCell;
import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import java.util.function.Function;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * @author blog.unclezs.com
 * @date 2021/5/6 22:01
 */
public class TocListCell extends BaseListCell<Chapter> {

  public static final String DEFAULT_STYLE_CLASS = "toc-list-cell";
  private final Icon icon = new Icon(IconFont.BOOKMARK);
  private final Icon cached = new Icon(IconFont.ENABLED);
  private final Label label = new Label();
  private final HBox cell = new HBox();
  private Function<Integer, Boolean> cacheStateGetter;

  public TocListCell() {
    label.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(label, Priority.ALWAYS);
    this.cacheStateGetter = index -> getItem().getContent() != null;
  }

  public TocListCell(Function<Integer, Boolean> cacheStateGetter) {
    this();
    this.cacheStateGetter = cacheStateGetter;
  }

  @Override
  protected void updateItem(Chapter item) {
    NodeHelper.addClass(this, DEFAULT_STYLE_CLASS);
    setGraphic(cell);
    label.setGraphic(icon);
    label.setText(item.getName());
    cell.getChildren().setAll(label);
    if (Boolean.TRUE.equals(cacheStateGetter.apply(getIndex()))) {
      cell.getChildren().add(cached);
    }
  }
}
