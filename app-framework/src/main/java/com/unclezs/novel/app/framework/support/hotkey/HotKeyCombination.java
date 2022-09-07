package com.unclezs.novel.app.framework.support.hotkey;

import com.unclezs.novel.analyzer.util.StringUtils;
import java.util.Objects;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javax.swing.KeyStroke;
import lombok.Getter;
import lombok.Setter;

/**
 * 组合键
 *
 * @author blog.unclezs.com
 * @since 2021/03/09 8:22
 */
@Getter
@Setter
public class HotKeyCombination {

  /**
   * 组合键 文本
   */
  private String stroke;
  /**
   * 组合键
   */
  private KeyCombination combination;

  public HotKeyCombination(KeyCode keyCode, KeyCombination.Modifier... modifiers) {
    this.combination = new KeyCodeCombination(keyCode, modifiers);
    // 生成快捷键组合文本
    StringBuilder keyStroke = new StringBuilder();
    for (KeyCombination.Modifier modifier : modifiers) {
      keyStroke.append(modifier.getKey().getName().toLowerCase()).append(" ");
    }
    keyStroke.append(keyCode.getName().toUpperCase());
    this.stroke = keyStroke.toString();
  }

  private HotKeyCombination() {

  }

  public static HotKeyCombination fromStroke(String stroke) {
    if (StringUtils.isBlank(stroke)) {
      return null;
    }
    HotKeyCombination hotKeyCombination = new HotKeyCombination();
    hotKeyCombination.setCombination(KeyCombination.keyCombination(stroke));
    hotKeyCombination.setStroke(stroke);
    return hotKeyCombination;
  }

  public KeyStroke getKeyStroke() {
    return KeyStroke.getKeyStroke(stroke);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotKeyCombination that = (HotKeyCombination) o;
    return Objects.equals(stroke, that.stroke);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stroke);
  }

  @Override
  public String toString() {
    return stroke;
  }
}
