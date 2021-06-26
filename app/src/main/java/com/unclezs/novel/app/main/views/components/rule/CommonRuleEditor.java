package com.unclezs.novel.app.main.views.components.rule;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.rule.ReplaceRule;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.SerializationUtils;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
  private CommonRule rule;

  public CommonRuleEditor() {
    NodeHelper.addClass(this, "common-rule-editor");
    editor.setWrapText(true);
    // 替换规则
    TextField key = new TextField();
    key.setPromptText("请输入替换文字，正则以regex:开头");
    TextField value = new TextField();
    value.setPromptText("请输入替换为的文字，正则以$组号替换");
    HBox keyBox = createActionBox("替换文字或正则", key);
    HBox valueBox = createActionBox("替换文字或模板", value);
    IconButton add = NodeHelper.addClass(new IconButton("添加", IconFont.PLUS, null), "btn");
    IconButton remove = NodeHelper.addClass(new IconButton("删除", IconFont.DELETE, null), "btn");
    add.setOnMouseClicked(e -> {
      rule().addReplaceRule(new ReplaceRule(key.getText(), value.getText()));
      update();
    });
    remove.setOnMouseClicked(e -> {
      if (CollectionUtils.isNotEmpty(rule().getReplace())) {
        rule().getReplace().remove(new ReplaceRule(key.getText(), null));
        update();
      }
    });
    HBox buttonBox = new HBox(add, remove);
    buttonBox.setSpacing(20);
    buttonBox.setAlignment(Pos.CENTER);
    getChildren().addAll(editor, keyBox, valueBox, buttonBox);
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
