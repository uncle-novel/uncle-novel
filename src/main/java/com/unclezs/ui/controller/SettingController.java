package com.unclezs.ui.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;
import com.unclezs.mapper.SettingMapper;
import com.unclezs.model.DownloadConfig;
import com.unclezs.ui.node.ProgressFrom;
import com.unclezs.ui.utils.AlertUtil;
import com.unclezs.ui.utils.DataManager;
import com.unclezs.ui.utils.ToastUtil;
import com.unclezs.utils.ConfUtil;
import com.unclezs.utils.MybatisUtil;
import com.unclezs.utils.ProxyUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 设置控制器
 *
 * @author unclezs.com
 * @date 2019.07.07 11:52
 */
public class SettingController implements Initializable {
    @FXML
    JFXToggleButton merge, autoImport;
    @FXML
    JFXComboBox<Integer> chapterNum, delay;
    @FXML
    JFXRadioButton dmobi, depub, dtxt;
    @FXML
    JFXButton testProxy, saveProxy;
    @FXML
    TextField proxyPort, proxyHost;
    @FXML
    Label pathLabel, changePath;

    ToggleGroup group = new ToggleGroup();
    private static DownloadConfig config;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dmobi.setToggleGroup(group);
        dtxt.setToggleGroup(group);
        depub.setToggleGroup(group);
        initData();
        initEventHandler();
    }

    //初始化数据
    private void initData() {
        for (int i = 0; i < 30; i++) {
            delay.getItems().add(i);
        }
        for (int i = 30; i < 1000; i += 5) {
            delay.getItems().add(i);
        }
        for (int i = 50; i < 10000; i += 100) {
            chapterNum.getItems().add(i);
        }
        SettingMapper mapper = MybatisUtil.getMapper(SettingMapper.class);
        config = mapper.querySetting();
        MybatisUtil.getCurrentSqlSession().close();
        merge.setSelected(config.isMergeFile());
        //禁用焦点过渡
        merge.setDisableVisualFocus(true);
        chapterNum.setValue(config.getPerThreadDownNum());
        delay.setValue(config.getSleepTime() / 1000);
        pathLabel.setText(config.getPath());
        autoImport.setSelected(Boolean.valueOf(ConfUtil.get(ConfUtil.USE_ANALYSIS_PASTE)));
        switch (config.getFormat()) {
            case "epub":
                depub.setSelected(true);
                break;
            case "txt":
                dtxt.setSelected(true);
                break;
            default:
                dmobi.setSelected(true);
                break;
        }
        //读取本地配置
        proxyPort.setText(ConfUtil.get(ConfUtil.PROXY_PORT));
        proxyHost.setText(ConfUtil.get(ConfUtil.PROXY_HOSTNAME));
    }

    /**
     * 初始化事件监听
     */
    private void initEventHandler() {
        //值改变监听
        merge.selectedProperty().addListener(e -> {
            config.setMergeFile(merge.isSelected());
        });
        chapterNum.valueProperty().addListener(e -> {
            config.setPerThreadDownNum(chapterNum.getValue());
        });
        delay.valueProperty().addListener(e -> {
            config.setSleepTime(delay.getValue() * 1000);
        });
        changePath.setOnMouseClicked(e -> {
            //文件选择
            DirectoryChooser chooser = new DirectoryChooser();
            File dir = new File(config.getPath());
            if (dir.exists()) {
                chooser.setInitialDirectory(dir);
            }
            chooser.setTitle("选择下载位置");
            File file = chooser.showDialog(DataManager.mainStage);
            //防空
            if (file == null || !file.exists()) {
                return;
            }
            //更新
            String path = file.getAbsolutePath() + File.separator;
            pathLabel.setText(path);
            config.setPath(path);
        });
        dmobi.selectedProperty().addListener(e -> {
            config.setFormat("mobi");
        });
        dtxt.selectedProperty().addListener(e -> {
            config.setFormat("txt");
        });
        depub.selectedProperty().addListener(e -> {
            config.setFormat("epub");
        });
        autoImport.selectedProperty().addListener(e -> {
            ConfUtil.set(ConfUtil.USE_ANALYSIS_PASTE, autoImport.isSelected() + "");
        });
        testProxy.setOnMouseClicked(e -> {
            String host = proxyHost.getText();
            String port = proxyPort.getText();
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    return ProxyUtil.testProxy(host, port);
                }
            };
            ProgressFrom pf = new ProgressFrom(DataManager.settingStage, task);
            pf.activateProgressBar();
            task.setOnSucceeded(es -> {
                pf.cancelProgressBar();
                if (task.getValue() == null) {
                    ToastUtil.toast("代理无效");
                } else {
                    AlertUtil.getAlert("代理信息", task.getValue()).show();
                }
            });
        });

        saveProxy.setOnMouseClicked(e -> {
            String host = proxyHost.getText();
            String port = proxyPort.getText();
            ConfUtil.set(ConfUtil.PROXY_HOSTNAME, host);
            ConfUtil.set(ConfUtil.PROXY_PORT, port);
            ToastUtil.toast("保存成功", DataManager.settingStage);
        });
    }

    //保存更新设置
    public static void updateSetting() {
        ThreadUtil.execute(() -> {
            SettingMapper mapper = MybatisUtil.getMapper(SettingMapper.class);
            mapper.updateSetting(config);
            MybatisUtil.getCurrentSqlSession().close();
        });
    }
}
