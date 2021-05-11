package com.unclezs.novel.app.main.model;

import com.unclezs.novel.app.main.ui.home.views.FictionBookshelfView;
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
}
