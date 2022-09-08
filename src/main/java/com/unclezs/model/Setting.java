package com.unclezs.model;

import cn.hutool.core.util.ArrayUtil;
import com.unclezs.enmu.LanguageLocale;
import com.unclezs.enmu.TextNovelType;
import com.unclezs.utils.FileUtil;
import com.unclezs.utils.JsonUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 设置
 *
 * @author unclezs.com
 * @date 2019.07.05 19:33
 */
@Data
public final class Setting implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 下载路径
     */
    private SimpleStringProperty savePath = new SimpleStringProperty(FileUtil.getCurrentDir());
    /**
     * 代理端口和Host
     */
    private SimpleStringProperty proxyHost = new SimpleStringProperty();
    private SimpleStringProperty proxyPort = new SimpleStringProperty();
    private SimpleBooleanProperty useProxy = new SimpleBooleanProperty(false);

    /**
     * 线程数量
     */
    private SimpleIntegerProperty threadNum = new SimpleIntegerProperty(1);
    /**
     * 每章节延迟
     */
    private SimpleIntegerProperty delay = new SimpleIntegerProperty(0);
    /**
     * 下载完成后是否合并
     */
    private SimpleBooleanProperty mergeFile = new SimpleBooleanProperty(true);
    /**
     * 下载格式 mobi epub txt
     */
    private SimpleStringProperty textNovelSaveType = new SimpleStringProperty(TextNovelType.TXT.toString());

    /**
     * 自动导入剪贴版
     */
    private SimpleBooleanProperty autoImportClipboardLink = new SimpleBooleanProperty(true);
    /**
     * 退出时候是否为最小化到托盘 0询问 1退出 2托盘
     */
    private SimpleIntegerProperty exitHandler = new SimpleIntegerProperty(0);
    /**
     * 语言
     */
    private SimpleObjectProperty<LanguageLocale> language = new SimpleObjectProperty<>(LanguageLocale.ZH_CN);


    public int getSaveType() {
        return ArrayUtil.indexOf(TextNovelType.values(), TextNovelType.valueOf(this.textNovelSaveType.get()));
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
