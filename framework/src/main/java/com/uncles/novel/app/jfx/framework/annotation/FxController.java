package com.uncles.novel.app.jfx.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fxml控制器类
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 14:42
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FxController {
    /**
     * FXML路径
     *
     * @return fxml路径
     */
    String fxml() default "";

    /**
     * 国际化资源文件
     *
     * @return 路径
     */
    String bundle() default "";
}
