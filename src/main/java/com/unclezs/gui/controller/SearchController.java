package com.unclezs.gui.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import com.jfoenix.controls.JFXListView;
import com.unclezs.crawl.TextNovelSpider;
import com.unclezs.enmu.SearchKeyType;
import com.unclezs.gui.components.AbstractLoadingTask;
import com.unclezs.gui.components.SearchNode;
import com.unclezs.gui.components.SearchTextField;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.ContentUtil;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.gui.utils.DesktopUtil;
import com.unclezs.gui.utils.ToastUtil;
import com.unclezs.model.rule.SearchTextRule;
import com.unclezs.utils.thead.RunAsyncUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文本小说搜索
 *
 * @author unclezs.com
 * @date 2019.06.26 16:44
 */
@FXController("search")
public class SearchController implements LifeCycleFxController {
    public SearchTextField search;
    public FlowPane searchResultTitle;
    public JFXListView<SearchNode> searchResultList;


    @Override
    public void initialize() {
        //列表菜单
        searchResultList.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && !searchResultList.getContextMenu().isShowing()) {
                analysisBook();
            }
        });
        search.setOnSubmit(this::search);
        search.getBox().getItems().addAll(SearchKeyType.TITLE, SearchKeyType.AUTHOR);
        searchResultTitle.visibleProperty().bind(
            Bindings.createBooleanBinding(() -> searchResultList.getItems().size() != 0, searchResultList.getItems()));
    }

    @Override
    public void onShow(Dict data) {
        RunAsyncUtil.run(() -> Platform.runLater(() -> this.search.getInput().requestFocus()), 150);
    }

    public void search(String keyword, SearchKeyType type) {
        this.searchResultList.getItems().clear();
        //搜索任务
        AbstractLoadingTask searchTask = new AbstractLoadingTask() {
            @Override
            protected Object call() {
                //过滤出启用的规则
                List<SearchTextRule> rules =
                    DataManager.application.getTextRules().stream().filter(SearchTextRule::isEnabled).collect(
                        Collectors.toList());
                TextNovelSpider spider = new TextNovelSpider();
                int finishedCount = 0;
                for (SearchTextRule rule : rules) {
                    List<SearchNode> nodes =
                        spider.search(keyword, rule, type).stream().map(SearchNode::new).collect(Collectors.toList());
                    Platform.runLater(() -> {
                        if (!isCancelled()) {
                            searchResultList.getItems().addAll(nodes);
                        }
                    });
                    if (++finishedCount == 1) {
                        Platform.runLater(this::close);
                    }
                }
                return 0;
            }
        };
        ThreadUtil.execute(searchTask);
        searchTask.setSuccessHandler(e -> {
            if (searchResultList.getItems().size() == 0) {
                ToastUtil.warning("啥都没有搜索到哎，可以去试试目录解析~");
            } else {
                ToastUtil.success("搜索完毕");
            }
            search.finished();
        });
        searchTask.setCancelHandler(e -> search.finished());
    }

    /**
     * 浏览器打开
     */
    public void openInBrowse() {
        DesktopUtil.openBrowse(searchResultList.getSelectionModel().getSelectedItem().getInfo().getUrl());
    }

    /**
     * 解析书
     */
    public void analysisBook() {
        if (searchResultList.getItems().size() == 0) {
            return;
        }
        //选中的章节目录地址
        SearchNode item = searchResultList.getSelectionModel().getSelectedItem();
        if (item == null) {
            return;
        }
        String url = item.getInfo().getUrl();
        ContentUtil.show(AnalysisController.class, Dict.create().set("url", url));
    }

    /**
     * url到剪贴板
     */
    public void copyLink() {
        DesktopUtil.copyLink(searchResultList.getSelectionModel().getSelectedItem().getInfo().getUrl());
    }
}
