package com.unclezs.novel.app.jfx.app.ui.pages.home.header;

import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXPopup;
import com.unclezs.novel.app.jfx.app.ui.app.App;
import com.unclezs.novel.app.jfx.framework.annotation.FxView;
import com.unclezs.novel.app.jfx.framework.ui.view.BaseView;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.awt.Color;


/**
 * 主题
 *
 * @author blog.unclezs.com
 * @date 2021/03/06 18:16
 */
@FxView(fxml = "/layout/home/header/theme.fxml")
public class ThemePopupView extends BaseView {
    public static final String THEME_FORMAT = "/css/home/theme/%s.css";
    public JFXPopup popup;
    public JFXColorPicker customColorButton;
    private Color color;

    @Override
    public void onCreated() {

    }

    /**
     * 切换主题
     *
     * @param event 点击切换
     */
    public void changeTheme(MouseEvent event) {
        Node themePalette = (Node) event.getSource();
        String theme = String.format(THEME_FORMAT, themePalette.getUserData().toString());
        App.changeTheme(theme);
        popup.hide();
    }


    public void customThemeColor(MouseEvent mouseEvent) {
        System.out.println(customColorButton.getValue());

    }
}
