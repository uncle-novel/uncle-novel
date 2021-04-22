package com.unclezs.novel.app.framework.components;

import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.layout.StackPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 * JSON编辑器
 *
 * @author blog.unclezs.com
 * @date 2021/4/22 14:17
 */
public class JsonEditor extends StackPane {

  public static final String STYLESHEETS = "css/widgets/json-editor.css";
  private static final Pattern JSON_KEY = Pattern.compile("\"(?<key>.+?)\":|(?<others>.+)");
  private final CodeArea codeArea;

  public JsonEditor() {
    this("");
  }

  public JsonEditor(String text) {
    NodeHelper.addStyleSheet(this, ResourceUtils.externalForm(STYLESHEETS), "json-editor");
    codeArea = new CodeArea();
    codeArea.textProperty().addListener((obs, oldText, newText) -> codeArea.setStyleSpans(0, computeHighlighting(newText)));
    codeArea.replaceText(text);
    this.getChildren().setAll(codeArea);
  }

  private static StyleSpans<Collection<String>> computeHighlighting(String text) {
    Matcher matcher = JSON_KEY.matcher(text);
    int lastKwEnd = 0;
    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
    while (matcher.find()) {
      spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
      if (matcher.group("key") != null) {
        spansBuilder.add(Collections.singleton("key"), matcher.end() - matcher.start());
      } else if (matcher.group("others") != null) {
        spansBuilder.add(Collections.singleton("content"), matcher.end(2) - matcher.start(2));
      }
      lastKwEnd = matcher.end();
    }
    return spansBuilder.create();
  }

  /**
   * 是否心啊实行号
   *
   * @param show true 显示
   */
  public void setShowLineNumber(boolean show) {
    if (show) {
      codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    } else {
      codeArea.setParagraphGraphicFactory(null);
    }
  }

  public String getText() {
    return codeArea.getText();
  }

  public void setText(String text) {
    this.codeArea.replaceText(text);
  }
}
