package com.uncles.novel.app.jfx.ui.controller;

import com.uncles.novel.app.jfx.framework.annotation.FxController;
import com.uncles.novel.app.jfx.framework.controller.BaseFxController;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 15:23
 */
@FxController(bundle = "basic")
public class MainController extends BaseFxController {

    public void print() {
        System.out.println(str("app_name"));
    }
}
