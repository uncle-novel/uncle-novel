package com.unclezs.gui.utils;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

/**
 * @author uncle
 * @date 2020/4/22 0:09
 */
public class DesktopUtil {
    private static File historyDir;

    /**
     * 浏览器打开
     *
     * @param url /
     */
    public static void openBrowse(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 打开文件夹
     *
     * @param file /
     */
    public static void openDir(File file) {
        if (!file.exists()) {
            ToastUtil.error("文件夹不存在");
        }
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制到剪贴板
     *
     * @param text 要复制的文字
     */
    public static void copyLink(String text) {
        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.put(DataFormat.PLAIN_TEXT, text);
        cb.setContent(content);
        ToastUtil.success("复制成功");
    }

    /**
     * 选择图片
     *
     * @param consumer /
     */
    public static void selectImage(Consumer<String> consumer) {
        FileChooser chooser = new FileChooser();
        if (historyDir != null) {
            chooser.setInitialDirectory(historyDir);
        }
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("背景图", "*.jpg", "*.png", "*.jpeg"));
        File file = chooser.showOpenDialog(DataManager.currentStage);
        if (cn.hutool.core.io.FileUtil.exist(file)) {
            historyDir = file.getParentFile();
            if (com.unclezs.utils.FileUtil.isImage(file.getAbsolutePath())) {
                String path = file.getAbsoluteFile().toURI().toString();
                consumer.accept(path);
            } else {
                ToastUtil.error("只支持图片文件");
            }
        }
    }
}
