package com.unclezs.novel.app.framework.components;

import com.unclezs.novel.app.framework.collection.SimpleObservableList;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.components.properties.Str;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;

/**
 * @author blog.unclezs.com
 * @date 2021/4/16 13:56
 */
public class SearchBar extends HBox {

  public static final EventType<SearchEvent> ON_SEARCH = new EventType<>(InputEvent.ANY, "ON_SEARCH");
  public static final String DEFAULT_STYLE_CLASS = "search-bar";

  private final TextField input;
  private final IconButton searchButton;
  private ComboBox<String> types;
  /**
   * 搜索类型
   */
  @Getter
  private final ObservableList<Str> type = new SimpleObservableList<>() {
    @Override
    public void onAdd(Str type) {
      addType(type.getValue());
    }
  };
  @Getter
  @Setter
  private EventHandler<? super SearchEvent> onSearch;

  public SearchBar() {
    NodeHelper.addClass(this, DEFAULT_STYLE_CLASS);
    this.input = new TextField();
    this.searchButton = new IconButton(IconFont.SEARCH, "search");
    this.getChildren().setAll(input, searchButton);
    this.input.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        onSearch.handle(new SearchEvent(input.getText(), types.getValue()));
      }
    });
    this.searchButton.setOnMouseClicked(event -> onSearch.handle(new SearchEvent(input.getText(), types.getValue())));
  }

  /**
   * 获取焦点
   */
  public void focus() {
    input.requestFocus();
  }

  /**
   * 添加类型
   *
   * @param type 类型
   */
  public void addType(String type) {
    if (types == null) {
      types = new ComboBox<>();
      types.setValue(type);
      getChildren().add(0, types);
    }
    types.getItems().add(type);
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
