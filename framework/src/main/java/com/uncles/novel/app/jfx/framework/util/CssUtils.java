package com.uncles.novel.app.jfx.framework.util;

import com.sun.javafx.css.StyleManager;
import javafx.css.CssParser;
import javafx.css.StyleOrigin;
import javafx.css.Stylesheet;
import javafx.scene.Scene;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/03/02 21:51
 */
@UtilityClass
public class CssUtils {
    public static void addStylesheet(Scene scene, String css) {
        Stylesheet stylesheet = new CssParser().parse(css);
        stylesheet.setOrigin(StyleOrigin.USER);
        StyleManager.getInstance().addUserAgentStylesheet(scene, stylesheet);
    }
}
