package com.unclezs.novel.app.framework;

import com.unclezs.novel.app.framework.view.View;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/4/4 22:39
 */
@UtilityClass
public class AppContext {

  public void createView(Class<? extends View> viewClass) {
    System.out.println("success");
  }
}
