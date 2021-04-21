package com.unclezs.novel.app.main.home.views.widgets;

import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.main.home.views.FictionRulesView;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.EqualsAndHashCode;

/**
 * @author blog.unclezs.com
 * @date 2021/4/21 12:05
 */
@EqualsAndHashCode(callSuper = true)
@FxView(fxml = "/layout/home/views/widgets/rule-editor.fxml")
public class RuleEditorView extends SidebarView<StackPane> {

  public static final String BUNDLE_RULE_KEY = "rule";
  private final IntegerStringConverter intStrConverter = new IntegerStringConverter();
  public TextField searchList;
  public TextField searchDetailPage;

  private List<Property<?>> properties = new ArrayList<>();
  @FXML
  private TextField name;
  @FXML
  private TextField group;
  @FXML
  private TextField weight;
  @FXML
  private JFXCheckBox enabled;
  @FXML
  private ComboBox<String> searchParamMethod;
  @FXML
  private ComboBox<String> searchParamCharset;
  @FXML
  private TextArea searchParamHeaders;
  @FXML
  private TextField searchParamBody;
  @FXML
  private JFXCheckBox searchParamMockBrowser;

  private AnalyzerRule rule;

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    this.rule = bundle.get(BUNDLE_RULE_KEY);
    if (rule != null) {
      bindData();
    }
  }

  private void bindData() {
    // 基本信息
    bind(name, rule::getName, rule::setName);
    bind(group, rule::getGroup, rule::setGroup);
    bind(weight, weight.textProperty(), rule::getWeight, rule::setWeight, intStrConverter);
    bind(enabled, rule::isEnable, rule::setEnable);
    // 搜索规则
    RequestParams params = rule.getSearch().getParams();
    bind(searchParamMethod, searchParamMethod.valueProperty(), params::getMethod, params::setMethod, null);
    bind(searchParamCharset, searchParamCharset.valueProperty(), params::getCharset, params::setCharset, null);
    bind(searchParamHeaders, params::getHeaderString, params::setHeaderString);
    bind(searchParamBody, params::getBody, params::setBody);
    bind(searchParamMockBrowser, params::isDynamic, params::setDynamic);
  }

  @SuppressWarnings("unchecked")
  private void bind(CheckBox checkBox, BooleanSupplier getter, Consumer<Boolean> setter) {
    BooleanProperty property = checkBox.selectedProperty();
    ChangeListener<Boolean> listener = (ChangeListener<Boolean>) checkBox.getUserData();
    if (listener != null) {
      property.removeListener(listener);
    }
    // 初值
    property.setValue(getter.getAsBoolean());
    listener = (observableValue, oldValue, newValue) -> setter.accept(property.getValue());
    checkBox.setUserData(listener);
    property.addListener(listener);
  }

  private <T> void bind(TextInputControl field, Supplier<T> getter, Consumer<T> setter) {
    bind(field, field.textProperty(), getter, setter, null);
  }

  @SuppressWarnings("unchecked")
  private <T> void bind(Node node, Property<String> property, Supplier<T> getter, Consumer<T> setter, StringConverter<T> converter) {
    ChangeListener<String> listener = (ChangeListener<String>) node.getUserData();
    if (listener != null) {
      property.removeListener(listener);
    }
    // 初值
    T initValue = getter.get();
    String initStrValue = converter == null ? (String) initValue : converter.toString(initValue);
    property.setValue(initStrValue);
    listener = (observableValue, oldValue, newValue) -> {
      T value;
      if (converter == null) {
        value = (T) newValue;
      } else {
        value = converter.fromString(newValue);
      }
      setter.accept(value);
    };
    node.setUserData(listener);
    property.addListener(listener);
  }

  /**
   * 返回书源页面
   */
  @FXML
  private void goBack() {
    navigation.navigate(FictionRulesView.class);
  }

  @FXML
  private void save() {
    System.out.println(rule);
    goBack();
  }
}
