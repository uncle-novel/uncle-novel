package com.unclezs.gui.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXMasonryPane;
import com.unclezs.crawl.LocalNovelLoader;
import com.unclezs.crawl.NovelLoader;
import com.unclezs.crawl.WebNovelLoader;
import com.unclezs.downloader.NovelDownloader;
import com.unclezs.downloader.TTSDownloader;
import com.unclezs.downloader.config.DownloadConfig;
import com.unclezs.gui.app.App;
import com.unclezs.gui.app.Reader;
import com.unclezs.gui.components.AbstractLoadingTask;
import com.unclezs.gui.components.BookNode;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.AlertUtil;
import com.unclezs.gui.utils.ApplicationUtil;
import com.unclezs.gui.utils.ContentUtil;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.gui.utils.DesktopUtil;
import com.unclezs.gui.utils.NodeUtil;
import com.unclezs.gui.utils.ToastUtil;
import com.unclezs.mapper.BookMapper;
import com.unclezs.model.Book;
import com.unclezs.model.Chapter;
import com.unclezs.utils.MybatisUtil;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 书架页面
 *
 * @author unclezs.com
 * @date 2019.06.22 09:31
 */
@Slf4j
@FXController("book_shelf")
public class BookShelfController implements LifeCycleFxController {
    public StackPane root;
    public JFXMasonryPane bookPane;
    public ScrollPane scrollPane;
    public ContextMenu contextMenu;
    public Label title;
    public Book selectedBook = null;
    public BookNode selectedNode = null;
    private JFXAlert detailsBox;
    private JFXListView<String> updateList;

    @Override
    public void initialize() {
        initEventHandler();
        Platform.runLater(this::loadBooks);
    }

    @Override
    public void onShow(Dict data) {
        scrollPane.requestLayout();
    }

    /**
     * 初始化所有事件监听
     */
    private void initEventHandler() {
        //文件拖入事件
        root.setOnDragOver((DragEvent e) -> {
            File file = e.getDragboard().getFiles().get(0);
            if (file.getAbsolutePath().endsWith(".txt")) {
                e.acceptTransferModes(TransferMode.LINK);
            }
        });
        root.setOnDragDropped((DragEvent e) -> {
            File file = e.getDragboard().getFiles().get(0);
            if (e.isAccepted()) {
                addLocalBook(file);
            }
        });
    }

    /**
     * 导入本地书
     */
    public void importLocalBook() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("文本文件", "*.txt");
        fileChooser.getExtensionFilters().addAll(filter);
        File file = fileChooser.showOpenDialog(DataManager.currentStage);
        if (file == null) {
            return;
        }
        addLocalBook(file);
    }

    /**
     * 添加一本书
     *
     * @param file /
     */
    private void addLocalBook(File file) {
        //加载loading
        AbstractLoadingTask<BookNode> task = new AbstractLoadingTask<BookNode>() {
            @Override
            protected BookNode call() {
                String path = file.getPath();
                String name = FileUtil.mainName(file);
                LocalNovelLoader loader = new LocalNovelLoader();
                loader.load(path);
                Book book = new Book(name, path, "");
                book.setId(loader.getId());
                //格式化存储
                String configPath = loader.store();
                book.setChapterPath(configPath);
                book.setWeb(false);
                if (isCancelled()) {
                    throw new RuntimeException();
                } else {
                    MybatisUtil.execute(BookMapper.class, mapper -> mapper.insert(book));
                    return new BookNode(book);
                }
            }
        };
        task.setSuccessHandler(e -> {
            addBook(task.getValue());
            scrollPane.requestLayout();
        });
        ThreadUtil.execute(task);
    }

    void addBook(BookNode node) {
        bookPane.getChildren().add(node);
    }

    /**
     * 加载书
     */
    private void loadBooks() {
        long l = System.currentTimeMillis();
        AbstractLoadingTask<List<BookNode>> task = new AbstractLoadingTask<List<BookNode>>() {
            @Override
            protected List<BookNode> call() {
                return MybatisUtil.execute(BookMapper.class, m -> m.selectList(null)).stream().map(
                    BookNode::new).collect(Collectors.toList());
            }
        };
        task.setSuccessHandler(e -> {
            bookPane.getChildren().setAll(task.getValue());
            bookPane.requestLayout();
            //显示更新内容
            log.info("书架查库耗时：{}ms", (System.currentTimeMillis() - l));
        });
        //完成后设置内容
        ThreadUtil.execute(task);
    }


    /**
     * 更新章节目录
     */
    public void updateChapters() {
        if (!selectedBook.isWeb()) {
            ToastUtil.warning("本地书籍不可更新");
            return;
        }
        AbstractLoadingTask<List<String>> task = new AbstractLoadingTask<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                return updateChapters(selectedBook);
            }
        };
        task.setSuccessHandler(e -> {
            if (task.getValue().isEmpty()) {
                ToastUtil.success("暂无更新");
            } else {
                showUpdateList(task.getValue());
                ToastUtil.success("发现并已经更新章节");
            }
        });
        ThreadUtil.execute(task);
    }

    private List<String> updateChapters(Book book) throws IOException {
        WebNovelLoader loader = new WebNovelLoader();
        loader.load(book);
        List<Chapter> chapters = loader.getSpider().chapters(book.getPath());
        int count = loader.chapters().size() + loader.getConfig().getBlackList().size();
        if (chapters.size() == count) {
            return Collections.emptyList();
        }
        List<Chapter> update = chapters.stream()
            .filter(c -> !loader.getConfig().getBlackList().contains(c.getUrl()))
            .collect(Collectors.toList());
        List<Chapter> newChapters = update.subList(loader.chapters().size(), update.size());
        loader.chapters().addAll(newChapters);
        loader.store();
        return newChapters.stream().map(Chapter::getName).collect(Collectors.toList());
    }

    /**
     * 重命名书籍
     */
    public void newName() {
        AlertUtil.input("重命名", selectedBook.getName(), "输入新的书名", name -> {
            selectedNode.getTitle().setText(name);
            selectedBook.setName(name);
            ThreadUtil.execute(() -> MybatisUtil.execute(BookMapper.class, mapper -> mapper.updateById(selectedBook)));
        });
    }

    /**
     * 更换封面
     */
    public void newCover() {
        DesktopUtil.selectImage(path -> {
            try {
                selectedNode.requestLayout();
                String cover = ApplicationUtil.saveImage(path, selectedBook.getName());
                selectedNode.getCover().setImage(new Image("file:" + cover));
                selectedBook.setCover(cover);
                ThreadUtil.execute(
                    () -> MybatisUtil.execute(BookMapper.class, mapper -> mapper.updateById(selectedBook)));
            } catch (IOException ignored) {
            }
        });
    }


    /**
     * 删除一本书
     */
    public void remove() {
        ThreadUtil.execute(() -> {
            MybatisUtil.execute(BookMapper.class, mapper -> mapper.deleteById(selectedBook));
            //删除相关文件
            File file = FileUtil.file(selectedBook.getChapterPath());
            FileUtil.del(file.getParentFile());
            if (StrUtil.isNotEmpty(selectedBook.getCover())) {
                FileUtil.del(selectedBook.getCover());
            }
        });
        bookPane.getChildren().remove(selectedNode);
    }

    /**
     * 打开阅读器
     */
    public void open() {
        if (!FileUtil.exist(selectedBook.getChapterPath())) {
            log.warn("文本小说【{}】文件不存在", selectedBook.getName());
            ToastUtil.error("书籍不存在");
            return;
        }
        NovelLoader loader;
        if (selectedBook.isWeb()) {
            loader = new WebNovelLoader();
        } else {
            loader = new LocalNovelLoader();
        }
        loader.load(selectedBook);
        Reader.controller.loadBook(loader);
        if (ReaderController.firstLoad) {
            long l = System.currentTimeMillis();
            Reader.stage.show();
            App.stage.close();
            log.info("初始化阅读器耗时：{}ms", System.currentTimeMillis() - l);
        } else {
            Reader.stage.show();
            App.stage.close();
        }
    }

    /**
     * 查看详情
     */
    private void showUpdateList(List<String> chapter) {
        if (detailsBox == null) {
            detailsBox = new JFXAlert(DataManager.currentStage);
            JFXDialogLayout layout = new JFXDialogLayout();
            title = new Label(selectedBook.getName());
            layout.setHeading(title);
            updateList = new JFXListView<>();
            updateList.getStyleClass().setAll("bg-transparent-all");
            layout.setBody(updateList);
            detailsBox.setContent(NodeUtil.createBgPane(layout));
        }
        updateList.getItems().setAll(chapter);
        title.setText(selectedBook.getName() + "  -  最新章节列表");
        detailsBox.show();
    }


    /**
     * 添加下载
     */
    public void download() throws IOException {
        if (selectedBook.isWeb()) {
            ToastUtil.warning("本地书籍不可以下载");
        }
        WebNovelLoader loader = new WebNovelLoader();
        loader.load(selectedBook);
        WebNovelLoader.Config config = loader.getConfig();
        DownloadConfig setting = new DownloadConfig(DataManager.application.getSetting());
        NovelDownloader downloader =
            new NovelDownloader(loader.chapters(), setting, selectedBook.getName(), config.getRule());
        ContentUtil.getController(DownloadController.class).addTask(downloader);
        ToastUtil.success("添加下载成功");
    }

    /**
     * 文本合成语音
     *
     * @throws IOException /
     */
    public void toAudio() throws IOException {
        TTSDownloader downloader =
            new TTSDownloader(selectedBook, DataManager.application.getSetting().getSavePath().get());
        ContentUtil.getController(DownloadController.class).addTask(downloader);
        ToastUtil.success("添加文本转语音任务成功");
    }
}
