package com.unclezs.novel.app.main.views.home;

import cn.hutool.core.io.FileUtil;
import com.jfoenix.controls.JFXPopup;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.core.View;
import com.unclezs.novel.app.framework.util.DesktopUtils;
import com.unclezs.novel.app.main.manager.ResourceManager;
import javafx.fxml.FXML;
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

  @FXML
  private JFXPopup popup;

  /**
   * github源码
   */
  @FXML
  public void github() {
    DesktopUtils.openBrowse("https://github.com/unclezs/NovelHarvester");
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
//    if (App.versionInfo != null) {
    box.getChildren().add(new Label(String.format("当前版本：%s\n\n", "5.0.0")));
//    }
    box.getChildren().add(new Label("Uncle小说软件是@Uncle业余时间兴趣开发，软件完全免费且开源，可以下载全网小说也可以在线阅读\n\n"));
    box.getChildren().add(new Label("开发者 QQ：1585503310\n微信：z1585503310"));
    HBox qgBox = new HBox();
    Label qg = new Label("问题反馈QQ群：");
    Hyperlink qgLink = new Hyperlink("774716671");
    qgBox.getChildren().addAll(qg, qgLink);
    qgLink.setOnAction(e -> DesktopUtils.openBrowse("https://shang.qq.com/wpa/qunwpa?idkey=e49493cef7cb08f05a60d84feed2338ddbde2930cae9deac75b7f3b7f4fac697"));
    box.getChildren().add(qgBox);
    ModalBox.none().title("关于").body(box).show();
  }


  /**
   * 免责声明
   */
  @FXML
  private void statement() {
    ModalBox.none().title("免责声明").message("软件仅供技术交流，请勿用于商业及非法用途，\n" + "如产生法律纠纷与本人无关，如有侵权请联系我删除.").show();
  }

  /**
   * 更新历史
   */
  @FXML
  private void update() {
    DesktopUtils.openBrowse("https://unclezs.gitee.io/service/%E6%9B%B4%E6%96%B0%E8%AF%B4%E6%98%8E.html");
  }

  /**
   * 查看日志
   */
  @FXML
  private void showLog() {
    DesktopUtils.openDir(FileUtil.file(ResourceManager.WORK_DIR, "logs"));
  }

  /**
   * 问题反馈
   */
  @FXML
  private void feedback() {
    DesktopUtils.openBrowse("https://support.qq.com/products/169599");
  }
}
