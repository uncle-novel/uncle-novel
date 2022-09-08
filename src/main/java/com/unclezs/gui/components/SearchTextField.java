package com.unclezs.gui.components;


import cn.hutool.core.util.StrUtil;
import com.unclezs.enmu.SearchKeyType;
import com.unclezs.gui.utils.ResourceUtil;
import com.unclezs.gui.utils.ToastUtil;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.controlsfx.glyphfont.Glyph;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 搜索框
 *
 * @author uncle
 * @date 2020/4/23 9:37
 */
@Getter
public class SearchTextField extends FlowPane {
    private static final char SEARCH_ICON = '\uf002';
    private static final char STOP_ICON = '\uf28e';
    private static final char CLEAR_ICON = '\uf057';
    private TextField input = new TextField();
    private Glyph leftIcon = new Glyph("FontAwesome", SEARCH_ICON);
    private Glyph rightIcon = new Glyph("FontAwesome", CLEAR_ICON);
    private AtomicBoolean searching = new AtomicBoolean(false);
    private ComboBox<SearchKeyType> box = new ComboBox<>();
    private SearchRunner onSubmitRunner;

    public SearchTextField() {
        init();
    }


    private void init() {
        this.getStylesheets().addAll(ResourceUtil.loadCss("/css/components/search-text-field.css"));
        this.getStyleClass().add("search-pane");
        this.input.getStyleClass().addAll("search-text-input");
        this.leftIcon.getStyleClass().addAll("search-left-icon");
        this.rightIcon.getStyleClass().addAll("search-right-icon");
        this.rightIcon.setTooltip(new Tooltip("清除输入内容"));
        this.box.getStyleClass().addAll("search-combo-box", "bg-color-theme");
        this.box.setValue(SearchKeyType.TITLE);
        this.getInput().setPromptText("请输入要搜索的关键词");
        this.getChildren().addAll(leftIcon, box, input, rightIcon);
        bindSize();
        initEventHandler();
    }

    private void bindSize() {
        this.leftIcon.prefHeightProperty().bind(prefHeightProperty());
        this.rightIcon.setIcon("");
        this.rightIcon.prefHeightProperty().bind(prefHeightProperty());
        this.box.prefHeightProperty().bind(prefHeightProperty());
        this.input.prefHeightProperty().bind(prefHeightProperty());
        this.input.prefWidthProperty().bind(
            prefWidthProperty().subtract(this.leftIcon.prefWidthProperty().multiply(2)));
        this.input.textProperty().addListener(e -> {
            if (StrUtil.isBlank(this.input.getText())) {
                this.rightIcon.setIcon("");
            } else {
                this.rightIcon.setIcon(CLEAR_ICON);
            }
        });
    }

    private void initEventHandler() {
        this.rightIcon.setOnMouseClicked(e -> {
            this.input.clear();
        });
        this.input.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (searching.get()) {
                    ToastUtil.warning("正在搜索中,请先停止搜索");
                } else {
                    search();
                }
            }
        });
        //点击图标搜索
        this.leftIcon.setOnMouseClicked(e -> {
            if (!searching.get()) {
                search();
            } else {
                finished();
            }
        });
    }

    private void search() {
        String keyword = input.getText().trim();
        if (StrUtil.isBlank(keyword)) {
            ToastUtil.warning("多少输入点东西~");
            return;
        }
        this.leftIcon.setColor(Color.RED);
        this.leftIcon.setIcon(STOP_ICON);
        searching.set(true);
        if (onSubmitRunner != null) {
            onSubmitRunner.search(getText(), getType());
        }
    }

    /**
     * 设置搜索提交处理回调
     *
     * @param searchRunner /
     */
    public void setOnSubmit(SearchRunner searchRunner) {
        this.onSubmitRunner = searchRunner;
    }

    /**
     * 设置搜索完成
     */
    public void finished() {
        this.leftIcon.setIcon(SEARCH_ICON);
        this.searching.set(false);
        this.leftIcon.setColor(Color.valueOf("#888"));
    }

    /**
     * 获取输入框的值
     */
    public String getText() {
        return this.input.getText().trim();
    }

    /**
     * 获取搜索key的类型
     */
    public SearchKeyType getType() {
        return this.box.getValue();
    }

    /**
     * 搜索Runner
     */
    public interface SearchRunner {
        /**
         * 搜索方法
         *
         * @param keyword 关键词
         * @param type    搜索类型
         */
        void search(String keyword, SearchKeyType type);
    }
}
