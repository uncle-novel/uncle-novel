package com.unclezs.novel.app.main.ui.home.views;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.analyzer.spider.TocSpider;
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
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.main.model.ChapterProperty;
import com.unclezs.novel.app.main.ui.home.views.widgets.ChapterListCell;
import java.util.Collections;
import java.util.Objects;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
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

  private final TextArea content = new TextArea();
  @FXML
  private HBox contentPanel;
  @FXML
  private ListView<ChapterProperty> listView;
  @FXML
  private InputBox input;
  private AnalyzerRule rule;
  private Novel novel;

  @Override
  public void onCreate() {
    input.getInput().setText("https://m.zhaishuyuan.com/read/33959");
    listView.setCellFactory(param -> new ChapterListCell());
    content.setWrapText(true);
    content.prefWidthProperty().bind(contentPanel.widthProperty().multiply(0.5));
    listView.prefWidthProperty().bind(contentPanel.widthProperty().multiply(0.5));
    contentPanel.getChildren().remove(content);
    listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    EventUtils.setOnMouseDoubleClick(listView, event -> {
      analysisContent();
    });
    // 空数据不弹出上下文菜单
    listView.setOnContextMenuRequested(e -> {
      if (listView.getSelectionModel().isEmpty()) {
        listView.getContextMenu().hide();
      }
    });
  }

  public void onAnalysis(ActionClickedEvent event) {
    String tocUrl = event.getInput();
    if (!UrlUtils.isHttpUrl(tocUrl)) {
      Toast.error(getRoot(), "请输入正确的目录地址");
      return;
    }
    listView.getItems().clear();
    contentPanel.getChildren().remove(content);
    AnalyzerRule tocRule = RuleHelper.getRule(tocUrl);
    this.rule = Objects.requireNonNullElseGet(tocRule, AnalyzerRule::new);
    rule.setSite(UrlUtils.getSite(tocUrl));
    TocSpider tocSpider = new TocSpider(rule);
    tocSpider.setOnNewItemAddHandler(chapter -> Executor.runFx(() -> {
      listView.getItems().add(new ChapterProperty(chapter));
      listView.scrollTo(listView.getItems().size() - 1);
    }));
    TaskFactory.create(() -> {
      tocSpider.toc(tocUrl);
      this.novel = tocSpider.getNovel();
      tocSpider.loadAll();
      return null;
    }).onSuccess(value -> {
      if (StringUtils.isBlank(this.rule.getName())) {
        this.rule.setName(novel.getTitle());
      }
    }).start();
  }

  public void displayContent() {
    analysisContent();
  }

  @FXML
  private void analysisContent() {
    if (!listView.getSelectionModel().isEmpty()) {
      if (contentPanel.getChildren().size() == 1) {
        contentPanel.getChildren().add(content);
      }
      content.clear();
      Chapter item = listView.getSelectionModel().getSelectedItem().getChapter();
      TaskFactory.create(() -> {
        NovelSpider spider = new NovelSpider(rule);
        spider.content(RequestParams.create(item.getUrl()), page -> {
          Executor.runFx(() -> content.appendText(page));
        });
        return null;
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

  @FXML
  private void reverseToc() {
    Collections.reverse(listView.getItems());
  }

  /**
   * 重命名章节序号
   */
  @FXML
  private void renameChapterNames() {
    String defaultTemplate = "第{{章节序号}}章 {{章节名}}";
    ModalBox.input(defaultTemplate, "请输入章节重命名模板", tempalte -> {
      int index = 1;
      ObservableList<ChapterProperty> items = listView.getItems();
      for (int i = 0; i < listView.getItems().size(); i++) {
        ChapterProperty chapter = items.get(i);
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

  public void configRule() {
    if (rule == null) {
      Toast.error(getRoot(), "请先解析目录~");
      return;
    }
    SidebarNavigateBundle bundle = new SidebarNavigateBundle().put(RuleEditorView.BUNDLE_RULE_KEY, rule);
    navigation.navigate(RuleEditorView.class, bundle);
  }
}
