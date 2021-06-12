package com.unclezs.novel.app.main.views.components.rule;

import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.analyzer.request.MediaType;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * @author blog.unclezs.com
 * @date 2021/4/26 23:24
 */
public class ParamsEditor extends VBox {

  public ParamsEditor(RequestParams params) {
    NodeHelper.addClass(this, "params-editor");
    CheckBox dynamicCheckbox = new JFXCheckBox();
    dynamicCheckbox.setSelected(Boolean.TRUE.equals(params.getDynamic()));
    addItem("动态网页", dynamicCheckbox).selectedProperty().addListener((ov, o, n) -> params.setDynamic(n));

    // 代理支持
    // CheckBox enableProxyCheckbox = new JFXCheckBox();
    // enableProxyCheckbox.setSelected(Boolean.TRUE.equals(params.getEnableProxy()));
    // addItem("启用代理", enableProxyCheckbox).selectedProperty().addListener((ov, o, n) -> params.setEnableProxy(n));

    ComboBox<String> methodBox = new ComboBox<>(FXCollections.observableArrayList("GET", "POST"));
    methodBox.setValue(params.getMethod());
    methodBox.setEditable(true);
    addItem("请求方法", methodBox).valueProperty().addListener((ov, o, n) -> params.setMethod(n));

    ComboBox<String> charsetBox = new ComboBox<>(FXCollections.observableArrayList("UTF-8", "GBK", "GB2312"));
    charsetBox.setValue(params.getCharset());
    charsetBox.setEditable(true);
    addItem("编码格式", charsetBox).valueProperty().addListener((ov, o, n) -> params.setCharset(n));

    ComboBox<String> mediaTypeBox = new ComboBox<>(FXCollections.observableArrayList(MediaType.FORM.getMediaType(), MediaType.JSON.getMediaType()));
    mediaTypeBox.setValue(params.getMediaType());
    mediaTypeBox.setEditable(true);
    addItem("媒体类型", mediaTypeBox).valueProperty().addListener((ov, o, n) -> params.setMediaType(n));

    TextArea headerArea = new TextArea();
    headerArea.setPromptText("格式为  key: value ，一行一个");
    headerArea.setText(params.getHeaderString());
    addItem("请求头", headerArea).focusedProperty().addListener((ov, o, n) -> {
      if (Boolean.FALSE.equals(n) && StringUtils.isNotBlank(headerArea.getText())) {
        params.setHeaderString(headerArea.getText());
      }
    });
    TextField bodyField = new TextField(params.getBody());
    addItem("请求体", bodyField).focusedProperty().addListener(e -> {
      if (!bodyField.isFocused() && StringUtils.isNotBlank(bodyField.getText())) {
        params.setBody(bodyField.getText());
      }
    });
  }

  private <T extends Node> T addItem(String name, T content) {
    RuleItem item = new RuleItem();
    item.setName(name);
    item.getContent().add(content);
    getChildren().add(item);
    return content;
  }
}
