package com.unclezs.novel.app.main.ui.home.views;

import com.jfoenix.controls.JFXMasonryPane;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import lombok.EqualsAndHashCode;

/**
 * 小说书架
 *
 * @author blog.unclezs.com
 * @date 2021/4/25 9:40
 */
@FxView(fxml = "/layout/home/views/fiction-bookshelf.fxml")
@EqualsAndHashCode(callSuper = true)
public class FictionBookshelfView extends SidebarView<StackPane> {

  @FXML
  private JFXMasonryPane bookPanel;

  @Override
  public void onCreated() {

  }

  public void add() {
    Novel novelInfo = new Novel();
    novelInfo.setAuthor("辰东");
    novelInfo.setTitle("完美世界");
    novelInfo.setUrl("https://www.zhaishuyuan.com/read/33959");
    novelInfo.setCategory("东方玄幻");
    novelInfo.setWordCount("6593730");
    novelInfo.setIntroduce("一粒尘可填海，一根草斩尽日月星辰，弹指间天翻地覆。群雄并起，万族林立，诸圣争霸，乱天动地。问苍茫大地，谁主沉浮？！一个少年从大荒中走出，一切从这里开始………");
    novelInfo.setLatestChapterName("第两千零一十四章 独断万古（大结局）");
    novelInfo.setCoverUrl("https://img.zhaishuyuan.com/bookpic/s191.jpg");
    novelInfo.setState("已完结");
    novelInfo.setUpdateTime("2017-8-28 19:21:17");
  }
}
