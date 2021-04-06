package com.unclezs.novel.app.framework.i18n.control;

import com.unclezs.novel.app.framework.i18n.LanguageUtils;
import com.unclezs.novel.app.framework.i18n.bundle.XmlResourceBundle;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import lombok.NonNull;

/**
 * 读取Xml
 *
 * @author blog.unclezs.com
 * @date 2021/03/04 0:13
 */
public class XmlControl extends ResourceBundle.Control {

  public static final XmlControl ME = new XmlControl();

  public static final String FLAG = "xml";
  public static final List<String> FORMAT = Collections.singletonList(FLAG);


  private XmlControl() {

  }

  @Override
  public List<String> getFormats(String baseName) {
    if (baseName == null) {
      throw new NullPointerException();
    }
    return FORMAT;
  }

  @Override
  public ResourceBundle newBundle(@NonNull String baseName, @NonNull Locale locale,
      @NonNull String format, @NonNull ClassLoader loader, boolean reload) throws IOException {
    ResourceBundle bundle = null;
    if (FLAG.equals(format)) {
      if (LanguageUtils.isChineseSimple()) {
        return new XmlResourceBundle();
      }
      String resourceName = toResourceName(toBundleName(baseName, locale), format);
      InputStream stream = loader.getResourceAsStream(resourceName);
      if (stream != null) {
        BufferedInputStream bis = new BufferedInputStream(stream);
        bundle = new XmlResourceBundle(bis);
        bis.close();
      }
    }
    return bundle;
  }
}
