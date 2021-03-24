package com.unclezs.novel.app.jfx.app.converter;

import com.unclezs.novel.app.jfx.app.beans.MainBean;
import com.unclezs.novel.app.jfx.app.ui.MainViewModel;

/**
 * @author blog.unclezs.com
 * @since 2021/03/04 11:11
 */
public class MainConverter {

  public MainBean convert(MainViewModel mainViewModel) {
    MainBean mainBean = new MainBean();
    mainBean.setName(mainViewModel.getName());
    return mainBean;
  }
}
