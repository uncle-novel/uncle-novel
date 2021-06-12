package com.unclezs.novel.app.main.views.home;

import cn.hutool.core.io.FileUtil;
import com.jfoenix.controls.JFXPopup;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.core.View;
import com.unclezs.novel.app.framework.util.DesktopUtils;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.manager.SettingManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author blog.unclezs.com
 * @date 2021/03/07 9:45
 */
@FxView(fxml = "/layout/home/header-menu.fxml")
public class HeaderMenuView extends View<JFXPopup> {

  public static final String GITHUB = "https://github.com/unclezs/NovelHarvester";
  /**
   * 免责声明
   */
  private static final String DISCLAIMERS = "https://app.unclezs.com/common/disclaimers.html";
  public static final String LOGS_DIR = "logs";
  public static final String FEEDBACK_URL = "https://support.qq.com/products/169599";
  public static final String CHANGE_LOG_URL = "https://unclezs.gitee.io/service/%E6%9B%B4%E6%96%B0%E8%AF%B4%E6%98%8E.html";
  @FXML
  private JFXPopup popup;

  /**
   * github源码
   */
  @FXML
  public void github() {
    DesktopUtils.openBrowse(GITHUB);
  }

  /**
   * 打赏
   */
  @FXML
  private void reward() {
    BorderPane rewardBox = new BorderPane();
    Label title = new Label("欢迎打赏捐助，您的打赏捐助是对我最大的支持");
    title.getStyleClass().add("reward-title");
    rewardBox.setTop(title);
    ImageView rewardImage = new ImageView("/assets/images/reward.jpg");
    rewardBox.setCenter(rewardImage);
    ModalBox.none().title("赞赏开发者").body(rewardBox).show();
    popup.hide();
  }

  /**
   * 关于
   */
  @FXML
  public void about() {
    VBox box = new VBox();
    box.setSpacing(10);
    box.getChildren().add(new Label(String.format("当前版本：%s", SettingManager.manager().getVersion())));
    box.getChildren().add(new Label("Uncle小说软件是@Unclezs业余时间兴趣开发，软件完全免费且开源，可以下载全网小说也可以在线阅读"));
    box.getChildren().add(new Label("邮箱：unclezs@qq.com"));
    Label qqGroupLabel = new Label("问题反馈QQ群：");
    Hyperlink qqGroupLink = new Hyperlink("774716671");
    qqGroupLink.setOnAction(e -> DesktopUtils.openBrowse("https://shang.qq.com/wpa/qunwpa?idkey=e49493cef7cb08f05a60d84feed2338ddbde2930cae9deac75b7f3b7f4fac697"));
    HBox qgBox = new HBox(qqGroupLabel, qqGroupLink);
    qgBox.setAlignment(Pos.CENTER_LEFT);
    box.getChildren().add(qgBox);
    ModalBox.none().title("关于").body(box).show();
  }


  /**
   * 免责声明
   */
  @FXML
  private void statement() {
    DesktopUtils.openBrowse(DISCLAIMERS);
  }

  /**
   * 更新历史
   */
  @FXML
  private void update() {
    DesktopUtils.openBrowse(CHANGE_LOG_URL);
  }

  /**
   * 查看日志
   */
  @FXML
  private void showLog() {
    DesktopUtils.openDir(FileUtil.file(ResourceManager.WORK_DIR, LOGS_DIR));
  }

  /**
   * 问题反馈
   */
  @FXML
  private void feedback() {
    DesktopUtils.openBrowse(FEEDBACK_URL);
  }
}
