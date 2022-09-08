package com.unclezs.gui.utils;

import cn.hutool.core.lang.Dict;
import com.unclezs.utils.FileUtil;
import com.unclezs.utils.TemplateUtil;
import javafx.scene.Scene;
import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * @author uncle
 * @date 2020/5/16 17:53
 */
@UtilityClass
public class ThemeUtil {
    /**
     * 切换主题
     */
    public void setCss(Dict dict, Scene scene, String templates, String out) {
        File css = FileUtil.currentDirFile(out);
        if (!css.exists()) {
            cn.hutool.core.io.FileUtil.touch(css);
        } else {
            FileUtil.deleteForce(css);
        }
        TemplateUtil.process(dict, templates, css);
        if (scene.getStylesheets().size() == 1) {
            scene.getStylesheets().add(1, css.toURI().toString());
        } else {
            scene.getStylesheets().set(1, css.toURI().toString());
        }
    }
}
