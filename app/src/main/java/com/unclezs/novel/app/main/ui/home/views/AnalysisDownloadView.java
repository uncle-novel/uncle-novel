package com.unclezs.novel.app.main.ui.home.views;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.jfoenix.controls.JFXNodesList;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.analyzer.spider.TocSpider;
import com.unclezs.novel.analyzer.util.SerializationUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.InputBox;
import com.unclezs.novel.app.framework.components.InputBox.ActionClickedEvent;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.util.DesktopUtils;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.main.manager.RuleManager;
import com.unclezs.novel.app.main.model.ChapterWrapper;
import com.unclezs.novel.app.main.model.DownloadBundle;
import com.unclezs.novel.app.main.ui.home.views.widgets.BookDetailNode;
import com.unclezs.novel.app.main.ui.home.views.widgets.ChapterListCell;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.EqualsAndHashCode;

/**
 * @author blog.unclezs.com
 * @date 2021/4/25 9:40
 */
@FxView(fxml = "/layout/home/views/analysis-download.fxml")
@EqualsAndHashCode(callSuper = true)
public class AnalysisDownloadView extends SidebarView<StackPane> {

  /**
   * 传递小说详情数据
   */
  public static final String BUNDLE_KEY_NOVEL_INFO = "novel-info";
  private final TextArea content = new TextArea();
  @FXML
  private JFXNodesList floatButtons;
  @FXML
  private HBox contentPanel;
  @FXML
  private ListView<ChapterWrapper> listView;
  @FXML
  private InputBox inputBox;
  private AnalyzerRule rule;
  private Novel novel;

  @Override
  public void onCreate() {
    TextField input = inputBox.getInput();
    input.setText("https://m.zhaishuyuan.com/read/33959");
    // 监听剪贴板
    input.focusedProperty().addListener(e -> {
      if (input.isFocused()) {
        String tocUrl = Clipboard.getSystemClipboard().getString();
        if (!Objects.equals(input.getText(), tocUrl) && UrlUtils.isHttpUrl(tocUrl)) {
          input.setText(tocUrl);
        }
      }
    });
    listView.setCellFactory(param -> new ChapterListCell());
    content.setWrapText(true);
    content.prefWidthProperty().bind(contentPanel.widthProperty().multiply(0.5));
    listView.prefWidthProperty().bind(contentPanel.widthProperty().multiply(0.5));
    contentPanel.getChildren().remove(content);
    listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    // 左键双击解析正文
    EventUtils.setOnMouseDoubleClick(listView, event -> analysisContent());
    // 空数据不弹出上下文菜单
    listView.setOnContextMenuRequested(e -> {
      if (listView.getSelectionModel().isEmpty()) {
        listView.getContextMenu().hide();
      }
    });
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    Novel novelInfo = bundle.get(BUNDLE_KEY_NOVEL_INFO);
    if (novelInfo != null) {
      this.novel = novelInfo;
      inputBox.getInput().setText(this.novel.getUrl());
      inputBox.fire();
    }
  }

  public void onAnalysis(ActionClickedEvent event) {
    String tocUrl = event.getInput();
    if (!UrlUtils.isHttpUrl(tocUrl)) {
      Toast.error(getRoot(), "请输入正确的目录地址");
      return;
    }
    listView.getItems().clear();
    contentPanel.getChildren().remove(content);
    if (this.novel != null && !Objects.equals(tocUrl, novel.getUrl())) {
      this.novel = null;
    }
    // 非同一个网站，重新获取规则，这时如果未保存书源则会丢弃修改
    if (rule == null || !Objects.equals(rule.getSite(), UrlUtils.getSite(tocUrl))) {
      this.rule = RuleManager.getOrDefault(tocUrl);
    }
    TocSpider tocSpider = new TocSpider(rule);
    tocSpider.setOnNewItemAddHandler(chapter -> Executor.runFx(() -> listView.getItems().add(new ChapterWrapper(chapter))));
    TaskFactory.create(() -> {
      tocSpider.toc(tocUrl);
      // 小说详情
      Novel novelInfo = tocSpider.getNovel();
      if (novel != null) {
        BeanUtil.copyProperties(novel, novelInfo, CopyOptions.create().ignoreNullValue());
      }
      this.novel = novelInfo;
      this.novel.setUrl(tocUrl);
      // 加载全部
      tocSpider.loadAll();
      return null;
    }).onSuccess(value -> {
      if (StringUtils.isBlank(this.rule.getName())) {
        this.rule.setName(novel.getTitle());
      }
    }).start();
  }

  /**
   * 解析正文
   */
  @FXML
  private void analysisContent() {
    if (!listView.getSelectionModel().isEmpty()) {
      if (contentPanel.getChildren().size() == 1) {
        contentPanel.getChildren().add(content);
      }
      Chapter item = listView.getSelectionModel().getSelectedItem().getChapter();
      TaskFactory.create(() -> {
        NovelSpider spider = new NovelSpider(rule);
        return spider.content(item.getUrl());
      }).onSuccess(pages -> {
        if (pages != null) {
          content.setText(pages);
        }
      }).start();
    }
  }

  /**
   * 勾选选中
   */
  @FXML
  private void checkedAllSelected() {
    listView.getSelectionModel().getSelectedItems().forEach(item -> item.setSelected(true));
    listView.refresh();
  }

  /**
   * 取消勾选选中
   */
  @FXML
  private void unCheckedAllSelected() {
    listView.getSelectionModel().getSelectedItems().forEach(item -> item.setSelected(false));
    listView.refresh();
  }

  /**
   * 移除选中章节
   */
  @FXML
  private void removeSelected() {
    listView.getItems().removeAll(listView.getSelectionModel().getSelectedItems());
  }

  /**
   * 逆序章节
   */
  @FXML
  private void reverseToc() {
    Collections.reverse(listView.getItems());
  }

  /**
   * 浏览器打开
   */
  @FXML
  private void openBrowser() {
    String url = listView.getSelectionModel().getSelectedItem().getChapter().getUrl();
    if (UrlUtils.isHttpUrl(url)) {
      DesktopUtils.openBrowse(url);
    }
  }

  /**
   * 重命名章节序号
   */
  @FXML
  private void renameChapterNames() {
    String defaultTemplate = "第{{章节序号}}章 {{章节名}}";
    ModalBox.input(defaultTemplate, "请输入章节重命名模板", tempalte -> {
      int index = 1;
      ObservableList<ChapterWrapper> items = listView.getItems();
      for (int i = 0; i < listView.getItems().size(); i++) {
        ChapterWrapper chapter = items.get(i);
        if (chapter.isSelected()) {
          String name = chapter.getChapter().getName();
          name = StringUtils.remove(name, "[0-9]", "第.*?章");
          String newName = tempalte.replace("{{章节序号}}", String.valueOf(index++)).replace("{{章节名}}", name);
          chapter.getChapter().setName(newName);
        }
      }
      listView.refresh();
    }).title("重命名章节模板设置").show();
  }

  /**
   * 配置解析规则
   */
  @FXML
  public void configRule() {
    if (rule == null) {
      Toast.error(getRoot(), "请先解析目录~");
      return;
    }
    SidebarNavigateBundle bundle = new SidebarNavigateBundle().put(RuleEditorView.BUNDLE_RULE_KEY, rule);
    navigation.navigate(RuleEditorView.class, bundle);
  }

  /**
   * 显示小说详情
   */
  @FXML
  private void displayDetail() {
    if (novel == null) {
      Toast.error(getRoot(), "请先解析目录~");
      return;
    }
    ModalBox.none().body(new BookDetailNode(novel, true)).cancel("关闭").title("小说详情").show();
    floatButtons.animateList(false);
  }

  @FXML
  private void download() {
    if (novel == null) {
      Toast.error(getRoot(), "请先解析目录~");
      return;
    }
    // 获取选中的章节
    List<Chapter> selectedChapters = listView.getItems().stream()
      .filter(ChapterWrapper::isSelected)
      .map(ChapterWrapper::getChapter)
      .collect(Collectors.toList());
    if (selectedChapters.isEmpty()) {
      Toast.error("至少需要选择一个章节");
      return;
    }
    DownloadBundle downloadBundle = new DownloadBundle(novel, rule);
    // 只下载选中的章节
    downloadBundle.getNovel().setChapters(SerializationUtils.deepClone(selectedChapters));
    SidebarNavigateBundle bundle = new SidebarNavigateBundle();
    bundle.put(DownloadManagerView.BUNDLE_DOWNLOAD_KEY, downloadBundle);
    navigation.navigate(DownloadManagerView.class, bundle);
  }
}
