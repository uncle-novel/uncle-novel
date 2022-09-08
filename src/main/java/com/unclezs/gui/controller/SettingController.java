package com.unclezs.gui.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;
import com.unclezs.enmu.LanguageLocale;
import com.unclezs.gui.components.AbstractLoadingTask;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.AlertUtil;
import com.unclezs.gui.utils.ApplicationUtil;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.gui.utils.DesktopUtil;
import com.unclezs.gui.utils.ResourceUtil;
import com.unclezs.gui.utils.ToastUtil;
import com.unclezs.mapper.SearchAudioRuleMapper;
import com.unclezs.mapper.SearchTextRuleMapper;
import com.unclezs.model.Setting;
import com.unclezs.model.rule.Rule;
import com.unclezs.model.rule.SearchAudioRule;
import com.unclezs.model.rule.SearchTextRule;
import com.unclezs.utils.FileUtil;
import com.unclezs.utils.MybatisUtil;
import com.unclezs.utils.RequestUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.controlsfx.glyphfont.Glyph;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 设置控制器
 *
 * @author unclezs.com
 * @date 2019.07.07 11:52
 */
@FXController("setting")
@SuppressWarnings("unchecked")
public class SettingController implements LifeCycleFxController {
    private static final int MAX_THREAD_NUM = 50;
    private static final int MAX_DELAY_TIME = 1000;
    private static final int MAX_DELAY_TIME_PART = 130;
    public ToggleGroup saveTypeGroup, exitHandlerGroup;
    public JFXToggleButton merge, autoImport;
    public JFXComboBox<Integer> threadNum, delay;
    public JFXButton useProxy;
    public TextField proxyPort, proxyHost, savePathInput;
    public TableView<SearchTextRule> textTable;
    public TableView<SearchAudioRule> audioTable;
    public TableColumn<SearchTextRule, Button> deleteTextRule;
    public TableColumn<SearchAudioRule, Button> audioDeleteTextRule;
    public TabPane root;
    public JFXComboBox<LanguageLocale> language;

    @Override
    public void initialize() {
        initData();
        initEventHandler();
    }

    @Override
    public void onHidden() {
        ApplicationUtil.storeConfig();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        for (int i = 0; i < MAX_DELAY_TIME_PART; i++) {
            delay.getItems().add(i);
        }
        for (int i = MAX_DELAY_TIME_PART; i < MAX_DELAY_TIME; i += 5) {
            delay.getItems().add(i);
        }
        for (int i = 1; i < MAX_THREAD_NUM; i++) {
            threadNum.getItems().add(i);
        }
        merge.selectedProperty().bindBidirectional(DataManager.application.getSetting().getMergeFile());
        savePathInput.textProperty().bindBidirectional(DataManager.application.getSetting().getSavePath());
        saveTypeGroup.selectToggle(saveTypeGroup.getToggles().get(DataManager.application.getSetting().getSaveType()));
        autoImport.selectedProperty().bindBidirectional(
            DataManager.application.getSetting().getAutoImportClipboardLink());
        exitHandlerGroup.selectToggle(
            exitHandlerGroup.getToggles().get(DataManager.application.getSetting().getExitHandler().get()));
        proxyHost.textProperty().bindBidirectional(DataManager.application.getSetting().getProxyHost());
        proxyPort.textProperty().bindBidirectional(DataManager.application.getSetting().getProxyPort());
        useProxy(DataManager.application.getSetting().getUseProxy().get());
        threadNum.valueProperty().addListener(
            e -> DataManager.application.getSetting().getThreadNum().set(threadNum.getValue()));
        threadNum.valueProperty().bindBidirectional(DataManager.application.getSetting().getThreadNum().asObject());
        delay.valueProperty().addListener(e -> DataManager.application.getSetting().getDelay().set(delay.getValue()));
        delay.valueProperty().bindBidirectional(DataManager.application.getSetting().getDelay().asObject());
        language.valueProperty().bindBidirectional(DataManager.application.getSetting().getLanguage());
        initTable();
    }

    /**
     * 初始话规则表格数据
     */
    private void initTable() {
        //文本小说
        initTable(textTable, SearchTextRule.class, SearchTextRuleMapper.class, deleteTextRule);
        //有声小说
        initTable(audioTable, SearchAudioRule.class, SearchAudioRuleMapper.class, audioDeleteTextRule);
        ThreadUtil.execute(() -> {
            textTable.setItems(DataManager.application.getTextRules());
            audioTable.setItems(DataManager.application.getAudioRules());
        });
    }

    /**
     * 绑定列
     *
     * @param column    列
     * @param colName   列名
     * @param converter 类型转换器
     * @param consumer  编辑提交事件处理
     * @param <T>       /
     * @param <R>       /
     */
    private <T, R> void setCellFactory(TableColumn<T, R> column, String colName, StringConverter<R> converter,
        Consumer<TableColumn.CellEditEvent<T, R>> consumer) {
        column.setCellValueFactory(new PropertyValueFactory<>(colName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(converter));
        column.setOnEditCommit(consumer::accept);
    }

    /**
     * 绑定表格数据
     *
     * @param dataClazz  绑定行的数据类
     * @param table      表格数据
     * @param baseMapper 操作数据得mapper
     * @param opCol      操作列
     */
    private <T> void initTable(TableView<T> table, Class<T> dataClazz, Class<? extends BaseMapper<T>> baseMapper,
        TableColumn<T, Button> opCol) {
        Field[] fields = dataClazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String colName = field.getName();
            TableColumn<T, ?> column = table.getColumns().stream().filter(
                col -> (opCol != col) && col.getUserData().equals(colName)).findFirst().orElse(null);
            StringConverter converter = new StringConverter() {
                @Override
                public String toString(Object object) {
                    return Convert.toStr(object);
                }

                @Override
                public Object fromString(String string) {
                    return Convert.convert(field.getType(), string);
                }
            };
            if (column != null) {
                setCellFactory(column, colName, converter, e -> {
                    try {
                        field.set(e.getRowValue(), e.getNewValue());
                        MybatisUtil.execute(baseMapper, mapper -> mapper.updateById(e.getRowValue()));
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        }
        opCol.setCellValueFactory(param -> createDelBtn(() -> {
            table.getItems().remove(param.getValue());
            MybatisUtil.execute(baseMapper, mapper -> mapper.deleteById(((Rule) param.getValue()).getSite()));
            ToastUtil.success("删除成功");
        }));
        table.prefWidthProperty().bind(root.widthProperty());
    }

    /**
     * 创建删除按钮
     *
     * @param clickHandler 这一行的数据
     * @return /
     */
    private SimpleObjectProperty<Button> createDelBtn(Runnable clickHandler) {
        Glyph graphic = new Glyph("FontAwesome", "\uf057");
        Button delBtn = new Button(ResourceUtil.getString("delete"), graphic);
        delBtn.getStyleClass().addAll("del-btn");
        delBtn.setOnMouseClicked(e -> {
            AlertUtil.confirm("确认删除嘛?", res -> {
                if (res) {
                    clickHandler.run();
                }
            });
        });
        return new SimpleObjectProperty<>(delBtn);
    }

    /**
     * 改变保存文件夹
     */
    @FXML
    public void changeSavePath() {
        //文件选择
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = new File(DataManager.application.getSetting().getSavePath().get());
        if (dir.exists()) {
            chooser.setInitialDirectory(dir);
        }
        chooser.setTitle("选择下载位置");
        File file = chooser.showDialog(DataManager.currentStage);
        if (file != null && file.exists()) {
            savePathInput.setText(FileUtil.getPath(file));
        }
    }

    /**
     * 打开保存文件夹
     */
    @FXML
    public void openSavePath() {
        File dir = new File(DataManager.application.getSetting().getSavePath().get());
        if (dir.exists()) {
            DesktopUtil.openDir(dir);
        } else {
            DesktopUtil.openDir(new File("/"));
        }
    }

    /**
     * 初始化事件监听
     */
    private void initEventHandler() {
        saveTypeGroup.selectedToggleProperty().addListener(
            l -> DataManager.application.getSetting().getTextNovelSaveType().set(
                ((JFXRadioButton) saveTypeGroup.getSelectedToggle()).getText()));
        exitHandlerGroup.selectedToggleProperty().addListener(
            l -> DataManager.application.getSetting().getExitHandler().set(
                exitHandlerGroup.getToggles().indexOf(exitHandlerGroup.getSelectedToggle())));
        useProxy.setOnMouseClicked(e -> {
            boolean userProxy = !DataManager.application.getSetting().getUseProxy().get();
            DataManager.application.getSetting().getUseProxy().set(userProxy);
            useProxy(userProxy);
        });
        language.valueProperty().addListener(e -> ToastUtil.success(ResourceUtil.getString("update_success")));
    }

    /**
     * 应用代理
     *
     * @param useProxy 是否使用
     */
    private void useProxy(boolean useProxy) {
        Setting setting = DataManager.application.getSetting();
        if (useProxy) {
            if (StrUtil.isNotEmpty(setting.getProxyHost().get()) && StrUtil.isNotEmpty(setting.getProxyPort().get())) {
                System.setProperty("proxyHost", setting.getProxyHost().get());
                System.setProperty("proxyPort", setting.getProxyPort().get());
            }
            this.useProxy.setText(ResourceUtil.getString("disable"));
        } else {
            System.setProperty("proxyHost", "");
            System.setProperty("proxyPort", "");
            this.useProxy.setText(ResourceUtil.getString("enabled"));
        }
    }


    /**
     * 到出有声规则
     */
    @FXML
    public void exportAudioRule() {
        exportRuleJson(false);
    }

    /**
     * 导出文本规则
     */
    @FXML
    public void exportTextRule() {
        exportRuleJson(true);
    }

    /**
     * 导入有声规则
     */
    @FXML
    public void importAudioRule() {
        importRuleJson(false);
    }

    /**
     * 导入文本规则
     */
    @FXML
    public void importTextRule() {
        importRuleJson(true);
    }

    /**
     * 导入解析配置
     *
     * @param isText 是否为文本小说
     */
    private void importRuleJson(boolean isText) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JSON", "*.json");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(DataManager.currentStage);
        if (file != null && file.exists()) {
            boolean accept = cn.hutool.core.io.FileUtil.pathEndsWith(file, "json");
            if (accept) {
                try {
                    AbstractLoadingTask saveTask;
                    if (isText) {
                        List<SearchTextRule> rules =
                            JSON.parseArray(cn.hutool.core.io.FileUtil.readUtf8String(file), SearchTextRule.class);
                        List<SearchTextRule> collect = rules.stream().filter(rule -> {
                            for (SearchTextRule item : textTable.getItems()) {
                                if (item.getSite().equals(rule.getSite())) {
                                    return false;
                                }
                            }
                            return true;
                        }).collect(Collectors.toList());
                        textTable.getItems().addAll(collect);
                        saveTask = new AbstractLoadingTask() {
                            @Override
                            protected Object call() {
                                for (SearchTextRule rule : collect) {
                                    MybatisUtil.execute(SearchTextRuleMapper.class, mapper -> mapper.insert(rule));
                                }
                                return null;
                            }
                        };
                    } else {
                        List<SearchAudioRule> rules =
                            JSON.parseArray(cn.hutool.core.io.FileUtil.readUtf8String(file), SearchAudioRule.class);
                        List<SearchAudioRule> collect = rules.stream().filter(rule -> {
                            for (SearchAudioRule item : audioTable.getItems()) {
                                if (item.getSite().equals(rule.getSite())) {
                                    return false;
                                }
                            }
                            return true;
                        }).collect(Collectors.toList());
                        audioTable.getItems().addAll(collect);
                        saveTask = new AbstractLoadingTask() {
                            @Override
                            protected Object call() {
                                for (SearchAudioRule rule : collect) {
                                    MybatisUtil.execute(SearchAudioRuleMapper.class, mapper -> mapper.insert(rule));
                                }
                                return null;
                            }
                        };
                    }
                    saveTask.setSuccessHandler(e -> ToastUtil.success(ResourceUtil.getString("import_success")));
                    ThreadUtil.execute(saveTask);
                } catch (JSONException exception) {
                    ToastUtil.error("导入失败，请确认为JSON格式");
                }
            }
        }
    }

    private void exportRuleJson(boolean isText) {
        FileChooser fileChooser = new FileChooser();
        String fileName = isText ? "文本小说规则.json" : "有声小说规则.json";
        fileChooser.setInitialFileName(fileName);
        File file = fileChooser.showSaveDialog(DataManager.currentStage);
        if (file != null) {
            cn.hutool.core.io.FileUtil.writeUtf8String(
                JSON.toJSONString(isText ? textTable.getItems() : audioTable.getItems()), file);
            ToastUtil.success("导出成功");
        }
    }

    /**
     * 测试代理
     */
    public void testProxy() {
        AbstractLoadingTask<String> task = new AbstractLoadingTask<String>() {
            @Override
            protected String call() throws IOException {
                Document doc = RequestUtil.doc("http://www.cip.cc/");
                return doc.select(".kq-well").first().text();
            }
        };
        ThreadUtil.execute(task);
        task.setSuccessHandler(es -> {
            if (task.getValue() == null) {
                ToastUtil.error("代理无效");
            } else {
                AlertUtil.alert("代理信息", task.getValue());
            }
        });
    }
}
