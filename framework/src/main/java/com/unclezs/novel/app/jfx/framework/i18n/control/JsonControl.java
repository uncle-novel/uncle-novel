package com.unclezs.novel.app.jfx.framework.i18n.control;

import com.unclezs.novel.app.jfx.framework.i18n.bundle.JsonResourceBundle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 读取Json
 *
 * @author blog.unclezs.com
 * @date 2021/03/04 0:13
 */
public class JsonControl extends ResourceBundle.Control {
    public static final JsonControl ME = new JsonControl();

    public static final String JSON = "json";
    public static final List<String> FORMAT = Collections.singletonList("json");


    private JsonControl() {

    }

    @Override
    public List<String> getFormats(String baseName) {
        if (baseName == null) {
            throw new NullPointerException();
        }
        return FORMAT;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
        if (baseName == null || locale == null || format == null || loader == null) {
            throw new NullPointerException();
        }
        ResourceBundle bundle = null;
        if (JSON.equals(format)) {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, format);
            InputStream stream = loader.getResourceAsStream(resourceName);
            if (stream != null) {
                BufferedInputStream bis = new BufferedInputStream(stream);
                bundle = new JsonResourceBundle(bis);
                bis.close();
            }
        }
        return bundle;
    }
}
