package com.unclezs.novel.app.main.ui.home.views;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.google.gson.JsonSyntaxException;
import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.helper.RuleTester;
import com.unclezs.novel.analyzer.core.matcher.matchers.DefaultTextMatcher;
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
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.main.manager.RuleManager;
import com.unclezs.novel.app.main.ui.home.views.widgets.rule.CommonRuleEditor;
import com.unclezs.novel.app.main.ui.home.views.widgets.rule.RuleItem;
import com.unclezs.novel.app.main.ui.home.views.widgets.rule.RuleItems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
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
import lombok.extern.slf4j.Slf4j;

/**
 * 书源编辑页面
 *
 * @author blog.unclezs.com
 * @date 2021/4/21 12:05
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@FxView(fxml = "/layout/home/views/widgets/rule-editor.fxml")
public class RuleEditorView extends SidebarView<StackPane> {

  public static final String BUNDLE_RULE_KEY = "rule";
  private static final String SELECTOR_CHECK_BOX = ".check-box";
  private static final String SELECTOR_COMBO_BOX = ".combo-box";
  private static final String SELECTOR_TEXT_INPUT = ".item > .text-input";
  private static final String SELECTOR_INPUT_BOX = ".input-box";
  private final IntegerStringConverter intStrConverter = new IntegerStringConverter();
  /**
   * 监听器
   */
  private final Map<ReadOnlyProperty<?>, InvalidationListener> listeners = new HashMap<>();
  /**
   * 通用组件
   */
  private final List<InputBox> inputBoxes = new ArrayList<>();
  private final List<TextInputControl> inputs = new ArrayList<>();
  private final List<CheckBox> checkBoxes = new ArrayList<>();
  private final List<ComboBox<String>> comboBoxes = new ArrayList<>();
  /**
   * 正文解析规则输入框
   */
  @FXML
  private InputBox contentRule;
  /**
   * 自动解析模式下拉选择
   */
  @FXML
  private ComboBox<String> autoAnalysisMode;
  @FXML
  private RuleItems infoItemsPanel;
  @FXML
  private TextArea sourceEditor;
  @FXML
  private ScrollPane panel;
  @FXML
  private IconButton showSourceButton;
  @FXML
  private VBox ruleContainer;
  @FXML
  private TextField weight;
  @FXML
  private TextArea searchParamHeaders;
  private CommonRuleEditor editor;
  private VBox debugContentPanel;
  private VBox debugTocPanel;
  private VBox debugDetailPanel;
  private VBox debugSearchPanel;
  private VBox debugPanel;
  /**
   * 用于数据绑定
   */
  private AnalyzerRule rule;
  /**
   * 真实规则数据
   */
  private AnalyzerRule realRule;
  /**
   * 是否来自书源管理页面
   */
  private boolean fromManager = false;
  /**
   * 来自哪个页面
   */
  private SidebarView<?> from;
  /**
   * 保存到书源（解析下载 自动新增时显示）
   */
  private RuleItem saveToRule;
  /**
   * 保存到书源的开关
   */
  private JFXCheckBox saveToRulesSwitch;

  /**
   * 创建时获取相关组件
   */
  @Override
  @SuppressWarnings("unchecked")
  public void onCreate() {
    ruleContainer.lookupAll(SELECTOR_TEXT_INPUT).stream().filter(node -> node.getUserData() != null).forEach(node -> inputs.add((TextInputControl) node));
    ruleContainer.lookupAll(SELECTOR_INPUT_BOX).stream().filter(node -> node.getUserData() != null).forEach(node -> inputBoxes.add((InputBox) node));
    ruleContainer.lookupAll(SELECTOR_CHECK_BOX).stream().filter(node -> node.getUserData() != null).forEach(node -> checkBoxes.add((CheckBox) node));
    ruleContainer.lookupAll(SELECTOR_COMBO_BOX).stream().filter(node -> node.getUserData() != null).forEach(node -> comboBoxes.add((ComboBox<String>) node));

    autoAnalysisMode.valueProperty().addListener(e -> {
      int mode = autoAnalysisMode.getItems().indexOf(autoAnalysisMode.getValue()) + 1;
      contentRule.getInput().setText(DefaultTextMatcher.DEFAULT_RULE_TYPE + mode);
      rule.getContent().setContent(CommonRule.create(DefaultTextMatcher.DEFAULT_RULE_TYPE, String.valueOf(mode)));
    });
  }

  /**
   * 显示时，如果存在规则，则进行数据绑定
   *
   * @param bundle 页面跳转数据
   */
  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    this.fromManager = bundle.getFrom().equals(RuleManagerView.class.getName());
    this.from = AppContext.getView(bundle.getFrom());
    realRule = bundle.get(BUNDLE_RULE_KEY);
    if (realRule == null) {
      rule = new AnalyzerRule();
    } else {
      rule = realRule.copy();
    }
    addSaveToRuleItem();
    bindData();
    // 绑定后会自动生成默认数据，保持一致
    if (!Objects.equals(rule, realRule)) {
      BeanUtil.copyProperties(rule, realRule);
    }
  }

  private void addSaveToRuleItem() {
    if (saveToRule != null) {
      infoItemsPanel.getItems().remove(saveToRule);
    }
    if (!fromManager && !RuleManager.exist(rule)) {
      if (saveToRule == null) {
        saveToRule = new RuleItem();
        saveToRule.setName("保存为书源");
        saveToRulesSwitch = new JFXCheckBox();
        saveToRulesSwitch.setSelected(true);
        saveToRule.getChildren().add(saveToRulesSwitch);
      }
      infoItemsPanel.getItems().add(saveToRule);
    }
  }

  /**
   * 返回书源页面，重置初始状态，移除监听。直接调用则丢弃修改。
   */
  private void back() {
    SidebarNavigateBundle bundle = new SidebarNavigateBundle();
    // 新增时回传
    if (realRule == null) {
      bundle.put(BUNDLE_RULE_KEY, rule.copy());
    }
    navigation.navigate(from, bundle);
    reset();
    this.realRule = null;
    this.rule = null;
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
    debugContentPanel = null;
    debugDetailPanel = null;
    debugSearchPanel = null;
    debugTocPanel = null;
    debugPanel = null;
  }

  @FXML
  public void debugRule() {
    if (debugPanel == null) {
      debugPanel = createDebugBox("请输入小说名称", RuleTester::test);
    }
    ModalBox.none().body(debugPanel).title("规则测试").show();
  }

  /**
   * 调试搜索规则
   */
  @FXML
  public void debugSearchRule() {
    if (debugSearchPanel == null) {
      debugSearchPanel = createDebugBox("请输入小说名称", RuleTester::search);
    }
    ModalBox.none().body(debugSearchPanel).title("小说搜索规则测试").show();
  }

  /**
   * 调试目录规则
   */
  @FXML
  public void debugTocRule() {
    if (debugTocPanel == null) {
      debugTocPanel = createDebugBox("请输入目录链接", RuleTester::toc);
    }
    ModalBox.none().body(debugTocPanel).title("目录规则测试").show();
  }

  /**
   * 调试详情规则
   */
  @FXML
  public void debugDetailRule() {
    if (debugDetailPanel == null) {
      debugDetailPanel = createDebugBox("请输入目录链接", RuleTester::detail);
    }
    ModalBox.none().body(debugDetailPanel).title("详情规则测试").show();
  }

  /**
   * 调试正文规则
   */
  @FXML
  public void debugContentRule() {
    if (debugContentPanel == null) {
      debugContentPanel = createDebugBox("请输入正文链接", RuleTester::content);
    }
    ModalBox.none().body(debugContentPanel).title("正文规则测试").show();
  }

  /**
   * 创建测试容器
   *
   * @param promptText 提示文字
   * @param starter    执行器
   * @return 容器
   */
  private VBox createDebugBox(String promptText, BiConsumer<RuleTester, String> starter) {
    TextArea console = new TextArea();
    console.setWrapText(true);
    VBox debugBox = new VBox();
    debugBox.setSpacing(10);
    InputBox inputBox = new InputBox();
    inputBox.setIcon(IconFont.START.name());
    inputBox.setPrompt(promptText);
    AtomicBoolean running = new AtomicBoolean(false);
    inputBox.setOnIconClicked(e -> {
      if (running.get()) {
        Toast.info((StackPane) debugBox.getParent(), "正在测试中，请等待测试完成");
        return;
      }
      running.set(true);
      console.clear();
      Executor.run(() -> {
        try {
          RuleTester tester = new RuleTester(rule.copy(), msg -> Executor.runFx(() -> console.appendText(msg)));
          starter.accept(tester, e.getInput());
        } catch (Exception exception) {
          Executor.runFx(() -> console.appendText(ExceptionUtil.getSimpleMessage(exception)));
          log.warn("调试解析规则错误", exception);
          exception.printStackTrace();
        } finally {
          running.set(false);
        }
      });
    });
    debugBox.getChildren().setAll(inputBox, console);
    return debugBox;
  }

  /**
   * 返回书源页面，重置初始状态，移除监听。直接调用则丢弃修改。
   */
  @FXML
  private void goBack() {
    if (Objects.equals(rule, realRule)) {
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
   * 保存修改
   */
  @FXML
  private void save() {
    saveSource();
    if (realRule != null) {
      BeanUtil.copyProperties(rule, realRule, CopyOptions.create().ignoreNullValue());
    }
    if (!fromManager && saveToRulesSwitch != null && saveToRulesSwitch.isSelected()) {
      RuleManager.addRule(realRule);
    }
    back();
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
        editor = new CommonRuleEditor();
      }
      editor.setRule(finalRuleItem);
      AtomicBoolean success = new AtomicBoolean(false);
      ModalBox.confirm(save -> {
        success.set(true);
        if (Boolean.TRUE.equals(save)) {
          CommonRule commonRule;
          try {
            commonRule = editor.getRule();
            BeanUtil.copyProperties(commonRule, finalRuleItem);
            field.getInput().setText(finalRuleItem.ruleString());
          } catch (JsonSyntaxException e) {
            Toast.error((StackPane) editor.getParent(), "格式错误！");
            log.warn("规则JSON格式错误：规则：{} ，JSON：\n{}", field.getUserData(), editor.getJson(), e);
            success.set(false);
          } catch (Exception e) {
            Toast.error((StackPane) editor.getParent(), "未知错误！");
            log.error("规则保存失败：规则：{} ，JSON：\n{}", field.getUserData(), editor.getJson(), e);
            success.set(false);
          }
        }
      }).body(editor).title("编辑规则").success(success::get).show();
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
      if (Boolean.FALSE.equals(property.get()) && !Objects.equals(propertyGetter.get(), getter.get())) {
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
