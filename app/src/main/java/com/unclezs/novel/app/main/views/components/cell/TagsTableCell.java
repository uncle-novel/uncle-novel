package com.unclezs.novel.app.main.views.components.cell;

import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.framework.components.Tag;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

/**
 * 表格标签列
 *
 * @author blog.unclezs.com
 * @since 2021/5/2 17:00
 */
public class TagsTableCell<S> extends TableCell<S, String> {

  private final List<Tag> tags = new ArrayList<>();
  private final HBox box = new HBox();

  public TagsTableCell() {
    box.setSpacing(3);
  }

  @Override
  protected void updateItem(String item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
      setText(null);
    } else {
      String[] tagsText = item.split(StrUtil.COMMA);
      box.getChildren().clear();
      for (int i = 0; i < tagsText.length; i++) {
        if (tags.size() < i + 1) {
          tags.add(new Tag(tagsText[i]));
        } else {
          tags.get(i).setText(tagsText[i]);
        }
        box.getChildren().add(tags.get(i));
      }
      setGraphic(box);
    }
  }
}
