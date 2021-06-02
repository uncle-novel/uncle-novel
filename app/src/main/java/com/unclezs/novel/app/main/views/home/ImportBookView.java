package com.unclezs.novel.app.main.views.home;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.core.loader.TxtLoader;
import com.unclezs.novel.app.main.db.beans.Book;
import com.unclezs.novel.app.main.db.beans.TxtTocRule;
import com.unclezs.novel.app.main.db.dao.TxtTocRuleDao;
import com.unclezs.novel.app.main.model.BookCache;
import com.unclezs.novel.app.main.util.BookHelper;
import com.unclezs.novel.app.main.util.EncodingDetect;
import com.unclezs.novel.app.main.views.components.cell.TocListCell;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 导入本地书籍
 *
 * @author blog.unclezs.com
 * @date 2021/5/9 22:34
 */
@Slf4j
@EqualsAndHashCode(callSuper = false)
@FxView(fxml = "/layout/home/import-book.fxml")
public class ImportBookView extends SidebarView<StackPane> {

  public static final String BUNDLE_FILE_KEY = "bundle-file-key";
  private final TxtTocRuleDao txtTocRuleDao = new TxtTocRuleDao();
  /**
   * 编码选择
   */
  private final List<String> charsets = List.of("UTF-8", "GBK", "GB2312");
  @FXML
  public ComboBox<String> encoding;
  @FXML
  private ListView<Chapter> toc;
  @FXML
  private TextArea content;
  @FXML
  private ComboBox<String> ruleSelector;
  @FXML
  private HBox resultBox;
  @FXML
  private TextField ruleInput;
  @FXML
  private TextField fileInput;
  private Book book;
  private List<TxtTocRule> rules;
  private TxtLoader loader;

  @Override
  public void onCreated() {
    // 布局绑定
    toc.prefWidthProperty().bind(resultBox.widthProperty().multiply(0.5));
    content.prefWidthProperty().bind(resultBox.widthProperty().multiply(0.5));
    // 选中解析
    toc.getSelectionModel().selectedIndexProperty().addListener(e -> {
      if (!toc.getSelectionModel().isEmpty()) {
        int index = toc.getSelectionModel().getSelectedIndex();
        content.setText(loader.loadContent(index));
      }
    });
    encoding.getItems().addAll(charsets);
    toc.setCellFactory(param -> new TocListCell());
    // 文件选择器
    ruleSelector.valueProperty().addListener(e -> {
      if (!ruleSelector.getSelectionModel().isEmpty()) {
        this.ruleInput.setText(rules.get(ruleSelector.getSelectionModel().getSelectedIndex()).getRule());
      }
    });
    // 加载已有规则
    loadRules();
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    File bookFile = bundle.get(BUNDLE_FILE_KEY);
    if (bookFile != null) {
      setFile(bookFile.getAbsolutePath());
      // 自动选择适合哪种规则
      int ruleIndex = TxtLoader.checkChapterType(new File(this.fileInput.getText()), rules);
      if (ruleIndex >= 0) {
        ruleSelector.getSelectionModel().select(ruleIndex);
        analysis();
      }
    }
  }

  @Override
  public void onHidden() {
    this.book = null;
    this.toc.getItems().clear();
    this.content.setText(null);
  }

  /**
   * 保存到书架
   */
  @FXML
  public void addToBookShelf() {
    if (book != null) {
      AppContext.getView(FictionBookshelfView.class).addBook(book);
      // 缓存章节
      BookHelper.cache(new BookCache(null, loader.toc()), FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId()));
      Toast.success("加入成功");
      ModalBox modalBox = ModalBox.confirm(notBack -> {
        if (Boolean.FALSE.equals(notBack)) {
          backToBookShelf();
        }
      }).message("是否继续导入");
      modalBox.show();
    }
  }

  /**
   * 返回书架
   */
  @FXML
  private void backToBookShelf() {
    navigation.navigate(FictionBookshelfView.class);
  }


  /**
   * 选择文件
   */
  @FXML
  private void selectTxtFile() {
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("文本文件", "*.txt");
    // 初始目录
    if (fileInput.getText() != null) {
      fileChooser.setInitialDirectory(new File(fileInput.getText()).getParentFile());
    }
    fileChooser.getExtensionFilters().addAll(filter);
    File bookFile = fileChooser.showOpenDialog(App.stage());
    if (bookFile == null) {
      return;
    }
    setFile(bookFile.getAbsolutePath());
  }

  /**
   * 设置小说文件，自动识别编码
   *
   * @param filePath 文件路径
   */
  private void setFile(String filePath) {
    this.encoding.setValue(EncodingDetect.getEncode(filePath));
    this.fileInput.setText(filePath);
  }

  /**
   * 解析文件
   */
  @FXML
  @SneakyThrows
  private void analysis() {
    File file = FileUtil.file(fileInput.getText());
    if (!FileUtil.exist(file)) {
      return;
    }
    TaskFactory.create(() -> {
      if (loader == null) {
        loader = new TxtLoader();
      }
      book = new Book(file.getAbsolutePath(), getEncoding());
      book.setId(IdUtil.fastSimpleUUID());
      book.setName(FileUtil.mainName(file));
      book.setLocal(true);
      book.setGroup(FictionBookshelfView.GROUP_LOCAL);
      book.setTxtTocRule(ruleInput.getText());
      loader.setBook(book);
      return loader.toc();
    }).onSuccess(chapters -> {
      toc.getItems().setAll(chapters);
      // 解析第一章
      if (!chapters.isEmpty()) {
        toc.getSelectionModel().select(0);
      }
    }).onFailed(e -> Toast.error("解析失败"))
      .start();
  }

  /**
   * 保存规则
   */
  @FXML
  private void saveRule() {
    ModalBox.input(name -> {
      if (StringUtils.isNotBlank(name)) {
        TxtTocRule txtTocRule = new TxtTocRule();
        txtTocRule.setRule(ruleInput.getText());
        txtTocRule.setName(name);
        if (!rules.isEmpty()) {
          txtTocRule.setOrder(rules.get(rules.size() - 1).getOrder() + 5);
        }
        try {
          txtTocRuleDao.getDao().createOrUpdate(txtTocRule);
        } catch (SQLException e) {
          log.error("保存规则失败：{}", txtTocRule, e);
          Toast.error("保存失败");
          return;
        }
        ruleSelector.getItems().add(txtTocRule.getName());
        rules.add(txtTocRule);
        // 选中最后一个规则
        if (!ruleSelector.getItems().isEmpty()) {
          ruleSelector.getSelectionModel().select(ruleSelector.getItems().size() - 1);
        }
        Toast.success("保存成功");
      }
    }).title("请输入规则名称").show();
  }

  /**
   * 删除规则
   */
  @FXML
  private void deleteRule() {
    if (ruleSelector.getSelectionModel().isEmpty()) {
      return;
    }
    int index = ruleSelector.getSelectionModel().getSelectedIndex();
    TxtTocRule txtTocRule = rules.get(index);
    txtTocRuleDao.delete(txtTocRule);
    rules.remove(txtTocRule);
    ruleSelector.getItems().remove(index);
    if (!ruleSelector.getItems().isEmpty()) {
      ruleSelector.getSelectionModel().select(0);
    }
    Toast.success("删除成功");
  }

  /**
   * 导入默认规则
   */
  @FXML
  private void importDefaultRules() {
    txtTocRuleDao.importDefault();
    loadRules();
    Toast.success("导入成功");
  }

  /**
   * 重命名章节序号
   */
  @FXML
  private void renameChapterNames() {
    String defaultTemplate = "第{{章节序号}}章 {{章节名}}";
    ModalBox.input(defaultTemplate, "请输入章节重命名模板", template -> {
      int index = 1;
      ObservableList<Chapter> items = toc.getItems();
      for (int i = 0; i < toc.getItems().size(); i++) {
        Chapter chapter = items.get(i);
        String name = chapter.getName();
        name = StringUtils.remove(name, "[0-9]", "第.*?章");
        String newName = template.replace("{{章节序号}}", String.valueOf(index++)).replace("{{章节名}}", name);
        chapter.setName(newName);
      }
      toc.refresh();
    }).title("重命名章节模板设置").show();
  }

  /**
   * 导入数据库中的规则， 一个都不存在则导入默认规则
   */
  private void loadRules() {
    rules = txtTocRuleDao.selectAllByOrder();
    ruleSelector.getItems().setAll(rules.stream().map(TxtTocRule::getName).collect(Collectors.toList()));
    if (!ruleSelector.getItems().isEmpty()) {
      ruleSelector.getSelectionModel().select(0);
    }
  }

  /**
   * 获取编码
   */
  private String getEncoding() {
    if (StringUtils.isBlank(encoding.getValue())) {
      encoding.setValue(EncodingDetect.getEncode(fileInput.getText()));
    }
    return encoding.getValue();
  }
}
