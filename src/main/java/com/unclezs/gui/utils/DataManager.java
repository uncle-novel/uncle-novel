package com.unclezs.gui.utils;

import com.unclezs.model.ApplicationConfig;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;

/**
 * 全局数据
 *
 * @author unclezs.com
 * @date 2019.06.21 11:32
 */
@UtilityClass
public class DataManager {
    /**
     * 应用配置
     */
    public ApplicationConfig application;
    /**
     * 当前显示窗口
     */
    public Stage currentStage;
}
