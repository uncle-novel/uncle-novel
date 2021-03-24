package com.unclezs.novel.app.jfx.framework.hotkey;

import com.unclezs.novel.app.jfx.framework.hotkey.listener.HotKeyListener;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import lombok.Getter;
import lombok.Setter;

import javax.swing.KeyStroke;
import java.util.Objects;

/**
 * 组合键
 *
 * @author blog.unclezs.com
 * @date 2021/03/09 8:22
 */
@Getter
@Setter
public class HotKeyCombination {
    /**
     * 组合键 文本
     */
    private String text;
    /**
     * 组合键
     */
    private KeyCodeCombination combination;
    /**
     * 触发后的动作
     */
    private HotKeyListener listener;

    public HotKeyCombination(KeyCode keyCode, KeyCombination.Modifier... modifiers) {
        this.combination = new KeyCodeCombination(keyCode, modifiers);
        // 生成快捷键组合文本
        StringBuilder keyStroke = new StringBuilder();
        for (KeyCombination.Modifier modifier : modifiers) {
            keyStroke.append(modifier.getKey().getName().toLowerCase()).append(" ");
        }
        keyStroke.append(keyCode.getName().toUpperCase());
        this.text = keyStroke.toString();
    }

    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(text);
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
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return text;
    }
}
