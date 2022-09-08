package com.unclezs.gui.utils;

import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import org.controlsfx.glyphfont.Glyph;

import java.util.function.Consumer;

/**
 * 弹窗
 *
 * @author unclezs.com
 * @date 2019.07.03 14:07
 */
public class AlertUtil {
    public static JFXDialogLayout layout;
    private static JFXAlert alert;

    static {
        alert = new JFXAlert<>(DataManager.currentStage);
        alert.setOverlayClose(false);
        layout = new JFXDialogLayout();
        StackPane content = NodeUtil.createBgPane(layout);
        alert.setContent(content);
    }

    public static JFXAlert alert(String title, String body) {
        return alert(title, "了解了", new Label(body));
    }

    public static JFXAlert alert(String title, Node... body) {
        return alert(title, "了解了", body);
    }

    public static JFXAlert alert(String title, String closeText, Node... body) {
        return alert(title, new JFXButton(closeText), body);
    }

    public static JFXAlert alert(String title, Button closeBtn, Node... body) {
        clearLayout();
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().setAll("dialog-title");
        layout.setHeading(titleLabel);
        layout.setBody(body);
        closeBtn.getStyleClass().add("dialog-close");
        closeBtn.setOnAction(event -> alert.hideWithAnimation());
        layout.setActions(closeBtn);
        alert.show();
        return alert;
    }

    /**
     * 确认对话框
     *
     * @param tips     提示信息  警告提示
     * @param consumer 确认后的 传入 确认true //'\uf06a'
     */
    public static void confirm(String tips, Consumer<Boolean> consumer) {
        Glyph icon = new Glyph("FontAwesome", '\uf06a');
        icon.setStyle("-fx-text-fill: rgb(230, 162, 60)!important;-fx-font-size: 20");
        confirm(tips, icon, "确定", "取消", consumer);
    }

    /**
     * 确认对话框
     *
     * @param tips     提示信息  警告提示
     * @param fontIcon 图标
     * @param consumer 确认后的 传入 确认true
     */
    @SuppressWarnings("unchecked")
    public static void confirm(String tips, Glyph fontIcon, String okText, String noText, Consumer<Boolean> consumer) {
        clearLayout();
        layout.setPrefHeight(50);
        layout.setPrefWidth(200);
        Label tipLabel = new Label(StrUtil.isBlank(tips) ? "确认删除嘛？" : tips, fontIcon);
        tipLabel.setWrapText(true);
        tipLabel.setMinHeight(50);
        tipLabel.setStyle("-fx-font-size: 13!important;");
        layout.setBody(tipLabel);
        JFXButton ok = new JFXButton(okText);
        JFXButton no = new JFXButton(noText);
        ok.getStyleClass().addAll("btn", "info-btn", "btn-xs");
        no.getStyleClass().addAll("btn", "default-btn", "btn-xs");
        ok.setOnMouseClicked(e -> {
            alert.setResult(true);
            alert.hideWithAnimation();
        });
        no.setOnMouseClicked(e -> {
            alert.setResult(false);
            alert.hideWithAnimation();
        });
        layout.setActions(ok, no);
        alert.setOnHidden(e -> consumer.accept((Boolean) alert.getResult()));
        alert.show();
    }

    /**
     * 输入框 返回输入的字符串
     *
     * @param title      /
     * @param initText   /
     * @param promptText /
     * @param success    输入成功回调 参数为新输入的字符串
     */
    public static void input(String title, String initText, String promptText, Consumer<String> success) {
        clearLayout();
        JFXTextField input = new JFXTextField(initText);
        input.setPromptText(promptText);
        JFXButton ok = new JFXButton("确定");
        JFXButton no = new JFXButton("取消");
        ok.getStyleClass().addAll("btn", "info-btn", "btn-xs");
        no.getStyleClass().addAll("btn", "default-btn", "btn-xs");
        layout.setBody(input);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().setAll("dialog-title");
        layout.setHeading(titleLabel);
        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                success.accept(input.getText());
                alert.hideWithAnimation();
            }
        });
        ok.setOnMouseClicked(e -> {
            success.accept(input.getText());
            alert.hideWithAnimation();
        });
        no.setOnMouseClicked(e -> alert.hideWithAnimation());
        layout.setActions(ok, no);
        alert.show();
    }

    /**
     * 清除布局内容
     */
    private static void clearLayout() {
        layout.getActions().clear();
        layout.getBody().clear();
        layout.getHeading().clear();
    }
}
