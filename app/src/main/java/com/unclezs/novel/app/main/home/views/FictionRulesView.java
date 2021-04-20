package com.unclezs.novel.app.main.home.views;

import cn.hutool.core.io.IoUtil;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.home.views.widgets.ActionButtonTableCell;
import com.unclezs.novel.app.main.home.views.widgets.CheckBoxTableCell;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import lombok.EqualsAndHashCode;

/**
 * @author blog.unclezs.com
 * @date 2021/4/20 11:16
 */
@FxView(fxml = "/layout/home/views/fiction-rules.fxml")
@EqualsAndHashCode(callSuper = true)
public class FictionRulesView extends SidebarView<StackPane> {

  @FXML
  private TableView<AnalyzerRule> rulesTable;

  @Override
  public void onCreated() {
    rulesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    createRuleTableColumns();
    RuleHelper.loadRules(IoUtil.readUtf8(ResourceUtils.stream("rule.json")));
    rulesTable.getItems().addAll(RuleHelper.rules());
  }

  @Override
  public void onHidden() {
    for (AnalyzerRule item : rulesTable.getItems()) {
      System.out.println(item.getName() + " -- " + item.isEnable());
    }
    RuleHelper.setRules(rulesTable.getItems());
  }

  /**
   * 创建书源表格列
   */
  @SuppressWarnings("unchecked")
  private void createRuleTableColumns() {
    // 序号
    TableColumn<AnalyzerRule, Integer> id = NodeHelper.addClass(new TableColumn<>("#"), "id");
    id.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.06));
    id.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(rulesTable.getItems().indexOf(col.getValue()) + 1));
    // 名称
    TableColumn<AnalyzerRule, String> name = new TableColumn<>("名称");
    name.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.1));
    name.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue().getName()));
    // 分组
    TableColumn<AnalyzerRule, String> group = new TableColumn<>("分类");
    group.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.1));
    group.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue().getGroup()));
    // 权重
    TableColumn<AnalyzerRule, Integer> weight = new TableColumn<>("权重");
    weight.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.1));
    weight.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue().getWeight()));
    // 站点
    TableColumn<AnalyzerRule, String> site = new TableColumn<>("站点");
    site.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.4));
    site.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue().getSite()));
    // 是否启用
    TableColumn<AnalyzerRule, Boolean> enabled = NodeHelper.addClass(new TableColumn<>("启用"), "align-center");
    enabled.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.1));
    enabled.setEditable(true);
    enabled.setCellValueFactory(col -> new ReadOnlyBooleanWrapper(col.getValue().isEnable()));
    enabled.setCellFactory(col -> new CheckBoxTableCell<>(this::onEnabledChange));
    // 操作
    TableColumn<AnalyzerRule, AnalyzerRule> operation = NodeHelper.addClass(new TableColumn<>("操作"), "align-center");
    operation.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.13));
    operation.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue()));
    operation.setCellFactory(col -> new ActionButtonTableCell<>(this::onEdit, this::onDelete));
    // 添加列
    rulesTable.getColumns().addAll(id, name, group, weight, site, enabled, operation);
    // 禁用resize
    rulesTable.getColumns().forEach(column -> column.setResizable(false));
  }

  private void onDelete(AnalyzerRule rule, int index) {
    ModalBox.confirm(delete -> {
      if (Boolean.TRUE.equals(delete)) {
        rulesTable.getItems().remove(index);
      }
    }).title("确定删除吗？")
      .message(String.format("是否删除规则：%s", rule.getName()))
      .show();
  }

  /**
   * 编辑规则
   *
   * @param rule  规则
   * @param index 当前行
   */
  private void onEdit(AnalyzerRule rule, int index) {
    System.out.println(index + " - " + rule.getName());
  }

  /**
   * 启用状态改变
   *
   * @param enabled 是否启用
   * @param index   当前行
   */
  private void onEnabledChange(boolean enabled, int index) {
    rulesTable.getItems().get(index).setEnable(enabled);
  }

  /**
   * 禁用选中
   */
  @FXML
  private void disabledSelected() {
    rulesTable.getSelectionModel().getSelectedItems().forEach(rule -> rule.setEnable(false));
    rulesTable.refresh();
  }

  /**
   * 启用选中
   */
  @FXML
  private void enabledSelected() {
    rulesTable.getSelectionModel().getSelectedItems().forEach(rule -> rule.setEnable(true));
    rulesTable.refresh();
  }

  /**
   * 导出选中
   */
  @FXML
  private void exportSelected() {
    ObservableList<AnalyzerRule> rules = rulesTable.getSelectionModel().getSelectedItems();
    for (AnalyzerRule item : rules) {
      System.out.println(item.getName() + " -- " + item.isEnable());
    }
  }

  /**
   * 删除选中
   */
  @FXML
  private void deleteSelected() {
    ObservableList<AnalyzerRule> rules = rulesTable.getSelectionModel().getSelectedItems();
    ModalBox.confirm(delete -> {
      if (Boolean.TRUE.equals(delete)) {
        rulesTable.getItems().removeAll(rules);
      }
    }).title("确定删除吗？")
      .message(String.format("是否删除选中的%d条规则?", rules.size()))
      .show();
  }
}
