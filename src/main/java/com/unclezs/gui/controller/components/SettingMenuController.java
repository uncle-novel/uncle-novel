package com.unclezs.gui.controller.components;

import com.unclezs.gui.app.App;
import com.unclezs.gui.controller.SettingController;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.AlertUtil;
import com.unclezs.gui.utils.ContentUtil;
import com.unclezs.gui.utils.DesktopUtil;
import com.unclezs.utils.FileUtil;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * 设置页面
 *
 * @author unclezs.com
 * @date 2019.06.22 13:30
 */
@FXController("components/setting_popup")
public class SettingMenuController {
    public void github() {
        DesktopUtil.openBrowse("https://github.com/unclezs/NovelHarvester");
    }

    public void setting() {
        App.closeSetting();
        ContentUtil.show(SettingController.class);
    }

    public void reward() {
        BorderPane rewardBox = new BorderPane();
        Label title = new Label("欢迎打赏捐助，您的打赏捐助是对我最大的支持");
        title.getStyleClass().add("reward-title");
        rewardBox.setTop(title);
        ImageView rewardImage = new ImageView("/images/reward.jpg");
        rewardBox.setCenter(rewardImage);
        AlertUtil.alert("捐助", "以后再说", rewardBox);
    }

    public void about() {
        VBox box = new VBox();
        box.getChildren().add(new Label(String.format("当前版本：%s\n\n", "4.0.0")));
        box.getChildren().add(
            new Label("Uncle小说软件是@Uncle业余时间兴趣开发，软件完全免费且开源，可以下载全网小说也可以在线阅读\n\n"));
        AlertUtil.alert("关于", box);
    }

    /**
     * 免责弹窗
     */
    public void none() {
        AlertUtil.alert("免责声明",
            "软件仅供技术交流，请勿用于商业及非法用途，\n" + "如产生法律纠纷与本人无关，如有侵权请联系我删除.");
    }

    public void update() {
        DesktopUtil.openBrowse("https://unclezs.gitee.io/service/%E6%9B%B4%E6%96%B0%E8%AF%B4%E6%98%8E.html");
    }

    /**
     * 查看日志
     */
    public void showLog() {
        DesktopUtil.openDir(FileUtil.currentDirFile("logs"));
    }

    public void feedback() {
        DesktopUtil.openBrowse("https://support.qq.com/products/169599");
    }
}
