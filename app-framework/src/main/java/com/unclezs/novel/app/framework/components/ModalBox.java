package com.unclezs.novel.app.framework.components;

import cn.hutool.core.text.CharSequenceUtil;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.support.LocalizedSupport;
import com.unclezs.novel.app.framework.util.NodeHelper;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

/**
 * 模态框
 *
 * @author unclezs.com
 * @date 2019.07.03 14:07
 */
public class ModalBox extends JFXAlert<Object> implements LocalizedSupport {

  public static final String BUNDLE_NAME = "widgets.modal";
  private final JFXDialogLayout layout;
  private final JFXButton cancel;
  private JFXButton submit;
  private Icon icon;
  /**
   * 是否成功回调
   */
  private BooleanSupplier successStateSupplier;

  private ModalBox() {
    this(AppContext.getInstance().getPrimaryStage());
  }

  private ModalBox(Type type, String titleKey) {
    this();
    this.layout.getStyleClass().add(type.name().toLowerCase());
    this.icon = NodeHelper.addClass(new Icon(type.iconFont), "message-icon");
    if (titleKey != null) {
      this.title(localized(titleKey));
    }
  }

  private ModalBox(Window window) {
    super(window);
    this.layout = new JFXDialogLayout();
    this.cancel = new JFXButton(localized("cancel"));
    this.cancel.setOnMouseClicked(event -> closeModal());
    this.layout.getActions().add(cancel);
    this.setOverlayClose(false);
    setContent(layout);
  }

  /**
   * 模态框
   *
   * @return ModalBox.this
   */
  public static ModalBox none() {
    return new ModalBox(Type.NONE, null);
  }

  /**
   * 信息模态框
   *
   * @return ModalBox.this
   */
  public static ModalBox info() {
    return new ModalBox(Type.INFO, "info");
  }

  /**
   * 警告模态框
   *
   * @return ModalBox.this
   */
  public static ModalBox warn() {
    return new ModalBox(Type.WARN, "warn");
  }

  /**
   * 错误模态框
   *
   * @return ModalBox.this
   */
  public static ModalBox error() {
    return new ModalBox(Type.ERROR, "error");
  }

  /**
   * 成功模态框
   *
   * @return ModalBox.this
   */
  public static ModalBox success() {
    return new ModalBox(Type.SUCCESS, "success");
  }

  /**
   * 确定弹窗
   *
   * @param callback 回调
   * @return this
   */
  public static ModalBox confirm(Consumer<Boolean> callback) {
    return new ModalBox(Type.CONFIRM, "confirm").createConfirm(callback);
  }

  /**
   * 输入框
   *
   * @param callback 回调
   * @return this
   */
  public static ModalBox input(Consumer<String> callback) {
    return input(CharSequenceUtil.EMPTY, CharSequenceUtil.EMPTY, callback);
  }

  /**
   * 输入框
   *
   * @param prompt   提示信息
   * @param callback 回调
   * @return this
   */
  public static ModalBox input(String prompt, Consumer<String> callback) {
    return input(CharSequenceUtil.EMPTY, prompt, callback);
  }

  /**
   * 输入框
   *
   * @param init     初始值
   * @param prompt   提示信息
   * @param callback 回调
   * @return this
   */
  public static ModalBox input(String init, String prompt, Consumer<String> callback) {
    return new ModalBox().createInput(init, prompt, callback);
  }

  /**
   * 提示消息
   *
   * @param msg 消息内容
   * @return this
   */
  public ModalBox message(String msg) {
    return message(msg, null);
  }

  /**
   * 提示消息
   *
   * @param msg 消息内容
   * @return this
   */
  public ModalBox message(String msg, Icon icon) {
    if (icon != null) {
      this.icon = icon;
    }
    Label msgLabel = NodeHelper.addClass(new Label(msg, this.icon));
    this.layout.setBody(msgLabel);
    return this;
  }

  /**
   * 提示消息
   *
   * @param node 显示内容
   * @return this
   */
  public ModalBox body(Node node) {
    this.layout.setBody(node);
    return this;
  }

  /**
   * 设置标题
   *
   * @param title 标题
   */
  public ModalBox title(String title) {
    layout.setHeading(new Label(title));
    return this;
  }

  /**
   * 取消文字
   *
   * @param successStateSupplier 返回false则阻止窗口关闭
   * @return this
   */
  public ModalBox success(BooleanSupplier successStateSupplier) {
    this.successStateSupplier = successStateSupplier;
    return this;
  }

  /**
   * 取消文字
   *
   * @param text 取消文字
   * @return this
   */
  public ModalBox cancel(String text) {
    this.cancel.setText(text);
    return this;
  }

  /**
   * 确定按钮
   *
   * @param text 按钮文字
   * @return 提交
   */
  public ModalBox submit(String text) {
    if (CharSequenceUtil.isNotBlank(text)) {
      if (submit == null) {
        this.submit = NodeHelper.addClass(new IconButton(text), "btn-submit");
        layout.getActions().add(0, submit);
      } else {
        submit.setText(text);
      }
    }
    return this;
  }

  /**
   * 输入框
   *
   * @param init     初始值
   * @param prompt   提示信息
   * @param callback 回调
   * @return this
   */
  private ModalBox createInput(String init, String prompt, Consumer<String> callback) {
    TextField input = new TextField(init);
    if (CharSequenceUtil.isBlank(prompt)) {
      input.setPromptText(localized("input.prompt"));
    }
    submitButton().setOnMouseClicked(event -> {
      callback.accept(input.getText());
      closeModal();
    });
    this.layout.setBody(input);
    return this;
  }

  /**
   * 确定弹窗
   *
   * @param callback 回调
   * @return this
   */
  private ModalBox createConfirm(Consumer<Boolean> callback) {
    submitButton().setOnMouseClicked(event -> {
      callback.accept(true);
      closeModal();
    });
    cancel.setOnMouseClicked(event -> {
      callback.accept(false);
      closeModal();
    });
    return this;
  }

  /**
   * 获取确定按钮，不存在则创建
   *
   * @return 确定按钮
   */
  private JFXButton submitButton() {
    if (submit == null) {
      submit(localized("submit"));
    }
    return submit;
  }

  public void closeModal() {
    if (successStateSupplier != null && !successStateSupplier.getAsBoolean()) {
      return;
    }
    hideWithAnimation();
  }

  @Override
  public String getBundleName() {
    return BUNDLE_NAME;
  }

  /**
   * 弹窗类型
   */
  public enum Type {
    /**
     * 错误
     */
    ERROR(IconFont.ERROR),
    /**
     * 警告
     */
    WARN(IconFont.WARN),
    /**
     * 成功
     */
    SUCCESS(IconFont.SUCCESS),
    /**
     * 信息
     */
    INFO(IconFont.INFO),
    /**
     * 确认
     */
    CONFIRM(IconFont.CONFIRM),
    /**
     * 无图标
     */
    NONE(null);

    IconFont iconFont;

    Type(IconFont iconFont) {
      this.iconFont = iconFont;
    }
  }
}
