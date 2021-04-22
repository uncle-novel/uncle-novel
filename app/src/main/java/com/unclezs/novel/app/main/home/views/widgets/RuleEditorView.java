package com.unclezs.novel.app.main.home.views.widgets;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.model.SearchRule;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.InputBox;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.main.home.views.RuleManagerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
  public static final String SELECTOR_CHECK_BOX = ".check-box";
  public static final String SELECTOR_COMBO_BOX = ".combo-box";
  public static final String SELECTOR_TEXT_INPUT = ".item > .text-input";
  public static final String SELECTOR_INPUT_BOX = ".input-box";
  private final IntegerStringConverter intStrConverter = new IntegerStringConverter();
  private final Map<ReadOnlyProperty<?>, InvalidationListener> listeners = new HashMap<>();
  /**
   * 通用组件
   */
  private final List<InputBox> inputBoxes = new ArrayList<>();
  private final List<TextInputControl> inputs = new ArrayList<>();
  private final List<CheckBox> checkBoxes = new ArrayList<>();
  private final List<ComboBox<String>> comboBoxes = new ArrayList<>();
  public TextArea sourceEditor;
  public ScrollPane panel;
  @FXML
  private IconButton showSourceButton;
  @FXML
  private VBox ruleContainer;
  @FXML
  private TextField weight;
  @FXML
  private TextArea searchParamHeaders;
  private TextArea editor;
  /**
   * 用于数据绑定
   */
  private AnalyzerRule rule;
  /**
   * 真实规则数据
   */
  private AnalyzerRule realRule;

  /**
   * 创建时获取相关组件
   */
  @Override
  @SuppressWarnings("unchecked")
  public void onCreate() {
    ruleContainer.lookupAll(SELECTOR_TEXT_INPUT).stream()
      .filter(node -> node.getUserData() != null)
      .forEach(node -> inputs.add((TextInputControl) node));
    ruleContainer.lookupAll(SELECTOR_INPUT_BOX).forEach(node -> inputBoxes.add((InputBox) node));
    ruleContainer.lookupAll(SELECTOR_CHECK_BOX).forEach(node -> checkBoxes.add((CheckBox) node));
    ruleContainer.lookupAll(SELECTOR_COMBO_BOX).forEach(node -> comboBoxes.add((ComboBox<String>) node));
  }

  /**
   * 显示时，如果存在规则，则进行数据绑定
   *
   * @param bundle 页面跳转数据
   */
  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    this.realRule = bundle.get(BUNDLE_RULE_KEY);
    if (realRule != null) {
      rule = realRule.copy();
      bindData();
    }
  }

  /**
   * 返回书源页面，重置初始状态，移除监听。直接调用则丢弃修改。
   */
  @FXML
  private void goBack() {
    if (rule.equals(realRule)) {
      back();
    } else {
      ModalBox.confirm(confirm -> {
        if (Boolean.TRUE.equals(confirm)) {
          back();
        }
      }).message("将会丢失全部修改！").show();
    }
  }

  /**
   * 返回书源页面，重置初始状态，移除监听。直接调用则丢弃修改。
   */
  private void back() {
    navigation.navigate(RuleManagerView.class);
    reset();
  }

  /**
   * 重置到初始状态
   */
  private void reset() {
    // 移除监听
    listeners.forEach(Observable::removeListener);
    listeners.clear();
    // 重置为初始状态
    inputBoxes.forEach(box -> box.getInput().setText(null));
    inputs.forEach(input -> input.setText(null));
    checkBoxes.forEach(box -> box.setSelected(false));
    comboBoxes.forEach(box -> box.setValue(null));
  }

  /**
   * 保存修改
   */
  @FXML
  private void save() {
    if (rule.isEffective()) {
      saveSource();
      BeanUtil.copyProperties(rule, realRule, CopyOptions.create().ignoreNullValue());
      back();
    } else {
      Toast.error(getRoot(), "规则不合法！");
    }
  }

  /**
   * 书源模式切换
   */
  @FXML
  private void showSource() {
    if (sourceEditor == null) {
      sourceEditor = new TextArea();
    }
    if (isSourceMode()) {
      saveSource();
      sourceEditor.setText(null);
      showSourceButton.setText("源码");
      panel.setContent(ruleContainer);
    } else {
      showSourceButton.setText("规则");
      sourceEditor.setText(GsonUtils.PRETTY.toJson(rule));
      panel.setContent(sourceEditor);
    }
  }

  /**
   * 保存源码
   */
  private void saveSource() {
    if (isSourceMode()) {
      reset();
      rule = RuleHelper.parseRule(sourceEditor.getText(), AnalyzerRule.class);
      bindData();
    }
  }

  /**
   * 是否为源码模式
   *
   * @return true 源码模式
   */
  private boolean isSourceMode() {
    return panel.getContent() == sourceEditor;
  }


  /**
   * 组件与规则Bean的数据监听绑定
   */
  private void bindData() {
    // 特殊数据处理
    bind(weight.focusedProperty(), weight::getText, weight::setText, rule::getWeight, rule::setWeight, intStrConverter);
    // 搜索规则
    SearchRule search = rule.getSearch();
    if (search == null) {
      search = new SearchRule();
      rule.setSearch(search);
    }
    RequestParams params = search.getParams();
    if (params == null) {
      params = new RequestParams();
      search.setParams(params);
    }
    bind(searchParamHeaders, params::getHeaderString, params::setHeaderString);
    // 通用数据绑定
    inputBoxes.forEach(this::bind);
    inputs.forEach(this::bind);
    checkBoxes.forEach(this::bind);
    comboBoxes.forEach(this::bind);
  }

  /**
   * 绑定 CheckBox ，选中状态绑定
   *
   * @param checkBox 勾选框
   */
  private void bind(CheckBox checkBox) {
    String expression = checkBox.getUserData().toString();
    BooleanProperty property = checkBox.selectedProperty();
    property.set(BeanUtil.getProperty(rule, expression));
    InvalidationListener listener = e -> BeanUtil.setProperty(rule, expression, property.getValue());
    property.addListener(listener);
    listeners.put(property, listener);
  }

  /**
   * 绑定 InputBox的TextField, 焦点消失且数据改变时触发
   *
   * @param field InputBox
   */
  private void bind(InputBox field) {
    BeanPath resolver = new BeanPath(field.getUserData().toString());
    CommonRule ruleItem = (CommonRule) resolver.get(rule);
    if (ruleItem == null) {
      ruleItem = new CommonRule();
      resolver.set(rule, ruleItem);
    }
    // 点击编辑JSON
    CommonRule finalRuleItem = ruleItem;
    field.setOnIconClicked(event -> {
      if (editor == null) {
        editor = new TextArea();
      }
      editor.setText(GsonUtils.PRETTY.toJson(finalRuleItem));
      ModalBox.confirm(save -> {
        if (Boolean.TRUE.equals(save)) {
          CommonRule commonRule = GsonUtils.parse(editor.getText(), CommonRule.class);
          BeanUtil.copyProperties(commonRule, finalRuleItem);
          field.getInput().setText(finalRuleItem.ruleString());
        }
      }).body(editor).title("编辑规则").show();
    });
    bind(field.getInput(), CommonRule.ruleStringGetter(ruleItem), CommonRule.ruleStringSetter(ruleItem));
  }

  /**
   * 绑定 TextInputControl, 焦点消失且数据改变时触发
   *
   * @param field TextInputControl
   */
  private void bind(TextInputControl field) {
    String expression = field.getUserData().toString();
    bind(field.focusedProperty(), field::getText, field::setText, () -> BeanUtil.getProperty(rule, expression), value -> BeanUtil.setProperty(rule, expression, value), null);
  }

  /**
   * 数据绑定
   *
   * @param field ComboBox
   */
  private void bind(ComboBox<String> field) {
    String expression = field.getUserData().toString();
    bind(field.focusedProperty(), field::getValue, field::setValue, () -> BeanUtil.getProperty(rule, expression), value -> BeanUtil.setProperty(rule, expression, value), null);
  }

  /**
   * 数据绑定
   *
   * @param field  TextInputControl
   * @param getter 属性改变后JavaBean的getter
   * @param setter 属性改变后JavaBean的setter
   * @param <T>    JavaBean类型
   */
  private <T> void bind(TextInputControl field, Supplier<T> getter, Consumer<T> setter) {
    bind(field.focusedProperty(), field::getText, field::setText, getter, setter, null);
  }

  /**
   * 数据绑定
   *
   * @param property       要监听的属性
   * @param propertyGetter 属性的getter
   * @param propertySetter 属性的setter
   * @param getter         属性改变后JavaBean的getter
   * @param setter         属性改变后JavaBean的setter
   * @param converter      字符串转换器
   * @param <T>            JavaBean类型
   */
  @SuppressWarnings("unchecked")
  private <T> void bind(ReadOnlyBooleanProperty property, Supplier<String> propertyGetter, Consumer<String> propertySetter, Supplier<T> getter, Consumer<T> setter, StringConverter<T> converter) {
    // 初值
    T initValue = getter.get();
    String initStrValue = converter == null ? (String) initValue : converter.toString(initValue);
    propertySetter.accept(initStrValue);
    // 单向监听
    InvalidationListener listener = e -> {
      if (!property.get() && !Objects.equals(propertyGetter.get(), getter.get())) {
        T value;
        if (converter == null) {
          value = (T) propertyGetter.get();
        } else {
          value = converter.fromString(propertyGetter.get());
        }
        setter.accept(value);
      }
    };
    property.addListener(listener);
    listeners.put(property, listener);
  }
}
