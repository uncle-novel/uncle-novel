package com.unclezs.novel.app.jfx.launcher;

import com.sun.javafx.application.ParametersImpl;

import java.util.Map;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 19:46
 */
public class FxLauncher {
    public static void main(String[] args) {
        ParametersImpl parameters = new ParametersImpl(args);
        Map<String, String> named = parameters.getNamed();
        System.out.println(named);
        System.out.println(parameters.getUnnamed());
        System.out.println(parameters.getRaw());
    }
}
