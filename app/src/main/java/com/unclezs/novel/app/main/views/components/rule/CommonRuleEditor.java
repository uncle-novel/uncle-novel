package com.unclezs.novel.app.main.views.components.rule;

import cn.hutool.core.text.CharSequenceUtil;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.rule.ReplaceRule;
import com.unclezs.novel.analyzer.core.rule.RuleConstant;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.SerializationUtils;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * 通用规则编辑器
 *
 * @author blog.unclezs.com
 * @date 2021/4/26 1:01
 */
public class CommonRuleEditor extends VBox {

  private final TextArea editor = new TextArea();
  private final ComboBox<String> types;
  private final TextField ruleField;
  private CommonRule rule;
  private HBox pagesBox;

  public CommonRuleEditor() {
    NodeHelper.addClass(this, "common-rule-editor");
    editor.setWrapText(true);
    // 替换规则
    TextField key = new TextField();
    HBox keyBox = createActionBox("替换文字或正则", key);
    key.setPromptText("请输入替换文字，正则以regex:开头");

    TextField value = new TextField();
    value.setPromptText("请输入替换为的文字，正则以$组号替换");
    HBox valueBox = createActionBox("替换文字或模板", value);

    TextField script = new TextField();
    HBox scriptBox = createActionBox("结果预处理脚本", script);
    script.setPromptText("处理匹配后结果的脚本");

    ruleField = new TextField();
    HBox ruleBox = createActionBox("匹配器规则内容", ruleField);
    ruleField.setPromptText("处理匹配后结果的脚本");
    ruleField.focusedProperty().addListener(e -> {
      if (!ruleField.isFocused()) {
        rule().setRule(CharSequenceUtil.emptyToNull(ruleField.getText()));
        update();
      }
    });

    types = new ComboBox<>(
      FXCollections.observableArrayList(RuleConstant.TYPE_XPATH, RuleConstant.TYPE_CSS, RuleConstant.TYPE_JSON,
        RuleConstant.TYPE_REGEX));
    HBox typeBox = createActionBox("匹配器规则类型", types);
    types.valueProperty().addListener(e -> {
      rule().setType(types.getValue());
      update();
    });

    IconButton add = NodeHelper.addClass(new IconButton("添加替换规则", IconFont.PLUS, null), "btn");
    IconButton addScript = NodeHelper.addClass(new IconButton("更新预处理脚本", IconFont.CODE, null), "btn");
    IconButton reset = NodeHelper.addClass(new IconButton("重置规则", IconFont.DELETE, null), "btn");
    add.setOnMouseClicked(e -> {
      rule().addReplaceRule(new ReplaceRule(key.getText(), value.getText()));
      update();
    });
    addScript.setOnAction(e -> {
      rule().setScript(script.getText());
      update();
    });
    reset.setOnAction(e -> setRule(new CommonRule()));
    // 添加组件到容器
    HBox buttonBox = new HBox(reset, add, addScript);
    buttonBox.setSpacing(10);
    buttonBox.setAlignment(Pos.CENTER_RIGHT);
    getChildren().addAll(editor, typeBox, ruleBox, keyBox, valueBox, scriptBox, buttonBox);
    setSpacing(10);
  }

  /**
   * 创建一行
   *
   * @param title 标题
   * @param item  内容
   * @return 行节点
   */
  private HBox createActionBox(String title, Node item) {
    Label titleLabel = NodeHelper.addClass(new Label(title.concat(":")), "title-label");
    NodeHelper.addClass(item, "action-item");
    HBox.setHgrow(item, Priority.ALWAYS);
    return NodeHelper.addClass(new HBox(titleLabel, item), "action-box");
  }

  /**
   * 是否显示 commonRule.page 的编辑
   *
   * @param show true 显示
   */
  public void setShowPage(boolean show) {
    if (this.pagesBox == null) {
      // 特定页面显示
      ComboBox<String> pages =
        new ComboBox<>(FXCollections.observableArrayList(RuleConstant.SEARCH_PAGE, RuleConstant.DETAIL_PAGE));
      pagesBox = createActionBox("规则对应的页面", pages);
      pages.valueProperty().addListener(e -> {
        rule().setPage(pages.getValue());
        update();
      });
    }
    getChildren().remove(pagesBox);
    if (show) {
      getChildren().add(1, pagesBox);
    }
  }

  /**
   * 更新数据
   */
  public void update() {
    editor.setText(GsonUtils.PRETTY.toJson(rule));
  }

  /**
   * 获取最新的规则
   *
   * @return 规则
   */
  public CommonRule getRule() {
    return RuleHelper.parseRule(editor.getText(), CommonRule.class);
  }

  /**
   * 设置当前规则
   *
   * @param rule 规则
   */
  public void setRule(CommonRule rule) {
    this.rule = SerializationUtils.deepClone(rule);
    types.setValue(rule.getType());
    ruleField.setText(rule.getRule());
    update();
  }

  /**
   * 获取规则的JSON数据
   *
   * @return json字符串
   */
  public String getJson() {
    return editor.getText();
  }

  /**
   * 获取当前规则
   *
   * @return 规则
   */
  private CommonRule rule() {
    return rule;
  }
}
