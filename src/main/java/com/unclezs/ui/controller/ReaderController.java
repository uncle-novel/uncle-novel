package com.unclezs.ui.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import com.unclezs.mapper.NovelMapper;
import com.unclezs.mapper.ReaderMapper;
import com.unclezs.model.ReaderConfig;
import com.unclezs.ui.app.Reader;
import com.unclezs.ui.node.ProgressFrom;
import com.unclezs.ui.utils.DataManager;
import com.unclezs.ui.utils.LayoutUitl;
import com.unclezs.ui.utils.ToastUtil;
import com.unclezs.ui.utils.TrayUtil;
import com.unclezs.utils.MybatisUtil;
import com.unclezs.utils.VoiceUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/*
 *阅读器
 *@author unclezs.com
 *@date 2019.06.22 16:09
 */
public class ReaderController implements Initializable {
    @FXML
    Label title;//标题
    @FXML
    JFXToggleButton voice;//朗读
    @FXML
    Label readLabel, fontAdd, fontless, fontText, chapter, hideSet,
            pageAdd, pageLess, song, yahei, kaiti, pageWidth,
            fontStyle, pageSize, leftLabel, rightLabel;//设置页
    @FXML
    Label huyan, yangpi, heise, molv, anse, baise;//背景色
    @FXML
    Pane setPane;//设置页面
    @FXML
    JFXTextArea content;//内容
    @FXML
    JFXListView<String> list;//目录
    @FXML
    TextField searchContent;//正文搜索
    @FXML
    Pane root;//根容器
    int index = DataManager.book.getCpage();//当前章节
    private VoiceUtil voiceUtil;//朗读工具类
    int scrollNum = 0;//滚动事件次数
    double contentMaxWidth = 800;
    ScrollPane sp;//滚动
    //网络小说
    String text;//正文
    String novelTitle;//标题
    boolean firstLoading = true;//第一次加载标志
    boolean isPageTopOver = false;//一章节顶部标志
    boolean isPageDownOver = false;//一章节尾部标志
    private static String itemText = "隐藏边框";//隐藏边框菜单文字
    private static String searchText = "隐藏边框";//搜索内容，便于下一个
    private static int searchIndex = 0;//搜索内容坐标，便于下一个

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //加载小说
        loadNovel();
        //初始化
        loadReaderConfig();//加载页面设置
        initLoad();//窗口初始化
        initEventHandler();//初始化事件处理
        listSelect();//加载目录设置
        voiceRead();//初始化朗读
        initSetting();//初始化设置
    }

    //目录选择
    private void listSelect() {
        list.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                int index = list.getSelectionModel().getSelectedIndex();
                this.index = index;
                loadChapter(index);
                changePageHandler();
                list.setVisible(false);
            }
        });
    }

    //自适应页宽
    private void autoSize() {
        Platform.runLater(() -> {
            //获取当前窗口宽高
            double width = DataManager.readerStage.getWidth();
            //如果窗口宽度低于内容初始宽度，进行缩放，直至小于窗口最小宽度
            if (width < contentMaxWidth) {
                content.maxWidthProperty().bind(root.widthProperty());
            } else if (width > contentMaxWidth) {
                content.maxWidthProperty().bind(root.widthProperty().subtract(width - contentMaxWidth));
            }
        });
    }

    //初始化窗口事件，只一次
    private void initLoad() {
        //加载窗口改变事件
        DataManager.readerStage.widthProperty().addListener(e -> {
            autoSize();
        });
        DataManager.readerStage.heightProperty().addListener(e -> {
            autoSize();
        });
        //渲染完后加载第一次窗口绑定
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                LayoutUitl.bindStageSize(DataManager.readerStage, root);
                LayoutUitl.bindHeight(root, list, content);
                LayoutUitl.bindWidth(root, content, title);
                title.maxWidthProperty().bind(root.maxWidthProperty());
                title.layoutYProperty().bind(content.scrollTopProperty().multiply(-1));//标题位置随滚动条内容变化
                content.layoutXProperty().bind(root.layoutXProperty().add(DataManager.readerStage.widthProperty().subtract(content.maxWidthProperty()).divide(2).add(15)));
                //翻页区域绑定
                leftLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.2));
                leftLabel.prefHeightProperty().bind(root.heightProperty());
                rightLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.2));
                rightLabel.prefHeightProperty().bind(root.heightProperty());
                rightLabel.layoutXProperty().bind(root.layoutXProperty().add(root.widthProperty().subtract(rightLabel.widthProperty())));
                searchContent.layoutXProperty().bind(root.layoutXProperty().add(root.widthProperty()).subtract(searchContent.widthProperty().add(20)));
                autoSize();
                sp = (ScrollPane) content.lookup(".scroll-pane");//获取textarea的scrollPane;
                content.setScrollTop(DataManager.book.getvValue());
                changePageHandler();
            }
        });
        //窗口关闭事件
        DataManager.readerStage.setOnCloseRequest(e -> {
            DataManager.currentStage = DataManager.mainStage;
            new Thread(() -> {
                //关闭语音
                if (voiceUtil != null) voiceUtil.stop();
                //保存阅读器配置
                ReaderMapper mapper = MybatisUtil.getMapper(ReaderMapper.class);
                DataManager.readerConfig.setFontSize(content.getFont().getSize());//字体大小
                DataManager.readerConfig.setFontStyle(content.getFont().getFamily());//字体样式
                DataManager.readerConfig.setPageWidth(contentMaxWidth);//页宽
                DataManager.readerConfig.setStageHeight(DataManager.readerStage.getHeight());//舞台高度
                DataManager.readerConfig.setStageWidth(DataManager.readerStage.getWidth());//舞台宽度
                mapper.updateConfig(DataManager.readerConfig);
                MybatisUtil.getCurrentSqlSession().close();
                //保存新的书信息阅读位置
                NovelMapper bookMapper = MybatisUtil.getMapper(NovelMapper.class);
                bookMapper.updateCPage(DataManager.book.getId(), index, content.getScrollTop());
                MybatisUtil.getCurrentSqlSession().close();
            }).start();
            DataManager.mainStage.show();
        });

        //上下文菜单
        ContextMenu contextMenu = new ContextMenu();
        MenuItem preChapter = new MenuItem("上一章节", new ImageView("images/设置页/上一章.jpg"));
        MenuItem nextChapter = new MenuItem("下一章节", new ImageView("images/设置页/下一章.jpg"));
        MenuItem set = new MenuItem("设置面板", new ImageView("images/设置页/设置.jpg"));
        MenuItem chapter = new MenuItem("查看目录", new ImageView("images/设置页/查看目录.png"));
        MenuItem winTop = new MenuItem("窗口置顶", new ImageView("images/设置页/置顶.png"));
        MenuItem winHide = new MenuItem(itemText, new ImageView("images/设置页/隐藏.png"));
        MenuItem minTray = new MenuItem("最小化到托盘(Alt+U)", new ImageView("images/设置页/最小化到托盘.png"));
        contextMenu.getItems().addAll(chapter, nextChapter, preChapter, new SeparatorMenuItem(), set, new SeparatorMenuItem(), winTop, winHide, minTray);
        //章节目录
        chapter.setOnAction(e -> {
            list.setVisible(!list.isVisible());
        });
        //设置界面
        set.setOnAction(eee -> {
            setPane.setVisible(true);
        });
        //最小化托盘
        minTray.setOnAction(e -> {
            try {
                TrayUtil.tray();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        //窗口置顶
        winTop.setOnAction(e -> {
            if (winTop.getText().equals("窗口置顶")) {
                DataManager.readerStage.setAlwaysOnTop(true);
                winTop.setText("取消置顶");
            } else {
                DataManager.readerStage.setAlwaysOnTop(false);
                winTop.setText("窗口置顶");
            }
        });
        //换章
        preChapter.setOnAction(e -> loadChapter(--index));
        nextChapter.setOnAction(e -> loadChapter(++index));
        //窗口隐藏
        winHide.setOnAction(e -> {
            DataManager.book.setCpage(index);
            DataManager.book.setvValue(content.getScrollTop());
            DataManager.readerStage.close();
            double x = DataManager.readerStage.getX();
            double y = DataManager.readerStage.getY();
            double height = DataManager.readerStage.getHeight();
            double width = DataManager.readerStage.getWidth();
            Stage stage = new Stage();
            stage.setX(x);
            stage.setY(y);
            if (itemText.equals("隐藏边框")) {
                itemText = "显示边框";
                Reader reader = new Reader(StageStyle.UNDECORATED);
                try {
                    reader.start(stage);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                itemText = "隐藏边框";
                Reader reader = new Reader(StageStyle.DECORATED);
                try {
                    reader.start(stage);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            stage.setHeight(height);
            stage.setWidth(width);
        });
        this.content.setContextMenu(contextMenu);

    }

    //初始化事件处理
    void initEventHandler() {
        //搜索事件
        this.searchContent.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER:
                    String text = this.searchContent.getText().trim();
                    if (!content.getText().contains(text)) {
                        ToastUtil.toast("未匹配到结果", DataManager.readerStage);
                    } else {
                        if (text.equals(searchText)) {//上一次一样搜索内容
                            searchIndex = content.getText().indexOf(searchText, searchIndex > content.getText().length() ? 0 : (searchIndex + searchText.length()));
                        } else {
                            searchIndex = content.getText().indexOf(text);
                            searchText = text;
                        }
                        content.selectRange(searchIndex, searchIndex + searchText.length());
                    }
                case F:
                    if (e.isControlDown()) {
                        this.searchContent.setText("");
                        this.searchIndex = 0;
                        this.searchContent.setVisible(!this.searchContent.isVisible());
                    }
            }
        });
    }

    //加载阅读器配置
    void loadReaderConfig() {
        ReaderConfig config = DataManager.readerConfig;
        //舞台宽高
        DataManager.readerStage.setHeight(config.getStageHeight());
        DataManager.readerStage.setWidth(config.getStageWidth());
        //背景色
        changeColor(config.getBgColor(), config.getFontColor());
        //字体大小,样式
        content.setFont(Font.font(config.getFontStyle(), config.getFontSize()));
        title.setTextFill(Color.valueOf(config.getFontColor()));
        //页面宽度
        contentMaxWidth = config.getPageWidth();
        pageSize.setText((int) contentMaxWidth + "");
        autoSize();
    }

    void loadNovel() {
        if (DataManager.book.getIsWeb() == 1) {//加载网络小说
            list.setItems(FXCollections.observableArrayList(DataManager.wns.getChapters()));//加载目录
        } else {//加载本地书
            try {
                list.setItems(FXCollections.observableArrayList(DataManager.lns.getChapters()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loadChapter(index);
    }

    //加载一章节
    private void loadChapter(int index) {
        //停止朗读
        if (voiceUtil != null) voiceUtil.stop();
        voice.setSelected(false);
        if (DataManager.book.getIsWeb() == 1) {//网络书
            if (index >= list.getItems().size()) {
                title.setText("全书完");
                content.setText("\r\n\r\n这么快就看完了，快去看下一本吧");
                this.index = this.list.getItems().size() - 1;
                return;
            } else if (index < 0) {
                this.index = 0;
                index = 0;
            }
            final int i = index;
            if (firstLoading) {//第一次不用loading动画，为了绘制成功上次位置
                text = DataManager.wns.getContent(i);
                novelTitle = DataManager.wns.getChapters().get(i);
                this.content.setText(text);
                this.title.setText(novelTitle);
                firstLoading = false;
            } else {//加载loading
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        text = DataManager.wns.getContent(i);
                        novelTitle = DataManager.wns.getChapters().get(i);
                        return null;
                    }
                };
                ProgressFrom pf = new ProgressFrom(DataManager.readerStage, task);
                task.setOnSucceeded(e -> {
                    pf.cancelProgressBar();
                    this.content.setText(text);
                    this.title.setText(novelTitle);
                    //翻页顶部标志，因为需要loading与到底部共存，不得已放在这里，等待内容加载完毕，跳转底部，与changePage(false)对应
                    if (isPageTopOver) {
                        content.selectEnd();
                        content.deselect();
                        isPageDownOver = true;
                        isPageTopOver = false;
                    }
                    DataManager.readerStage.setTitle(title.getText());//设置标题
                });
                pf.activateProgressBar();
            }
            this.index = index;
        } else {//本地书
            try {
                //第一章与最后一章防止越界限
                if (index > DataManager.lns.getChapters().size() - 1) {
                    title.setText("全书完");
                    content.setText("\r\n\r\n这么快就看完了，快去看下一本吧");
                    this.index = DataManager.lns.getChapters().size() - 1;
                    return;
                } else if (index < 0) {
                    this.index = 0;
                    index = 0;
                }
                Map<String, String> page = DataManager.lns.getCPageByIndex(index);
                //加载章节到UI
                title.setText(page.get("title"));
                this.content.setText(page.get("content"));
                this.index = index;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DataManager.readerStage.setTitle(title.getText());//设置标题
    }

    //加载章节
    //方向键，鼠标翻页换章
    void changePageHandler() {
        //方向键翻页、换章节
        content.requestFocus();
        content.setOnKeyPressed(e -> {
            e.consume();
            switch (e.getCode()) {
                case RIGHT:
                    loadChapter(++index);
                    sp.setVvalue(0);
                    return;
                case LEFT:
                    loadChapter(--index);
                    sp.setVvalue(0);
                    return;
                case UP:
                    changePage(false);
                    break;
                case DOWN:
                    changePage(true);
                    break;
                case F11:
                    DataManager.readerStage.setFullScreen(true);
                    return;
                case SPACE:
                    changePage(true);
                    break;
                case F2:
                    DataManager.readerStage.setIconified(true);
                    break;
                case F:
                    if (e.isControlDown()) {
                        this.searchContent.setText("");
                        this.searchIndex = 0;
                        this.searchContent.setVisible(!this.searchContent.isVisible());
                        if (this.searchContent.isVisible()) {
                            this.searchContent.requestFocus();
                        }
                    }
            }
        });
        //滚动翻页换章节
        content.setOnScroll((ScrollEvent event) -> {
            event.consume();
            scrollNum++;
            if (event.getDeltaY() == 13 && scrollNum > 5) {//顶部（滚动次数大于10防止误触）
                loadChapter(--index);
                scrollNum = 0;
            } else if (event.getDeltaY() == -13 && scrollNum > 5) {//底部
                loadChapter(++index);
                content.setScrollTop(0);
                scrollNum = 0;
            }
        });
    }

    //语音朗读
    void voiceRead() {
        voice.setOnAction(e -> {
            if (voice.isSelected()) {
                voiceUtil = new VoiceUtil();
                voiceUtil.readText(title.getText() + content.getText());
            } else {
                if (voiceUtil != null) voiceUtil.stop();
            }
            voice.setSelected(voice.isSelected());
        });
    }

    //设置
    void initSetting() {
        //绑定位置在右下角
        setPane.layoutYProperty().bind(root.layoutYProperty().add(root.heightProperty()).subtract(290));
        setPane.layoutXProperty().bind(root.layoutXProperty().add(root.widthProperty()).subtract(230));
        //背景色更换
        molv.setOnMouseClicked(e -> changeColor(" #5e8e87", "#F0F0F0"));
        huyan.setOnMouseClicked(e -> changeColor("#CEEBCE", "#333333"));
        anse.setOnMouseClicked(e -> changeColor("#808A87", "#ddd"));
        heise.setOnMouseClicked(e -> changeColor("#393D49", "#c2c2c2"));
        yangpi.setOnMouseClicked(e -> changeColor("#e6dbbf", "#333333"));
        baise.setOnMouseClicked(e -> changeColor("#F0F0F0", "#333333"));
        //翻页区域点击
        rightLabel.setOnMouseClicked(e -> {
            e.consume();
            changePage(true);
        });
        leftLabel.setOnMouseClicked(e -> {
            e.consume();
            changePage(false);
        });
        //显示隐藏目录
        chapter.setOnMouseClicked(e -> {
            e.consume();
            list.setVisible(!list.isVisible());
        });
        //字体大小改变
        fontAdd.setOnMouseClicked(e -> {
            content.setFont(Font.font(content.getFont().getFamily(), content.getFont().getSize() + 1));
        });
        fontless.setOnMouseClicked(e -> {
            content.setFont(Font.font(content.getFont().getFamily(), content.getFont().getSize() - 1));
        });
        //页面宽度
        pageLess.setOnMouseClicked(e -> {
            if (contentMaxWidth <= 850) {
                return;
            }
            contentMaxWidth -= 50;
            pageSize.setText((int) contentMaxWidth + "");
            autoSize();
        });
        pageAdd.setOnMouseClicked(e -> {
            if (contentMaxWidth >= 1500) {
                return;
            }
            contentMaxWidth += 50;
            pageSize.setText((int) contentMaxWidth + "");
            autoSize();
        });
        //字体设置
        kaiti.setOnMouseClicked(event -> {
            content.setFont(Font.font("KaiTi", content.getFont().getSize()));
        });
        song.setOnMouseClicked(event -> {
            content.setFont(Font.font("SimSun", content.getFont().getSize()));
        });
        yahei.setOnMouseClicked(e -> {
            content.setFont(Font.font("Microsoft YaHei", content.getFont().getSize()));
        });
        hideSet.setOnMouseClicked(e -> setPane.setVisible(false));
        //图标设置
        fontText.setGraphic(new ImageView("images/设置页/字体.png"));
        fontAdd.setGraphic(new ImageView("images/设置页/字体放大.png"));
        fontless.setGraphic(new ImageView("images/设置页/字体缩小.png"));
        readLabel.setGraphic(new ImageView("images/设置页/朗读.png"));
        hideSet.setGraphic(new ImageView("images/设置页/关闭.jpg"));
        chapter.setGraphic(new ImageView("images/设置页/目录.jpg"));
        pageAdd.setGraphic(new ImageView("images/设置页/阅读页_页面增大.jpg"));
        pageLess.setGraphic(new ImageView("images/设置页/阅读页_页面缩小.jpg"));
        pageWidth.setGraphic(new ImageView("images/设置页/页面大小.jpg"));
        fontStyle.setGraphic(new ImageView("images/设置页/字体样式.jpg"));
    }

    // 切换背景
    public void changeColor(String color, String fontcolor) {
        content.setStyle("-fx-text-fill: " + fontcolor + ";");
        root.setStyle("-fx-background-color: " + color + ";");
        title.setTextFill(Color.valueOf(fontcolor));
        //更新配置
        DataManager.readerConfig.setBgColor(color);
        DataManager.readerConfig.setFontColor(fontcolor);
    }

    /**
     * 左右点击按键翻页
     * @param isRight
     */
    private void changePage(boolean isRight) {
        //向右边翻页，向到底部自动翻页到下一节
        if (isRight) {
            content.setScrollTop(content.getScrollTop() + content.getHeight() - 40);
            if (sp.getVvalue() == 1.0) {
                if (!isPageDownOver) {
                    isPageDownOver = true;
                } else {
                    loadChapter(++index);
                    sp.setVvalue(0);
                    isPageDownOver = false;
                }
            }
        } else {//向左翻页，到底部自动加载上一页
            content.setScrollTop(content.getScrollTop() - content.getHeight() + 40);
            if (sp.getVvalue() == 0) {
                if (!isPageTopOver) {
                    isPageTopOver = true;
                } else {
                    loadChapter(--index);
                }
            }
        }
    }
}
