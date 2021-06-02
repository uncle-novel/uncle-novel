package com.unclezs.novel.app.main.views.reader.widgets;

import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.views.reader.ReaderView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * @author blog.unclezs.com
 * @date 2021/5/9 15:40
 */
public class ReaderContextMenu extends ContextMenu {

  private final ReaderView readerView;
  private final MenuItem hideHeader;
  private final MenuItem windowTop;

  public ReaderContextMenu() {
    this.readerView = AppContext.getView(ReaderView.class);
    createMenuItem("下一章", IconFont.NEXT).setOnAction(event -> readerView.nextChapter());
    createMenuItem("上一章", IconFont.PRE).setOnAction(event -> readerView.preChapter());

    hideHeader = createMenuItem("隐藏状态栏", IconFont.HIDE);
    hideHeader.setOnAction(event -> toggleHeader());

    windowTop = createMenuItem("窗口置顶", IconFont.TOP);
    windowTop.setOnAction(event -> toggleWindowTop());

    createMenuItem("退出", IconFont.EXIT).setOnAction(event -> this.exit());
  }

  private MenuItem createMenuItem(String text, IconFont iconFont) {
    MenuItem menuItem = new MenuItem(text);
    Icon icon = new Icon(iconFont);
    menuItem.setGraphic(icon);
    getItems().add(menuItem);
    return menuItem;
  }


  /**
   * 隐藏/显示状态栏
   */
  public void toggleHeader() {
    toggleHeader(!readerView.getRoot().isHeaderHidden());
  }

  /**
   * 隐藏/显示状态栏
   *
   * @param showing 是否正在显示
   */
  public void toggleHeader(boolean showing) {
    Icon icon = (Icon) windowTop.getGraphic();
    if (showing) {
      SettingManager.manager().getReader().setShowHeader(false);
      hideHeader.setText("显示状态栏");
      icon.setValue(IconFont.SHOW);
      readerView.getRoot().hideHeader();
    } else {
      hideHeader.setText("隐藏状态栏");
      icon.setValue(IconFont.HIDE);
      readerView.getRoot().showHeader();
      SettingManager.manager().getReader().setShowHeader(true);
    }
  }

  /**
   * 窗口置顶
   */
  public void toggleWindowTop() {
    toggleWindowTop(!App.stage().isAlwaysOnTop());
  }

  /**
   * 窗口置顶
   *
   * @param top 是否置顶
   */
  public void toggleWindowTop(boolean top) {
    Icon icon = (Icon) windowTop.getGraphic();
    if (top) {
      App.stage().setAlwaysOnTop(true);
      windowTop.setText("取消窗口置顶");
      icon.setValue(IconFont.NO_TOP);
    } else {
      App.stage().setAlwaysOnTop(false);
      windowTop.setText("窗口置顶");
      icon.setValue(IconFont.TOP);
    }
  }

  /**
   * 退出
   */
  public void exit() {
    readerView.onClose(readerView.getRoot(), null);
  }
}
