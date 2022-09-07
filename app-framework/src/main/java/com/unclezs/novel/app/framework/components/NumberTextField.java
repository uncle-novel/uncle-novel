package com.unclezs.novel.app.framework.components;

import cn.hutool.core.util.NumberUtil;
import java.util.function.UnaryOperator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

/**
 * 限制输入数字的输入框
 *
 * @author blog.unclezs.com
 * @since 2021/4/21 23:43
 */
public class NumberTextField extends TextField {

  /**
   * 限制输入输入的formatter
   */
  public static final TextFormatter<Change> NUMBER = new TextFormatter<>(new NumberUnaryOperator());

  public NumberTextField() {
    this(null);
  }

  public NumberTextField(String text) {
    super(text);
    setTextFormatter(NUMBER);
  }

  /**
   * 限制输入数字
   */
  static class NumberUnaryOperator implements UnaryOperator<Change> {


    @Override
    public Change apply(Change change) {
      if (change.isDeleted()) {
        return change;
      }
      if (NumberUtil.isNumber(change.getText())) {
        return change;
      }
      return null;
    }
  }
}
