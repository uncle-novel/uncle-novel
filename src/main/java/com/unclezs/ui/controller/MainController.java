package com.unclezs.ui.controller;

import com.unclezs.ui.node.ProgressFrom;
import com.unclezs.ui.utils.ContentUtil;
import com.unclezs.ui.utils.DataManager;
import com.unclezs.ui.utils.LayoutUitl;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *主页
 *@author unclezs.com
 *@date 2019.06.20 23:46
 */
public class MainController implements Initializable {
    @FXML
    Label findBook, markBook, analysis, findAudioNovel, download, audioSelf;
    @FXML
    Pane content;
    @FXML
    BorderPane menu;
    @FXML
    HBox root;
    //页面缓存
    VBox bookSelf;//书架
    Pane analysisPane;//解析下载
    Pane findBookPane;//脱离书荒
    VBox downloadPane;//下载管理
    Pane searchAudioPane;//寻找有声
    Pane audioBookSelfPane;//有声书架

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    void init() {
        DataManager.content = content;
        initMenu();
        loadBookSelf();
        loadAll();//初始化全部Pane
    }

    //初始化菜单
    void initMenu() {
        findBook.setGraphic(new ImageView("images/菜单页/推荐.jpg"));
        analysis.setGraphic(new ImageView("images/菜单页/解析.jpg"));
        markBook.setGraphic(new ImageView("images/菜单页/书架.jpg"));
        findAudioNovel.setGraphic(new ImageView("images/菜单页/有声.jpg"));
        download.setGraphic(new ImageView("images/菜单页/下载.jpg"));
        audioSelf.setGraphic(new ImageView("images/菜单页/声音书架.jpg"));
        //宽高绑定，实现自适应
        LayoutUitl.bind(DataManager.root, root);
        LayoutUitl.bind(root, content);
        LayoutUitl.bindHeight(root, menu);
        //书架
        markBook.setOnMouseClicked(e -> {
            loadBookSelf();
        });
        //解析下载
        analysis.setOnMouseClicked(e -> {
            if (analysisPane == null) {
                if (ContentUtil.cachePane != null) {//如果之前有缓存则不生成
                    analysisPane = ContentUtil.cachePane;
                } else {//先点击的解析下载
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/analysis.fxml"));
                        analysisPane = loader.load();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            setContent(analysisPane);
        });
        //脱离书荒
        findBook.setOnMouseClicked(e -> {
            if (findBookPane == null) {
                try {
                    findBookPane = new FXMLLoader(getClass().getResource("/fxml/search.fxml")).load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            setContent(findBookPane);
        });
        //下载管理
        download.setOnMouseClicked(e -> {
            if (downloadPane == null) {
                try {
                    downloadPane = new FXMLLoader(getClass().getResource("/fxml/download.fxml")).load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            setContent(downloadPane);
        });
        //寻找有声
        findAudioNovel.setOnMouseClicked(e -> {
            if (searchAudioPane == null) {
                try {
                    searchAudioPane = FXMLLoader.load(getClass().getResource("/fxml/search_audio.fxml"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            setContent(searchAudioPane);
        });
        audioSelf.setOnMouseClicked(event -> {
            if (audioBookSelfPane == null) {
                try {
                    audioBookSelfPane = FXMLLoader.load(getClass().getResource("/fxml/audioBookSelf.fxml"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            setContent(audioBookSelfPane);
        });
        //监听鼠标移入移出
        addMenuSelectedListener(findBook, analysis, markBook, findAudioNovel, audioSelf, download);
    }

    //异步加载全部页面（降低点击延迟）
    private void loadAll() {
        Platform.runLater(() -> {
            new Thread(() -> {
                try {
                    //获取文件所在位置
                    downloadPane = new FXMLLoader(getClass().getResource("/fxml/download.fxml")).load();
                    findBookPane = new FXMLLoader(getClass().getResource("/fxml/search.fxml")).load();
                    audioBookSelfPane = FXMLLoader.load(getClass().getResource("/fxml/audioBookSelf.fxml"));
                    searchAudioPane = FXMLLoader.load(getClass().getResource("/fxml/search_audio.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    //加载书架
    public void loadBookSelf() {
        Platform.runLater(() -> {
            if (DataManager.needReloadBookSelf || bookSelf == null) {//需要刷新或者第一次打开时
                //loading
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookShelf.fxml"));
                        bookSelf = loader.load();
                        DataManager.needReloadBookSelf = false;
                        return null;
                    }
                };
                ProgressFrom pf = new ProgressFrom(DataManager.mainStage,task);
                task.setOnSucceeded(e -> {//完成后设置内容
                    setContent(bookSelf);
                    pf.cancelProgressBar();
                    return;
                });
                pf.activateProgressBar();
            } else {//不需要刷新直接查缓存
                setContent(bookSelf);
            }
        });
    }

    //设置内容区域
    public void setContent(Region node) {
        node.prefHeightProperty().bind(content.heightProperty().subtract(1));
        node.prefWidthProperty().bind(content.widthProperty().subtract(1));
        content.getChildren().clear();
        content.getChildren().add(node);
    }

    //添加鼠标移入移出监听
    public void addMenuSelectedListener(Label... node) {
        for (Node n : node) {
            n.setOnMouseMoved(e -> {
                n.setStyle("-fx-background-color: #009688");
            });
            n.setOnMouseExited(event -> {
                n.setStyle("-fx-background-color: #f2f2f2");
            });
        }
    }
}
