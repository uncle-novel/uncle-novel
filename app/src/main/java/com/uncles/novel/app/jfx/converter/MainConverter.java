package com.uncles.novel.app.jfx.converter;

import com.uncles.novel.app.jfx.beans.MainBean;
import com.uncles.novel.app.jfx.ui.MainViewModel;

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
