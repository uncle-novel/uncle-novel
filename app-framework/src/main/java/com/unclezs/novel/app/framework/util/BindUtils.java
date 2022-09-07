package com.unclezs.novel.app.framework.util;

import lombok.experimental.UtilityClass;

/**
 * 绑定工具
 *
 * @author blog.unclezs.com
 * @since 2021/4/21 20:15
 */
@UtilityClass
public class BindUtils {

//  public static void binds(Property<String> property, Supplier<String> getter, Consumer<String> setter) {
//    SimpleStringProperty bindProperty = new SimpleStringProperty(getter.get());
//    property.bindBidirectional(bindProperty);
//    bindProperty.addListener(e -> setter.accept(bindProperty.get()));
//  }
//
//  public static void bind(Property<String> property, Supplier<Integer> getter, Consumer<Integer> setter) {
//    Integer initValue = getter.get();
//    SimpleStringProperty bindProperty = new SimpleStringProperty(initValue == null ? null : String.valueOf(initValue));
//    property.bindBidirectional(bindProperty);
//    bindProperty.addListener(e -> setter.accept(Integer.parseInt(bindProperty.get())));
//  }

}
