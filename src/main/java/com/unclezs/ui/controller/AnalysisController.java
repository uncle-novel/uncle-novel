package com.unclezs.ui.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.jfoenix.controls.*;
import com.unclezs.crawl.NovelSpider;
import com.unclezs.downloader.NovelDownloader;
import com.unclezs.mapper.AnalysisMapper;
import com.unclezs.mapper.ChapterMapper;
import com.unclezs.mapper.NovelMapper;
import com.unclezs.mapper.SettingMapper;
import com.unclezs.model.AnalysisConfig;
import com.unclezs.model.Book;
import com.unclezs.model.Chapter;
import com.unclezs.model.DownloadConfig;
import com.unclezs.ui.node.ProgressFrom;
import com.unclezs.ui.utils.DataManager;
import com.unclezs.ui.utils.LayoutUitl;
import com.unclezs.ui.utils.ToastUtil;
import com.unclezs.utils.ConfUtil;
import com.unclezs.utils.OsUtil;
import com.unclezs.utils.MybatisUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.ibatis.session.SqlSession;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/*
 *解析下载页面
 *@author unclezs.com
 *@date 2019.06.26 16:44
 */
public class AnalysisController implements Initializable {

    //解析设置面板配置
    @FXML
    VBox menuPane;//菜单面板
    @FXML
    TextArea chapterHeadText, cookiesText, UAText, AdText, chapterTailText, contentHeadText, contentTailText;
    @FXML
    JFXRadioButton chapterFilterUse, chapterSortUse, NCRToXZhUse, TraToSimpleUse, rule1, rule2, rule3, startDynamic;
    //其他
    @FXML
    JFXTextField text;//输入框
    @FXML
    JFXListView<JFXCheckBox> list;//章节listView
    @FXML
    JFXTextArea content;//解析正文
    @FXML
    Label menu;//菜单
    @FXML
    Pane analysisRoot, doPane;//根容器
    @FXML
    JFXButton saveConfigBtn, analysisBtn, addToMark, downloadIt;//解析、加入书架按钮

    //成员
    ContextMenu contextMenu = new ContextMenu();
    private List<Chapter> chapters;//章节列表
    private AnalysisConfig config = new AnalysisConfig();//解析配置
    private NovelSpider spider;//爬虫
    private boolean startSelected = false;//shift多选开启标志
    private int startIndex;//多选开始位置

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    void init() {
        //绑定匹配规则单选
        ToggleGroup group = new ToggleGroup();
        rule1.setToggleGroup(group);
        rule2.setToggleGroup(group);
        rule3.setToggleGroup(group);
        //自适应
        autoSize();
        saveConfig();//加载解析配置
        initContextMenu();//右键菜单
        //自动导入剪贴板内容到输入框
        if (DataManager.content.getChildren().size() > 0 && DataManager.content.getChildren().get(0).getId().equals("searchRoot")) {
            autoImportClibord(true);
        }
        //解析章节
        analysisBtn.setOnMouseClicked(e -> {
            //防空
            String url = this.text.getText();
            if ("".equals(url) || url.equals(null) || !url.startsWith("http")) {
                ToastUtil.toast("请先输入文章目录地址");
                return;
            }
            analysisChapter();
        });
        //双击章节显示章节内容
        list.setOnMouseClicked(event -> {
            if (event.isShiftDown()) {//shift多选
                if (!startSelected) {
                    startSelected = true;
                    startIndex = list.getSelectionModel().getSelectedIndex();
                    list.getItems().get(startIndex).setSelected(!list.getItems().get(startIndex).isSelected());
                    return;
                }
                if (startSelected) {
                    int end = list.getSelectionModel().getSelectedIndex();
                    for (int i = startIndex + 1; i <= end; i++) {
                        list.getItems().get(i).setSelected(!list.getItems().get(i).isSelected());
                    }
                    startSelected = false;
                }
            } else {//非多选请求
                if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY && list.getItems().size() > 0) {
                    int index = list.getSelectionModel().getSelectedIndex();
                    showChapterContent(index);
                }
                if (event.getButton() == MouseButton.SECONDARY && list.getItems().size() > 0) {
                    contextMenu.setY(event.getScreenY());
                    contextMenu.setX(event.getScreenX());
                    contextMenu.getItems().get(2).setOnAction(e -> {//全选
                        for (JFXCheckBox cb : list.getItems()) {
                            cb.setSelected(true);
                        }
                    });
                    contextMenu.getItems().get(3).setOnAction(e -> {//全不选
                        for (JFXCheckBox cb : list.getItems()) {
                            cb.setSelected(false);
                        }
                    });
                    contextMenu.getItems().get(0).setOnAction(e -> {
                        showChapterContent(list.getSelectionModel().getSelectedIndex());
                    });
                    contextMenu.getItems().get(5).setOnAction(e -> {
                        reNameChapterNames();
                    });
                    contextMenu.show(DataManager.mainStage);
                }
            }
        });
        //添加到书架
        addToMark.setOnMouseClicked(e -> {
            addToBookSelf();
        });
        //菜单按钮
        menu.setOnMouseClicked(e -> {
            menuPane.setVisible(!menuPane.isVisible());
        });
        //保存解析配置
        saveConfigBtn.setOnMouseClicked(e -> {
            saveConfig();
            menuPane.setVisible(false);
            ToastUtil.toast("保存成功");
        });
        //点击输入框自动导入剪贴板内容
        if (ConfUtil.get(ConfUtil.USE_ANALYSIS_PASTE).equals("true")) {
            text.setOnMouseClicked(e -> {
                autoImportClibord(false);
                //光标移动到末尾
                text.selectEnd();
                text.deselect();
            });
        }
        //下载
        downloadIt.setOnMouseClicked(e -> {
            downloadBook();
        });
    }

    //显示章节内容
    private void showChapterContent(int index) {
        content.setText("正在获取章节：" + chapters.get(index).getChapterName());
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                Map<String, String> config = spider.getConfig();
                String text = spider.getContent(chapters.get(index).getChapterUrl(), config.get("charset"));
                return text;
            }
        };
        new Thread(task).start();
        task.setOnSucceeded(e -> {
            String text = task.getValue();
            if (text == null || "".equals(text)) {
                ToastUtil.toast("没有匹配到正文，可以换个匹配规则试试！");
                content.setText("没有匹配到正文，可以换个匹配规则试试！");
            } else {
                content.setText(text);
            }
        });
    }

    //解析章节目录
    private void analysisChapter() {
        String url = this.text.getText();
        //防止空指针
        if ("".equals(url) || url.equals(null) || !url.startsWith("http")) {
            return;
        }
        Platform.runLater(() -> {
            list.getItems().clear(); //清除原有的
            chapters = new ArrayList<>(500);
            Task<List<Chapter>> task = new Task<List<Chapter>>() {
                @Override
                protected List<Chapter> call() throws Exception {
                    //爬取章节列表
                    chapters = spider.getChapterList(url);
                    return chapters;
                }
            };
            ProgressFrom pf = new ProgressFrom(DataManager.mainStage, task);
            task.setOnSucceeded(e -> {
                //加入listView
                for (Chapter c : task.getValue()) {
                    JFXCheckBox cb = new JFXCheckBox();
                    cb.setText(c.getChapterName());
                    cb.setSelected(true);
                    list.getItems().add(cb);
                }
                pf.cancelProgressBar();
            });
            pf.activateProgressBar();
        });
    }

    //初始化右键菜单
    private void initContextMenu() {
        MenuItem selectAll = new MenuItem("全选", new ImageView("images/解析页/全选.jpg"));
        MenuItem showContent = new MenuItem("查看内容", new ImageView("images/解析页/查看.jpg"));
        MenuItem unSelectAll = new MenuItem("全不选", new ImageView("images/解析页/反选.jpg"));
        MenuItem reName = new MenuItem("重新命名章节序号", new ImageView("images/书架/修改.jpg"));
        contextMenu.getItems().addAll(showContent, new SeparatorMenuItem(), selectAll, unSelectAll, new SeparatorMenuItem(), reName);
    }

    //添加到书架
    private void addToBookSelf() {
        if (chapters == null || chapters.size() == 0) {
            ToastUtil.toast("请先解析目录后再添加！");
            return;
        }
        List<String> selectedNameItems = new ArrayList<>();//选中的章节名字列表
        //赛选出选中的条目
        for (JFXCheckBox cb : list.getItems()) {
            if (cb.isSelected()) {
                selectedNameItems.add(cb.getText());
            }
        }
        //loading
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                Map<String, String> config = spider.getConfig();
                String name = config.get("title");
                String charset = config.get("charset");
                String homeUrl = text.getText();
                //封面下载
                String imgUrl = spider.crawlDescImage(name);
                String path = FileUtil.file("./image/" + name + ".jpg").getAbsolutePath();
                HttpUtil.downloadFile(imgUrl, path);
                //保存书籍信息
                Book book = new Book(name, homeUrl, path);
                book.setCharset(charset);
                book.setIsWeb(1);//标记为网络书籍
                SqlSession sqlSession = MybatisUtil.openSqlSession(true);//开启sqlSession
                NovelMapper mapper = sqlSession.getMapper(NovelMapper.class);
                mapper.save(book);
                //保存选中的章节信息
                Integer id = mapper.findLastOne().getId();
                List<Chapter> cs = new ArrayList<>();
                for (Chapter c : chapters) {
                    if (selectedNameItems.contains(c.getChapterName())) {
                        cs.add(new Chapter(c.getChapterName(), c.getChapterUrl(), id));
                    }
                }
                sqlSession.getMapper(ChapterMapper.class).saveChapters(cs);//保存章节信息入库
                sqlSession.getMapper(AnalysisMapper.class).saveAnalysisConfig(spider.getConf(), id);//保存解析器配置
                sqlSession.close();
                DataManager.needReloadBookSelf = true;//刷新书架
                return null;
            }
        };
        ProgressFrom pf = new ProgressFrom(DataManager.mainStage, task);
        task.setOnSucceeded(e -> {
            pf.cancelProgressBar();
            ToastUtil.toast("添加成功!");
        });
        pf.activateProgressBar();
    }

    //保存配置
    private void saveConfig() {
        config.setAdStr(AdText.getText());
        config.setCookies(cookiesText.getText());
        config.setChapterFilter(chapterFilterUse.isSelected());
        config.setChapterHead(chapterHeadText.getText());
        config.setChapterSort(chapterSortUse.isSelected());
        config.setChapterTail(chapterTailText.getText());
        config.setContentHead(contentHeadText.getText());
        config.setContentTail(contentTailText.getText());
        config.setNcrToZh(NCRToXZhUse.isSelected());
        config.setTraToSimple(TraToSimpleUse.isSelected());
        if (rule3.isSelected()) {
            config.setRule("3");
        } else {
            config.setRule(rule1.isSelected() ? "1" : "2");
        }
        config.setStartDynamic(startDynamic.isSelected());
        config.setUserAgent(UAText.getText());
        if (spider == null) {
            spider = new NovelSpider(config);
        } else {
            spider.setConf(config);//添加到爬虫配置
        }
    }

    //自适应
    private void autoSize() {
        LayoutUitl.bind(DataManager.root, analysisRoot);
        list.prefWidthProperty().bind(analysisRoot.widthProperty().divide(2).subtract(10));
        content.prefWidthProperty().bind(list.prefWidthProperty().subtract(25));
        content.layoutXProperty().bind(list.layoutXProperty().add(list.widthProperty()).add(20));
        content.prefHeightProperty().bind(analysisRoot.heightProperty().subtract(100));
        list.prefHeightProperty().bind(content.heightProperty());
        menu.layoutXProperty().bind(analysisRoot.layoutXProperty().add(analysisRoot.widthProperty()).subtract(menu.widthProperty()).subtract(10));
        menuPane.layoutXProperty().bind(analysisRoot.layoutXProperty().add(analysisRoot.widthProperty()).subtract(menuPane.widthProperty()));
        doPane.layoutXProperty().bind(analysisRoot.layoutXProperty().add(analysisRoot.widthProperty().divide(2)).subtract(doPane.widthProperty().divide(2)));
    }

    //自动导入剪贴班链接
    private void autoImportClibord(boolean isAnalysis) {
        Clipboard cb = Clipboard.getSystemClipboard();
        String url = cb.getString();
        if (url != null && !"".equals(url) && url.startsWith("http")) {
            text.setText(url);
            if (isAnalysis) {//如果需要解析则，导入自动后解析
                analysisChapter();
            }
        }
    }

    //下载本书
    private void downloadBook() {
        String r = "[第]{0,1}.+?[章]{0,1}";
        if (list.getItems().size() == 0) {
            ToastUtil.toast("请先解析目录！");
            return;
        }
        SettingMapper mapper = MybatisUtil.getMapper(SettingMapper.class);
        DownloadConfig config = mapper.querySetting();
        if ("".equals(config.getPath()) || config.getPath() == null) {//路径不为空的时候使用当前路径
            config.setPath(new File("./").getAbsolutePath().replace(".", ""));
            mapper.updateSetting(config);
        } else if (!new File(config.getPath()).exists()) {
            ToastUtil.toast("保存路径不存在！");
            return;
        }
        //赛选出选中的条目
        List<String> taskUrlList = new ArrayList<>();
        List<String> selectedNameItems = new ArrayList<>();
        for (int i = 0; i < list.getItems().size(); i++) {
            if (list.getItems().get(i).isSelected()) {
                taskUrlList.add(chapters.get(i).getChapterUrl());
                selectedNameItems.add(chapters.get(i).getChapterName());
            }
        }
        //复制Bean，（填坑。。。）
        AnalysisConfig analysisConfig=new AnalysisConfig();
        BeanUtil.copyProperties(spider.getConf(),analysisConfig);
        NovelSpider novelSpider=new NovelSpider(analysisConfig);
        BeanUtil.copyProperties(spider,novelSpider);
        NovelDownloader downloader = new NovelDownloader(taskUrlList, selectedNameItems, config, novelSpider);
        DownloadController.addTask(downloader);
        ThreadUtil.execute(downloader::start);
        ToastUtil.toast("添加下载任务成功！");
    }

    //重命名章节序号
    private void reNameChapterNames() {
        //赛选出选中的条目
        int index = 1;
        for (int i = 0; i < list.getItems().size(); i++) {
            if (list.getItems().get(i).isSelected()) {
                String s = this.chapters.get(i).getChapterName();
                s = s.replaceAll("[0-9]", "")//去掉所有数字
                        .replaceAll("第.*?章", "");
                String newName = "第" + (index) + "章 " + s;
                this.chapters.get(i).setChapterName(newName);//更新章节数据
                this.list.getItems().get(i).setText(newName);//更新listView
                index++;
            }
        }
    }
}
