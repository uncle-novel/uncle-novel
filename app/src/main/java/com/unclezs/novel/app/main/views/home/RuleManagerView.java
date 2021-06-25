package com.unclezs.novel.app.main.views.home;

import cn.hutool.core.io.FileUtil;
import com.google.gson.reflect.TypeToken;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.manager.RuleManager;
import com.unclezs.novel.app.main.util.MixPanelHelper;
import com.unclezs.novel.app.main.views.components.cell.ActionButtonTableCell;
import com.unclezs.novel.app.main.views.components.cell.CheckBoxTableCell;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 书源管理
 *
 * @author blog.unclezs.com
 * @date 2021/4/20 11:16
 */
@Slf4j
@FxView(fxml = "/layout/home/rule-manager.fxml")
@EqualsAndHashCode(callSuper = true)
public class RuleManagerView extends SidebarView<StackPane> {

  /**
   * 书源的文件名
   */
  public static final String RULES_FILE_NAME = "rules.json";
  private static final String PAGE_NAME = "书源管理";
  @FXML
  private TableView<AnalyzerRule> rulesTable;

  @Override
  public void onCreated() {
    rulesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    createRuleTableColumns();
    rulesTable.setItems(RuleManager.rules());
  }

  @Override
  public void onHidden() {
    RuleManager.update(rulesTable.getItems());
    RuleManager.save();
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    MixPanelHelper.event(PAGE_NAME);
    AnalyzerRule rule = bundle.get(RuleEditorView.BUNDLE_RULE_KEY);
    // 新增书源
    if (rule != null) {
      rulesTable.getItems().add(rule);
    }
    rulesTable.refresh();
  }

  @Override
  public void onDestroy() {
    RuleManager.save();
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
    TableColumn<AnalyzerRule, String> name = new TableColumn<>(localized("rule.manager.table.name"));
    name.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.15));
    name.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue().getName()));
    // 分组
    TableColumn<AnalyzerRule, String> group = new TableColumn<>(localized("rule.manager.table.category"));
    group.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.08));
    group.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue().getGroup()));
    // 权重
    TableColumn<AnalyzerRule, Integer> weight = NodeHelper.addClass(new TableColumn<>(localized("rule.manager.table.weight")), "align-center");
    weight.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.1));
    weight.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue().getWeight()));
    // 站点
    TableColumn<AnalyzerRule, String> site = new TableColumn<>(localized("rule.manager.table.site"));
    site.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.25));
    site.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue().getSite()));
    // 书源类型
    TableColumn<AnalyzerRule, String> type = NodeHelper.addClass(new TableColumn<>(localized("rule.manager.table.type")));
    type.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.1));
    type.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(Boolean.TRUE.equals(col.getValue().getAudio()) ? "有声" : "文字"));
    // 是否启用
    TableColumn<AnalyzerRule, Boolean> enabled = NodeHelper.addClass(new TableColumn<>(localized("rule.manager.table.enabled")), "align-center");
    enabled.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.1));
    enabled.setCellValueFactory(col -> new ReadOnlyBooleanWrapper(Boolean.TRUE.equals(col.getValue().getEnabled())));
    enabled.setCellFactory(col -> new CheckBoxTableCell<>(this::onEnabledChange));
    // 操作
    TableColumn<AnalyzerRule, AnalyzerRule> operation = NodeHelper.addClass(new TableColumn<>(localized("rule.manager.table.operation")), "align-center");
    operation.prefWidthProperty().bind(rulesTable.widthProperty().multiply(0.13));
    operation.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue()));
    operation.setCellFactory(col -> new ActionButtonTableCell<>(this::onEdit, this::onDelete));
    // 添加列
    rulesTable.getColumns().addAll(id, name, group, weight, site, type, enabled, operation);
    // 禁用resize
    rulesTable.getColumns().forEach(column -> column.setResizable(false));
    rulesTable.setOnContextMenuRequested(event -> {
      if (rulesTable.getSelectionModel().isEmpty()) {
        rulesTable.getContextMenu().hide();
      }
    });
  }

  /**
   * 删除规则
   *
   * @param rule  规则
   * @param index 索引
   */
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
    SidebarNavigateBundle bundle = new SidebarNavigateBundle();
    bundle.put(RuleEditorView.BUNDLE_RULE_KEY, rule);
    navigation.navigate(RuleEditorView.class, bundle);
  }

  /**
   * 启用状态改变
   *
   * @param enabled 是否启用
   * @param index   当前行
   */
  private void onEnabledChange(boolean enabled, int index) {
    rulesTable.getItems().get(index).setEnabled(enabled);
  }

  /**
   * 禁用选中
   */
  @FXML
  private void disabledSelected() {
    rulesTable.getSelectionModel().getSelectedItems().forEach(rule -> rule.setEnabled(false));
    rulesTable.refresh();
  }

  /**
   * 启用选中
   */
  @FXML
  private void enabledSelected() {
    rulesTable.getSelectionModel().getSelectedItems().forEach(rule -> rule.setEnabled(true));
    rulesTable.refresh();
  }

  /**
   * 导出选中
   */
  @FXML
  private void exportSelected() {
    exportRule(new ArrayList<>(rulesTable.getSelectionModel().getSelectedItems()));
  }

  /**
   * 导出全部
   */
  @FXML
  private void exportAll() {
    exportRule(new ArrayList<>(rulesTable.getItems()));
  }

  /**
   * 导入规则，并去重
   */
  @FXML
  private void importRule() {
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JSON", "*.json");
    fileChooser.getExtensionFilters().add(filter);
    File file = fileChooser.showOpenDialog(App.stage());
    importRule(file);
  }

  /**
   * 导入书源
   *
   * @param file 文件
   */
  public void importRule(File file) {
    if (FileUtil.exist(file)) {
      try {
        List<AnalyzerRule> rules = GsonUtils.me().fromJson(FileUtil.readUtf8String(file), new TypeToken<List<AnalyzerRule>>() {
        }.getType());
        Set<String> ruleSites = rulesTable.getItems().stream().map(rule -> UrlUtils.getHost(rule.getSite())).collect(Collectors.toSet());
        rules.stream()
          .filter(rule -> rule.isEffective() && !ruleSites.contains(UrlUtils.getHost(rule.getSite())))
          .forEach(rule -> rulesTable.getItems().add(rule));
        RuleManager.save();
      } catch (Exception e) {
        Toast.error("导入失败");
        log.error("书源导入失败", e);
      }
    }
  }

  /**
   * 导出书源
   *
   * @param rules 规则列表
   */
  private void exportRule(List<AnalyzerRule> rules) {
    String ruleJson = GsonUtils.toJson(rules);
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialFileName(RULES_FILE_NAME);
    File file = fileChooser.showSaveDialog(App.stage());
    if (file != null) {
      FileUtil.writeUtf8String(ruleJson, file);
      Toast.success(getRoot(), "导出成功");
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
        Toast.success(getRoot(), "删除成功");
      }
    }).title("确定删除吗？")
      .message(String.format("是否删除选中的%d条规则?", rules.size()))
      .show();
  }

  /**
   * 新增规则
   */
  @FXML
  private void addRule() {
    navigation.navigate(RuleEditorView.class);
  }
}
