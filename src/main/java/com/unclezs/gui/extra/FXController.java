package com.unclezs.gui.extra;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置com.unclezs.utils.FxmlUtil使用
 *
 * @author uncle
 * @date 2020/4/18 10:17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXController {
    /**
     * FXML位置
     *
     * @return /
     */
    String value();

    /**
     * 前缀
     *
     * @return /
     */
    String prefix() default "/fxml/";

    /**
     * 后缀
     *
     * @return /
     */
    String suffix() default ".fxml";
}
