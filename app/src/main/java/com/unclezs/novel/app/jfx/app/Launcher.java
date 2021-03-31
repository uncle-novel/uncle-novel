package com.unclezs.novel.app.jfx.app;

import com.unclezs.jfx.launcher.Manifest;
import com.unclezs.novel.app.jfx.app.ui.app.App;
import com.unclezs.novel.app.jfx.framework.ui.appication.SsaApplication;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * _ooOoo_
 * o8888888o
 * 88" . "88
 * (| -_- |)
 *  O\ = /O
 * ___/`---'\____
 * .   ' \\| |// `.
 * / \\||| : |||// \
 * / _||||| -:- |||||- \
 * | | \\\ - /// | |
 * | \_| ''\---/'' | |
 * \ .-\__ `-` ___/-. /
 * ___`. .' /--.--\ `. . __
 * ."" ' `.___\_|_/___.' '"".
 * | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * \ \ `-. \_ __\ /__ _/ .-` / /
 * ======`-.____`-.___\_____/___.-`____.-'======
 * `=---='
 * .............................................
 * 佛曰：bug泛滥，我已瘫痪！
 * </pre>
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
@Slf4j
public class Launcher {

  public static void main(String[] args) {
    Platform.startup(() -> {
      SsaApplication app = new App();
      try {
        app.init();
        Stage stage = new Stage();
        Manifest value = new Manifest();
        value.setNewVersion(true);
        stage.setUserData(value);
        app.start(stage);
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(-1);
      }
    });
  }
}
