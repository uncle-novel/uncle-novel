package com.unclezs.gui.controller.components;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.utils.JFXNodeUtils;
import com.unclezs.gui.app.App;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.gui.utils.DesktopUtil;
import com.unclezs.gui.utils.ThemeUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;


/**
 * 换肤页面
 *
 * @author unclezs.com
 * @date 2020.05.2 13:30
 */
@FXController("components/theme_setting")
public class ThemeController {
    public JFXSlider opacitySlider;
    public JFXColorPicker colorPicker;
    public JFXColorPicker headerColorPicker;
    private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);

    public void initialize() {
        colorPicker.valueProperty().bindBidirectional(color);
        colorPicker.setOnAction(e -> {
            DataManager.application.getAppTheme().getFontColor().set("");
            DataManager.application.getAppTheme().getIsBgImage().set(false);
            changeTheme();
        });
        if (StrUtil.isNotBlank(DataManager.application.getAppTheme().getHeaderColor().get())) {
            headerColorPicker.setValue(Color.valueOf(DataManager.application.getAppTheme().getHeaderColor().get()));
        }
        headerColorPicker.setOnAction(e -> {
            DataManager.application.getAppTheme().getHeaderColor().set(
                JFXNodeUtils.colorToHex(headerColorPicker.getValue()));
            changeTheme();
        });
        //初始化透明度
        this.opacitySlider.setValue(DataManager.application.getAppTheme().getOpacity().get());
        DataManager.application.getAppTheme().getOpacity().bind(this.opacitySlider.valueProperty());
        //初始化颜色
        color.set(Color.valueOf(DataManager.application.getAppTheme().getBgColor().get()));
        DataManager.application.getAppTheme().getBgColor().bind(color.asString());
        //加载
        if (DataManager.application.getAppTheme().getIsBgImage().get()) {
            imageBind();
        } else {
            colorBind();
        }
        changeTheme();
        DataManager.application.getAppTheme().getIsBgImage().addListener(e -> {
            App.background.backgroundProperty().unbind();
            App.root.backgroundProperty().unbind();
            if (DataManager.application.getAppTheme().getIsBgImage().get()) {
                opacitySlider.setValue(1);
                imageBind();
            } else {
                opacitySlider.setValue(0);
                colorBind();
            }
        });
    }

    /**
     * 选择背景图
     */
    public void selectBgImage() {
        DesktopUtil.selectImage(path -> {
            DataManager.application.getAppTheme().getBgImage().set(path);
            if (!DataManager.application.getAppTheme().getIsBgImage().get()) {
                DataManager.application.getAppTheme().getIsBgImage().set(true);
            }
        });
    }

    /**
     * 背景图片时候的绑定
     */
    private void imageBind() {
        App.background.backgroundProperty().bind(
            Bindings.createObjectBinding(() -> new Background(new BackgroundImage(
                new Image(DataManager.application.getAppTheme().getBgImage().get()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(-1, -1, true, true, false, true)
            )), DataManager.application.getAppTheme().getBgImage()));
        App.root.backgroundProperty().bind(Bindings.createObjectBinding(() ->
            new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 1 - opacitySlider.getValue()),
                null,
                new Insets(5))), opacitySlider.valueProperty()));
    }


    /**
     * 背景颜色时候的绑定
     */
    private void colorBind() {
        App.root.backgroundProperty().bind(App.background.backgroundProperty());
        App.background.backgroundProperty().bind(Bindings.createObjectBinding(() ->
            new Background(new BackgroundFill(
                Color.color(colorPicker.getValue().getRed(), colorPicker.getValue().getGreen(),
                    colorPicker.getValue().getBlue(), 1 - opacitySlider.getValue()),
                null,
                new Insets(5))), color, opacitySlider.valueProperty()));
    }


    /**
     * 切换主题
     */
    private void changeTheme() {
        ThemeUtil.setCss(Dict.create().set("bgImage", DataManager.application.getAppTheme().getBgImage().get())
                .set("bgColor",
                    Color.TRANSPARENT.equals(color.get()) ? "transparent" : JFXNodeUtils.colorToHex(color.get()))
                .set("headerColor", DataManager.application.getAppTheme().getHeaderColor().get())
                .set("fontColor", DataManager.application.getAppTheme().getFontColor().get()),
            App.stage.getScene(), "css/theme.ftl", "/theme/theme.css");
    }

    /**
     * 使用背景图
     */
    public void applyBgImage() {
        if (StrUtil.isNotEmpty(DataManager.application.getAppTheme().getBgImage().get())) {
            headerColorPicker.setValue(color.get());
            DataManager.application.getAppTheme().getHeaderColor().set(
                JFXNodeUtils.colorToHex(headerColorPicker.getValue()));
            changeTheme();
            color.set(Color.TRANSPARENT);
            transparentHeader();
            DataManager.application.getAppTheme().getIsBgImage().set(true);
        }
    }


    public void resetDefault() {
        colorPicker.setValue(Color.valueOf("#FFF"));
        opacitySlider.setValue(0);
        DataManager.application.getAppTheme().getFontColor().set("");
        DataManager.application.getAppTheme().getHeaderColor().set("#393D49");
        DataManager.application.getAppTheme().getIsBgImage().set(false);
        changeTheme();
    }

    public void transparentHeader() {
        DataManager.application.getAppTheme().getHeaderColor().set("transparent");
        changeTheme();
    }

    public void changeFontBlackColor() {
        DataManager.application.getAppTheme().getFontColor().set("rgba(0,0,0,0.87)");
        changeTheme();
    }

    public void changeFontWhiteColor() {
        DataManager.application.getAppTheme().getFontColor().set("rgba(255,255,255,0.87)");
        changeTheme();
    }
}
