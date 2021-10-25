package com.unclezs.novel.app.main.views.components.rule;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.script.ScriptContext;
import com.unclezs.novel.analyzer.script.ScriptUtils;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.util.DesktopUtils;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * 预处理脚本调试工具
 *
 * @author blog.unclezs.com
 * @date 2021/6/29 18:07
 */
public class ScriptDebugBox extends VBox {
  TextField result = new TextField();
  TextField source = new TextField();
  TextField url = new TextField();
  TextField params = new TextField();
  TextArea script = new TextArea();
  JFXCheckBox autoRequest = new JFXCheckBox("自动请求url并将结果保存为source");
  TextArea console = NodeHelper.addClass(new TextArea(), "rule-debug-console");

  public ScriptDebugBox() {
    console.setMaxHeight(60);
    console.setWrapText(true);
    console.setPromptText("此处为输出结果");
    setSpacing(10);
    script.setPromptText("在此输入预处理脚本");
    script.setMaxHeight(120);
    autoRequest.setSelected(false);
    NodeHelper.addClass(autoRequest, "disable-visual-focus");
    result.setPromptText("请输入result");
    source.setPromptText("请输入source");
    url.setPromptText("请输入url");
    params.setPromptText("请输入params的json");
    IconButton run = NodeHelper.addClass(new IconButton("运行", IconFont.RUN), "btn");
    IconButton copy = NodeHelper.addClass(new IconButton("复制转义后的脚本", IconFont.COPY), "btn");
    copy.setOnAction(e -> copyScript());
    run.setOnAction(e -> run());
    HBox actions = new HBox(copy, run);
    actions.setAlignment(Pos.CENTER_RIGHT);
    actions.setSpacing(10);
    getChildren().setAll(autoRequest, result, source, url, params, script, console, actions);
  }

  private void run() {
    if (StringUtils.isBlank(script.getText())) {
      Toast.error((StackPane) getParent(), "请先输入脚本");
      return;
    }
    TaskFactory.create(() -> {
        StringBuilder ret = new StringBuilder();
        ScriptContext.put(ScriptContext.VAR_RESULT, result.getText());
        ScriptContext.put(ScriptContext.VAR_URL, url.getText());
        ScriptContext.put(ScriptContext.VAR_SOURCE, source.getText());
        if (StringUtils.isNotBlank(params.getText())) {
          ScriptContext.put(ScriptContext.VAR_PARAMS, GsonUtils.parse(params.getText(), RequestParams.class));
        }
        // 自动获取URL源码
        if (autoRequest.isSelected()) {
          autoRequest(ret);
        }
        String executeRet = ScriptUtils.execute(script.getText(), ScriptContext.current());
        ret.append(executeRet);
        ScriptContext.remove();
        return ret.toString();
      }).onSuccess(console::setText)
      .onFailed(err -> {
        console.setText(ExceptionUtil.stacktraceToString(err));
        Toast.error((StackPane) getParent(), "执行失败");
      }).onFinally(ScriptContext::remove)
      .start();
  }

  private void autoRequest(StringBuilder ret) throws IOException {
    if (UrlUtils.isHttpUrl(url.getText())) {
      RequestParams requestParams = RequestParams.create(url.getText());
      requestParams.setDynamic(true);
      String content = Http.content(requestParams);
      ScriptContext.put(ScriptContext.VAR_SOURCE, content);
      Executor.runFx(() -> source.setText(content));
    } else {
      ret.append("警告：必须填写url才可自动请求");
    }
  }

  private void copyScript() {
    if (StringUtils.isBlank(script.getText())) {
      Toast.error((StackPane) getParent(), "请先输入脚本");
      return;
    }
    DesktopUtils.copy(StringUtils.removeQuote(GsonUtils.toJson(script.getText())));
    Toast.success((StackPane) getParent(), "复制成功");
  }
}
