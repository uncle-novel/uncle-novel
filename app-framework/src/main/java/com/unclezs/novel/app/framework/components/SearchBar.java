package com.unclezs.novel.app.framework.components;

import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.support.LocalizedSupport;
import com.unclezs.novel.app.framework.util.NodeHelper;
import java.util.Collections;
import java.util.List;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.Getter;
import lombok.Setter;

/**
 * @author blog.unclezs.com
 * @date 2021/4/16 13:56
 */
public class SearchBar extends HBox implements LocalizedSupport {

  public static final EventType<SearchEvent> ON_SEARCH = new EventType<>(InputEvent.ANY, "ON_SEARCH");
  public static final String DEFAULT_STYLE_CLASS = "search-bar";
  @Getter
  private final TextField input;
  /**
   * 提示文字
   */
  @Getter
  private String prompt = localized("search.prompt");
  /**
   * 类型下拉框
   */
  @Getter
  private ComboBox<String> typeBox;
  /**
   * 搜索事件监听
   */
  @Getter
  @Setter
  private EventHandler<? super SearchEvent> onSearch;

  public SearchBar() {
    NodeHelper.addClass(this, DEFAULT_STYLE_CLASS);
    this.input = new TextField();
    HBox.setHgrow(input, Priority.ALWAYS);
    this.input.setPromptText(prompt);
    IconButton searchButton = new IconButton(IconFont.SEARCH, localized("search"));
    this.getChildren().setAll(input, searchButton);
    this.input.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        fire();
      }
    });
    searchButton.setOnMouseClicked(event -> fire());
  }

  /**
   * 获取焦点
   */
  public void focus() {
    input.requestFocus();
  }

  /**
   * 触发搜索
   */
  private void fire() {
    if (onSearch != null && StringUtils.isNotBlank(input.getText())) {
      onSearch.handle(new SearchEvent(input.getText(), currentType()));
    }
  }

  /**
   * 添加类型
   *
   * @param type 类型
   */
  public void addType(String type) {
    if (typeBox == null) {
      typeBox = new ComboBox<>();
      typeBox.setValue(type);
      getChildren().add(0, typeBox);
    }
    typeBox.getItems().add(type);
  }

  /**
   * 添加类型
   *
   * @param types 类型
   */
  public void addTypes(String... types) {
    for (String type : types) {
      addType(type);
    }
  }

  /**
   * 添加类型
   *
   * @param types 类型
   */
  public void addTypes(List<String> types) {
    for (String type : types) {
      addType(type);
    }
  }

  /**
   * 添加类型
   *
   * @param type 类型
   */
  public void removeType(String type) {
    if (typeBox == null) {
      return;
    }
    typeBox.getItems().remove(type);
  }

  public void clearType() {
    if (typeBox != null) {
      typeBox.getItems().clear();
      getChildren().remove(typeBox);
      typeBox = null;
    }
  }

  public String getCurrentType() {
    if (typeBox == null) {
      return null;
    }
    return typeBox.getValue();
  }

  public void setType(String type) {
    if (typeBox == null) {
      return;
    }
    typeBox.setValue(type);
  }

  public List<String> getTypeItems() {
    if (typeBox == null) {
      return Collections.emptyList();
    }
    return typeBox.getItems();
  }

  public void setPrompt(String prompt) {
    this.prompt = prompt;
    this.input.setPromptText(prompt);
  }

  public String currentType() {
    if (typeBox == null) {
      return null;
    }
    return typeBox.getValue();
  }

  @Override
  public String getBundleName() {
    return COMMON_BUNDLE_NAME;
  }

  /**
   * 搜索事件
   */
  public static class SearchEvent extends InputEvent {

    @Getter
    private final String input;
    @Getter
    private final String type;

    public SearchEvent(String input, String type) {
      super(ON_SEARCH);
      this.input = input;
      this.type = type;
    }
  }
}
