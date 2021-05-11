package com.unclezs.novel.app.framework.components;

import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.util.DesktopUtils;
import com.unclezs.novel.app.framework.util.NodeHelper;
import java.io.File;
import javafx.beans.NamedArg;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import lombok.Getter;

/**
 * 文件选择器
 *
 * @author blog.unclezs.com
 * @date 2021/4/30 13:28
 */
@Getter
public class FileSelector extends HBox {

  private final IconButton choose;
  private final TextField input;
  private IconButton open;

  private String promptText;
  private String text;

  private String chooseText;
  private String openText;

  public FileSelector(@NamedArg("chooseText") String chooseText) {
    choose = NodeHelper.addClass(new IconButton(chooseText, IconFont.DIR), "btn");
    input = new TextField();
    getChildren().addAll(input, choose);
    HBox.setHgrow(input, Priority.ALWAYS);
    setSpacing(10);

    // 文件选择事件
    choose.setOnAction(e -> {
      DirectoryChooser chooser = new DirectoryChooser();
      // 以前的文件夹
      File old = new File(input.getText());
      if (old.exists()) {
        chooser.setInitialDirectory(old);
      }
      File file = chooser.showDialog(AppContext.getInstance().getPrimaryStage());
      if (file != null) {
        input.setText(file.getAbsolutePath());
      }
    });
  }

  public void setOnChoose(EventHandler<ActionEvent> eventHandler) {
    choose.setOnAction(eventHandler);
  }

  public void setPromptText(String promptText) {
    this.promptText = promptText;
    this.input.setPromptText(promptText);
  }

  public void setText(String text) {
    this.text = text;
    this.input.setText(text);
  }

  public void setChooseText(String chooseText) {
    this.chooseText = chooseText;
    this.choose.setText(chooseText);
  }

  /**
   * 设置打开按钮文字,同时创建打开按钮
   *
   * @param openText 文字
   */
  public void setOpenText(String openText) {
    this.openText = openText;
    // 打开文件夹
    open = NodeHelper.addClass(new IconButton(openText, IconFont.OPEN_DIR), "btn");
    getChildren().add(open);
    open.setOnAction(e -> {
      File dir = new File(input.getText());
      if (dir.exists()) {
        DesktopUtils.openDir(dir);
      }
    });
  }
}
