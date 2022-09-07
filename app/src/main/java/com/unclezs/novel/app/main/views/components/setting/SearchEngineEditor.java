package com.unclezs.novel.app.main.views.components.setting;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.db.beans.SearchEngine;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;

/**
 * @author blog.unclezs.com
 * @since 2021/4/28 14:59
 */
public class SearchEngineEditor extends VBox {

  public static final String DEFAULT_STYLE_CLASS = "search-engine-editor";
  private final TextField nameInput = new TextField();
  private final TextField searchInput = new TextField();
  private final TextField stylesheetInput = new TextField();
  private final SearchEngine engine;

  public SearchEngineEditor(SearchEngine engine) {
    this.engine = engine;
    NodeHelper.addClass(this, DEFAULT_STYLE_CLASS);
    Label nameLabel = new Label("名称");
    nameInput.setText(engine.getName());
    nameInput.setPromptText("请输入搜索引擎名称");

    Label searchLabel = new Label("搜索网址");
    searchInput.setText(engine.getUrl());
    searchInput.setPromptText("请输入搜索引擎搜索链接，关键词用{{keyword}}代替");

    Label stylesheetLabel = new Label("自定义样式");
    stylesheetInput.setText(engine.getStylesheet());
    stylesheetInput.setPromptText("请输入或选择自定义搜索引擎的CSS样式");
    IconButton stylesheetSelector = NodeHelper.addClass(new IconButton("选择", IconFont.BROWSER, null), "btn");
    stylesheetSelector.setOnMouseClicked(e -> {
      FileChooser chooser = new FileChooser();
      chooser.getExtensionFilters().add(new ExtensionFilter("CSS", "*.css"));
      File file = chooser.showOpenDialog(App.stage());
      if (FileUtil.exist(file)) {
        stylesheetInput.setText(file.getAbsolutePath());
      }
    });
    HBox stylesheetsBox = new HBox(stylesheetInput, stylesheetSelector);
    HBox.setHgrow(stylesheetInput, Priority.ALWAYS);
    stylesheetsBox.setSpacing(10);

    getChildren().addAll(nameLabel, nameInput, searchLabel, searchInput, stylesheetLabel, stylesheetsBox);
    setSpacing(10);
  }

  public SearchEngine getResult() {
    engine.setName(nameInput.getText());
    engine.setUrl(searchInput.getText());
    engine.setStylesheet(stylesheetInput.getText());
    return engine;
  }
}
