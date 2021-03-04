package com.uncles.novel.app.jfx.framework.i18n.bundle;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.NonNull;

import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * 118n工具
 *
 * @author blog.unclezs.com
 * @date 2021/03/03 23:47
 */
@SuppressWarnings("all")
public class JsonResourceBundle extends ResourceBundle {
    JSONObject props;

    public JsonResourceBundle(InputStream inputStream) {
        props = JSONUtil.parseObj(IoUtil.readUtf8(inputStream));
    }

    @Override
    public Object handleGetObject(@NonNull String key) {
        return props.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(props.keySet());
    }

    @Override
    public void setParent(ResourceBundle parent) {
        super.setParent(parent);
    }
}
