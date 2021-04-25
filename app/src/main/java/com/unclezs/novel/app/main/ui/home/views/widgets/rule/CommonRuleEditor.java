package com.unclezs.novel.app.main.ui.home.views.widgets.rule;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.rule.ReplaceRule;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.SerializationUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
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

  public static final String TYPE_HEADER = "请求头";
  public static final String TYPE_REPLACE = "净化规则";
  private final TextArea editor = new TextArea();
  private final TextField key = new TextField();
  private final TextField value = new TextField();
  private final ComboBox<String> type = new ComboBox<>();
  private CommonRule rule;

  public CommonRuleEditor() {
    NodeHelper.addClass(this, "common-rule-editor");
    type.getItems().addAll(TYPE_HEADER, TYPE_REPLACE);
    type.valueProperty().addListener(e -> this.setPrompt());
    type.setValue(TYPE_REPLACE);

    IconButton add = NodeHelper.addClass(new IconButton("添加", IconFont.PLUS, null), "btn");
    IconButton remove = NodeHelper.addClass(new IconButton("删除", IconFont.DELETE, null), "btn");
    add.setOnMouseClicked(e -> doAddOrRemove(true));
    remove.setOnMouseClicked(e -> doAddOrRemove(false));
    HBox typeBox = createActionBox("类型", type);
    HBox keyBox = createActionBox("名称", key);
    HBox valueBox = createActionBox("数值", value);
    HBox buttonBox = new HBox(add, remove);
    buttonBox.setSpacing(20);
    buttonBox.setAlignment(Pos.CENTER);
    getChildren().addAll(editor, typeBox, keyBox, valueBox, buttonBox);
    setSpacing(10);
  }

  private HBox createActionBox(String title, Node item) {
    Label titleLabel = NodeHelper.addClass(new Label(title.concat(":")), "title-label");
    NodeHelper.addClass(item, "action-item");
    HBox.setHgrow(item, Priority.ALWAYS);
    return NodeHelper.addClass(new HBox(titleLabel, item), "action-box");
  }

  private void setPrompt() {
    if (TYPE_HEADER.equals(type.getValue())) {
      key.setPromptText("请输入请求头的名称");
      value.setPromptText("请输入请求头的值");
    } else if (TYPE_REPLACE.equals(type.getValue())) {
      key.setPromptText("请输入净化规则正则");
      value.setPromptText("请输入替换模板$1代表第一组，类推");
    }
  }

  private void doAddOrRemove(boolean add) {
    if (StringUtils.isNotBlank(key.getText())) {
      if (TYPE_HEADER.equals(type.getValue())) {
        RequestParams params = rule.getParams();
        if (params == null) {
          params = new RequestParams();
          rule.setParams(params);
        }
        if (add) {
          params.setHeader(key.getText(), value.getText());
        } else {
          if (CollectionUtils.isNotEmpty(params.getHeaders())) {
            params.getHeaders().remove(key.getText());
          }
        }
        update();
      } else if (TYPE_REPLACE.equals(type.getValue())) {
        if (add) {
          rule.addReplaceRule(new ReplaceRule(key.getText(), value.getText()));
        } else {
          if (CollectionUtils.isNotEmpty(rule.getReplace())) {
            rule.getReplace().remove(new ReplaceRule(key.getText(), null));
          }
        }
        update();
      }
    }
  }

  public void update() {
    editor.setText(GsonUtils.PRETTY.toJson(rule));
  }

  public CommonRule getRule() {
    return RuleHelper.parseRule(editor.getText(), CommonRule.class);
  }

  public void setRule(CommonRule rule) {
    this.rule = SerializationUtils.deepClone(rule);
    update();
  }

  public String getJson() {
    return editor.getText();
  }
}
