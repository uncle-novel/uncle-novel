package com.unclezs.novel.app.main.model.config;

import com.unclezs.novel.app.main.views.home.FictionBookshelfView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/5/10 17:31
 */
@Data
public class BookShelfConfig {

  /**
   * 选中的分组
   */
  private String group = FictionBookshelfView.GROUP_ALL;

  /**
   * 书架书籍自动更新
   */
  private ObjectProperty<Boolean> autoUpdate = new SimpleObjectProperty<>(false);
  /**
   * 总是显示标题
   */
  private ObjectProperty<Boolean> alwaysShowBookTitle = new SimpleObjectProperty<>(false);
}
