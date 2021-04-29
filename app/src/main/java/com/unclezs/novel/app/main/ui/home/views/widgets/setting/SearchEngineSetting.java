package com.unclezs.novel.app.main.ui.home.views.widgets.setting;

import cn.hutool.core.bean.BeanUtil;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.SearchEngine;
import com.unclezs.novel.app.main.ui.home.views.widgets.ActionButtonTableCell;
import com.unclezs.novel.app.main.ui.home.views.widgets.CheckBoxTableCell;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * 搜索引擎管理器
 *
 * @author blog.unclezs.com
 * @date 2021/4/28 15:48
 */
public class SearchEngineSetting extends VBox {

  private final TableView<SearchEngine> table = new TableView<>();

  public SearchEngineSetting() {
    NodeHelper.addClass(this, "search-engine-manager");
    IconButton add = NodeHelper.addClass(new IconButton("添加", IconFont.PLUS), "btn");
    add.setOnMouseClicked(e -> addSearchEngine());
    getChildren().addAll(add, table);
    table.setItems(SettingManager.manager().getSearchEngines());
    createColumns();
    table.getSelectionModel().selectFirst();
  }

  @SuppressWarnings("unchecked")
  private void createColumns() {
    // 序号
    TableColumn<SearchEngine, Integer> id = NodeHelper.addClass(new TableColumn<>("#"), "id");
    id.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
    id.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(param.getValue()) + 1));
    // 名称
    TableColumn<SearchEngine, String> name = new TableColumn<>("名称");
    name.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
    name.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getName()));
    // 域名
    TableColumn<SearchEngine, String> domain = new TableColumn<>("域名");
    domain.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
    domain.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getDomain()));
    // 启用
    TableColumn<SearchEngine, Boolean> enabled = NodeHelper.addClass(new TableColumn<>("启用"), "align-center");
    enabled.setCellValueFactory(col -> new ReadOnlyBooleanWrapper(Boolean.TRUE.equals(col.getValue().getEnabled())));
    enabled.setCellFactory(col -> new CheckBoxTableCell<>(this::onSearchEngineEnabledChange));
    enabled.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
    // 操作
    TableColumn<SearchEngine, SearchEngine> operation = NodeHelper.addClass(new TableColumn<>("操作"), "align-center");
    operation.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
    operation.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue()));
    operation.setCellFactory(col -> new ActionButtonTableCell<>(this::onEditSearchEngine, this::onDeleteSearchEngine));

    table.getColumns().addAll(id, name, domain, enabled, operation);
    table.getColumns().forEach(column -> column.setResizable(false));
    table.getSelectionModel().selectFirst();
  }

  /**
   * 删除规则
   *
   * @param searchEngine 规则
   * @param index        索引
   */
  private void onDeleteSearchEngine(SearchEngine searchEngine, int index) {
    ModalBox.confirm(delete -> {
      if (Boolean.TRUE.equals(delete)) {
        table.getItems().remove(index);
      }
    }).title("确定删除吗？")
      .message(String.format("是否删除规则：%s", searchEngine.getName()))
      .show();
  }

  /**
   * 编辑规则
   *
   * @param searchEngine 规则
   * @param index        当前行
   */
  private void onEditSearchEngine(SearchEngine searchEngine, int index) {
    SearchEngineEditor editor = new SearchEngineEditor(BeanUtil.toBean(searchEngine, SearchEngine.class));
    ModalBox.confirm(save -> {
      if (Boolean.TRUE.equals(save)) {
        BeanUtil.copyProperties(editor.getResult(), searchEngine);
        table.refresh();
      }
    }).body(editor).title("编辑搜索引擎").show();
  }

  /**
   * 搜索引擎启用状态改变
   *
   * @param enabled 是否启用
   * @param index   当前行
   */
  private void onSearchEngineEnabledChange(Boolean enabled, int index) {
    table.getItems().get(index).setEnabled(Boolean.TRUE.equals(enabled));
  }

  /**
   * 添加搜索引擎
   */
  public void addSearchEngine() {
    SearchEngineEditor editor = new SearchEngineEditor(new SearchEngine());
    AtomicBoolean validate = new AtomicBoolean(true);
    ModalBox.confirm(save -> {
      if (Boolean.TRUE.equals(save)) {
        SearchEngine engine = editor.getResult();
        if (StringUtils.isBlank(engine.getUrl()) || StringUtils.isBlank(engine.getName())) {
          Toast.error((StackPane) editor.getParent(), "名称与搜索网址必须填写");
          validate.set(false);
          return;
        }
        if (!UrlUtils.isHttpUrl(engine.getUrl())) {
          Toast.error((StackPane) editor.getParent(), "搜索网址必须为HTTP链接");
          validate.set(false);
          return;
        }
        table.getItems().add(engine);
      }
    }).body(editor).success(() -> {
      boolean success = validate.get();
      validate.set(true);
      return success;
    }).title("添加搜索引擎").show();
  }
}
