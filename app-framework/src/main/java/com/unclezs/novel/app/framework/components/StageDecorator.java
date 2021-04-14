package com.unclezs.novel.app.framework.components;

import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.i18n.LocalizedSupport;
import com.unclezs.novel.app.framework.util.PlatformUtils;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.framework.util.ViewUtils;
import java.util.ResourceBundle;
import javafx.beans.DefaultProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * 舞台装饰器
 *
 * @author blog.unclezs.com
 * @date 2021/02/28 15:12
 */
@DefaultProperty("content")
public class StageDecorator extends StackPane implements LocalizedSupport {

  /**
   * css类名
   */
  public static final String STAGE_DECORATOR = "stage-decorator";
  public static final String STAGE_DECORATOR_CONTAINER = "stage-decorator-container";
  public static final String STAGE_DECORATOR_HEADER = "stage-decorator-header";
  public static final String STAGE_DECORATOR_LOGO = "stage-decorator-logo";
  public static final String STAGE_DECORATOR_ACTIONS = "stage-decorator-actions";
  public static final String STAGE_DECORATOR_LOGO_ICON = "stage-decorator-logo-icon";
  public static final String STAGE_DECORATOR_LOGO_TITLE = "stage-decorator-logo-title";
  public static final String STAGE_DECORATOR_ACTION_SEPARATOR = "stage-decorator-action-separator";
  public static final String STAGE_DECORATOR_ACTIONS_EXIT = "stage-decorator-actions-exit";
  public static final int CLICK_COUNT_TO_MAX_WINDOW = 2;
  public static final String USER_AGENT_STYLESHEET = ResourceUtils.loadCss("css/components/stage-decorator.css");
  /**
   * 窗口 最大化 还原 全屏相关
   */
  private static final Border DEFAULT_BORDER = new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(4)));
  private static final Insets DEFAULT_PADDING = new Insets(10);
  /**
   * 被装饰的舞台
   */
  private Stage stage;
  /**
   * 容器
   */
  private VBox container;
  /**
   * 顶部logo HBox
   */
  private HBox logoBox;
  private ImageViewPlus logoImage;
  private Label titleLabel;
  /**
   * 顶部右侧操作按钮 HBox
   */
  private HBox actions;
  /**
   * 顶部容器
   */
  private HBox headerContainer;
  /**
   * 真正内容 View
   */
  private Node content;
  private double xOffset = 0;
  private double yOffset = 0;
  private double initX;
  private double initY;
  private double initWidth = -1;
  private double initHeight = -1;
  private double initStageX = -1;
  private double initStageY = -1;
  private boolean allowMove = false;
  private boolean isDragging = false;
  private Rectangle originalBox;
  private IconButton btnMax;
  private boolean maximized = false;
  /**
   * 支持fxml配置
   */
  @Getter
  @Setter
  private boolean theme;
  @Getter
  @Setter
  private boolean setting;
  @Getter
  @Setter
  private boolean max;
  @Getter
  @Setter
  private boolean min;
  @Getter
  private String logo;
  @Getter
  private String title;
  /**
   * action 按钮事件处理
   */
  private ActionHandler actionHandler;

  /**
   * 无参构造，fxml使用，直接创建用带参构造 创建之后要手动调用setStage方法进行初始化
   */
  public StageDecorator() {
    createContainer();
    initStageBehavior();
  }

  /**
   * 带参构造
   *
   * @param stage   舞台
   * @param theme   主题
   * @param setting 设置
   * @param max     最大化
   * @param min     最小化
   */
  public StageDecorator(Stage stage, ActionHandler actionHandler, boolean theme, boolean setting, boolean max, boolean min) {
    this();
    this.theme = theme;
    this.setting = setting;
    this.max = max;
    this.min = min;
    this.setStage(stage, actionHandler);
  }

  /**
   * 属性装配完成后执行初始化
   */
  private void initialize() {
    if (!stage.isShowing()) {
      stage.initStyle(StageStyle.TRANSPARENT);
    }
    // 创建actions按钮组
    createActions();
  }

  @Override
  public ResourceBundle getBundle() {
    return LocalizedSupport.getBundle("framework");
  }

  /**
   * 创建容器，不需要装配属性就可以调用
   */
  private void createContainer() {
    ViewUtils.addStyleSheetAndClass(this, USER_AGENT_STYLESHEET, STAGE_DECORATOR);
    ViewUtils.addClass(this.container = new VBox(), STAGE_DECORATOR_CONTAINER);
    this.headerContainer = ViewUtils.addClass(new HBox(), STAGE_DECORATOR_HEADER);
    this.logoBox = ViewUtils.addClass(new HBox(), STAGE_DECORATOR_LOGO);
    this.actions = ViewUtils.addClass(new HBox(), STAGE_DECORATOR_ACTIONS);
    HBox.setHgrow(this.actions, Priority.ALWAYS);
    this.headerContainer.getChildren().addAll(logoBox, actions);
    // 头部组件添加到容器
    this.container.getChildren().add(this.headerContainer);
    this.getChildren().setAll(container);
  }

  /**
   * 初始化舞台行为
   * <p>
   * 1.窗口拖动
   * <p>
   * 2.窗口拉动边框设置大小
   */
  private void initStageBehavior() {
    // 点击窗口和点击header时 更新鼠标位值
    this.container.addEventFilter(MouseEvent.MOUSE_PRESSED, this::updateInitMouseValues);
    headerContainer.addEventFilter(MouseEvent.MOUSE_PRESSED, this::updateInitMouseValues);
    // 在边框上显示拖动光标
    this.container.addEventFilter(MouseEvent.MOUSE_MOVED, this::showDragCursorOnTheBorder);
    // 处理舞台拖动事件
    this.container.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> isDragging = false);
    this.container.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::onStageDragged);
    this.setOnMouseClicked(e -> {
      if (e.getTarget().equals(container) && getCursor().equals(Cursor.DEFAULT)) {
        stage.toBack();
      }
    });
    // 点击header拖动监听
    headerContainer.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> allowMove = true);
    headerContainer.addEventFilter(MouseEvent.MOUSE_EXITED, e -> allowMove = isDragging);
  }

  /**
   * 创建actions按钮
   */
  private void createActions() {
    if (theme) {
      IconButton btnTheme = new IconButton(IconFont.THEME, localized("decorator.theme"));
      this.actions.getChildren().add(btnTheme);
      btnTheme.setOnMouseClicked(e -> actionHandler.onTheme(this, btnTheme));
    }
    if (setting) {
      IconButton btnSetting = new IconButton(null, IconFont.MENU, localized("decorator.setting"));
      this.actions.getChildren().addAll(btnSetting, ViewUtils.addClass(new Pane(), STAGE_DECORATOR_ACTION_SEPARATOR));
      btnSetting.setOnMouseClicked(e -> actionHandler.onSetting(this, btnSetting));
    }
    if (min) {
      IconButton btnMin = new IconButton(null, IconFont.MIN, localized("decorator.min"));
      btnMin.setOnAction(action -> stage.setIconified(true));
      this.actions.getChildren().add(btnMin);
    }
    if (max) {
      Icon resizeMaxIcon = new Icon(IconFont.MAX);
      Icon resizeMinIcon = new Icon(IconFont.MAX_RESTORE);
      btnMax = new IconButton();
      btnMax.setTip(localized("decorator.max"));
      btnMax.setIcon(resizeMaxIcon);
      btnMax.setOnAction(action -> maximize(resizeMinIcon, resizeMaxIcon));
      // 双击header 最大化
      headerContainer.setOnMouseClicked(mouseEvent -> {
        if (mouseEvent.getClickCount() >= CLICK_COUNT_TO_MAX_WINDOW) {
          btnMax.fire();
        }
      });
      this.actions.getChildren().add(btnMax);
    }
    IconButton btnClose = ViewUtils.addClass(new IconButton(null, IconFont.EXIT, localized("decorator.exit")), STAGE_DECORATOR_ACTIONS_EXIT);
    btnClose.setOnMouseClicked(e -> actionHandler.onClose(this, btnClose));
    this.actions.getChildren().add(btnClose);
  }

  /**
   * 获取舞台
   *
   * @return stage
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * 设置舞台（必须显示前调用）
   *
   * @param stage   舞台
   * @param handler action按钮点击回调
   */
  public void setStage(Stage stage, ActionHandler handler) {
    this.stage = stage;
    this.actionHandler = handler;
    initialize();
  }


  /**
   * 设置logo图标
   *
   * @param logoImagePath logo图标路径
   */
  public void setLogo(String logoImagePath) {
    if (this.logo == null) {
      this.logoImage = ViewUtils.addClass(new ImageViewPlus(), STAGE_DECORATOR_LOGO_ICON);
      this.logoBox.getChildren().add(0, this.logoImage);
    }
    this.logo = logoImagePath;
    this.logoImage.setImage(new Image(logoImagePath));
  }

  /**
   * 设置标题
   *
   * @param titleText 标题
   */
  public void setTitle(String titleText) {
    if (this.title == null) {
      this.titleLabel = ViewUtils.addClass(new Label(), STAGE_DECORATOR_LOGO_TITLE);
      this.logoBox.getChildren().add(this.titleLabel);
    }
    this.title = titleText;
    this.titleLabel.setText(titleText);
  }

  /**
   * 被装饰的 view
   *
   * @return view
   */
  public Node getContent() {
    return content;
  }

  /**
   * 被装饰的 view
   *
   * @param content view
   */
  public void setContent(Node content) {
    if (this.content != null) {
      this.container.getChildren().remove(content);
    }
    this.container.getChildren().add(content);
    this.content = content;
  }

  /**
   * 更新初始鼠标点击位置
   *
   * @param mouseEvent 鼠标点击事件
   */
  public void updateInitMouseValues(MouseEvent mouseEvent) {
    initStageX = stage.getX();
    initStageY = stage.getY();
    initWidth = stage.getWidth();
    initHeight = stage.getHeight();
    initX = mouseEvent.getScreenX();
    initY = mouseEvent.getScreenY();
    xOffset = mouseEvent.getSceneX();
    yOffset = mouseEvent.getSceneY();
  }

  /**
   * 鼠标在border的时候显示拖动
   *
   * @param mouseEvent 鼠标事件
   */
  public void showDragCursorOnTheBorder(MouseEvent mouseEvent) {
    // 最大化模式不支持调整大小
    if (stage.isFullScreen() || maximized) {
      this.setCursor(Cursor.DEFAULT);
      return;
    }
    if (!stage.isResizable()) {
      return;
    }
    double x = mouseEvent.getX();
    double y = mouseEvent.getY();
    if (this.container.getBorder() != null && !this.container.getBorder().getStrokes().isEmpty()) {
      double borderWidth = this.container.snappedLeftInset();
      if (isRightEdge(x)) {
        if (y < borderWidth) {
          this.setCursor(Cursor.NE_RESIZE);
        } else if (y > this.getHeight() - borderWidth) {
          this.setCursor(Cursor.SE_RESIZE);
        } else {
          this.setCursor(Cursor.E_RESIZE);
        }
      } else if (isLeftEdge(x)) {
        if (y < borderWidth) {
          this.setCursor(Cursor.NW_RESIZE);
        } else if (y > this.getHeight() - borderWidth) {
          this.setCursor(Cursor.SW_RESIZE);
        } else {
          this.setCursor(Cursor.W_RESIZE);
        }
      } else if (isTopEdge(y)) {
        this.setCursor(Cursor.N_RESIZE);
      } else if (isBottomEdge(y)) {
        this.setCursor(Cursor.S_RESIZE);
      } else {
        this.setCursor(Cursor.DEFAULT);
      }
    }
  }

  /**
   * 处理拖动
   *
   * @param mouseEvent 鼠标事件
   */
  private void onStageDragged(MouseEvent mouseEvent) {
    isDragging = true;
    // 释放鼠标 或者移动到边界
    if (!mouseEvent.isPrimaryButtonDown() || (xOffset == -1 && yOffset == -1)) {
      return;
    }
    // 长按会产生拖动事件
    if (stage.isFullScreen() || mouseEvent.isStillSincePress() || maximized) {
      return;
    }
    double newX = mouseEvent.getScreenX();
    double newY = mouseEvent.getScreenY();
    double deltaX = newX - initX;
    double deltaY = newY - initY;
    Cursor cursor = this.getCursor();
    if (Cursor.E_RESIZE.equals(cursor)) {
      setStageWidth(initWidth + deltaX);
      mouseEvent.consume();
    } else if (Cursor.NE_RESIZE.equals(cursor)) {
      if (setStageHeight(initHeight - deltaY)) {
        stage.setY(initStageY + deltaY);
      }
      setStageWidth(initWidth + deltaX);
      mouseEvent.consume();
    } else if (Cursor.SE_RESIZE.equals(cursor)) {
      setStageWidth(initWidth + deltaX);
      setStageHeight(initHeight + deltaY);
      mouseEvent.consume();
    } else if (Cursor.S_RESIZE.equals(cursor)) {
      setStageHeight(initHeight + deltaY);
      mouseEvent.consume();
    } else if (Cursor.W_RESIZE.equals(cursor)) {
      if (setStageWidth(initWidth - deltaX)) {
        stage.setX(initStageX + deltaX);
      }
      mouseEvent.consume();
    } else if (Cursor.SW_RESIZE.equals(cursor)) {
      if (setStageWidth(initWidth - deltaX)) {
        stage.setX(initStageX + deltaX);
      }
      setStageHeight(initHeight + deltaY);
      mouseEvent.consume();
    } else if (Cursor.NW_RESIZE.equals(cursor)) {
      if (setStageWidth(initWidth - deltaX)) {
        stage.setX(initStageX + deltaX);
      }
      if (setStageHeight(initHeight - deltaY)) {
        stage.setY(initStageY + deltaY);
      }
      mouseEvent.consume();
    } else if (Cursor.N_RESIZE.equals(cursor)) {
      if (setStageHeight(initHeight - deltaY)) {
        stage.setY(initStageY + deltaY);
      }
      mouseEvent.consume();
    } else if (allowMove) {
      stage.setX(mouseEvent.getScreenX() - xOffset);
      stage.setY(mouseEvent.getScreenY() - yOffset);
      mouseEvent.consume();
    }
  }

  /**
   * 判断是否为右边边界
   *
   * @param x 鼠标位置
   * @return true 是
   */
  private boolean isRightEdge(double x) {
    final double width = this.container.getWidth();
    return x < width && x > width - this.container.snappedLeftInset();
  }

  /**
   * 判断是否为顶部边界
   *
   * @param y 鼠标位置
   * @return true 是
   */
  private boolean isTopEdge(double y) {
    return y >= 0 && y < this.container.snappedLeftInset();
  }

  /**
   * 判断是否为底部边界
   *
   * @param y 鼠标位置
   * @return true 是
   */
  private boolean isBottomEdge(double y) {
    final double height = this.container.getHeight();
    return y < height && y > height - this.container.snappedLeftInset();
  }

  /**
   * 判断是否为左边界
   *
   * @param x 鼠标位置
   * @return true 是
   */
  private boolean isLeftEdge(double x) {
    return x >= 0 && x < this.container.snappedLeftInset();
  }

  /**
   * 设置舞台宽度
   *
   * @param width 舞台宽度
   * @return 舞台宽度
   */
  private boolean setStageWidth(double width) {
    if (width >= stage.getMinWidth() + this.container.snappedRightInset() + this.container
      .snappedLeftInset() && width >= headerContainer.getMinWidth()) {
      stage.setWidth(width);
      return true;
    } else if (width >= stage.getMinWidth() && width <= headerContainer.getMinWidth()) {
      width = headerContainer.getMinWidth();
      stage.setWidth(width);
    }
    return false;
  }

  /**
   * 设置舞台高度
   *
   * @param height 舞台高度
   * @return 舞台高度
   */
  private boolean setStageHeight(double height) {
    if (height >= (stage.getMinHeight() + this.container.snappedRightInset() + this.container
      .snappedLeftInset()) && height >= headerContainer.getHeight()) {
      stage.setHeight(height);
      return true;
    } else if (height >= stage.getMinHeight() && height <= headerContainer.getHeight()) {
      height = headerContainer.getHeight();
      stage.setHeight(height);
    }
    return false;
  }

  /**
   * 最大化与最小化 兼容Mac
   *
   * @param restoreIcon 复原图标
   * @param maxIcon     最大化图标
   */
  private void maximize(Icon restoreIcon, Icon maxIcon) {
    maximized = !maximized;
    // 切换按钮图标及提示
    if (maximized) {
      this.setPadding(Insets.EMPTY);
      this.container.setBorder(Border.EMPTY);
      btnMax.setGraphic(restoreIcon);
      btnMax.setTip(localized("decorator.restore"));
    } else {
      this.setPadding(DEFAULT_PADDING);
      this.container.setBorder(DEFAULT_BORDER);
      btnMax.setGraphic(maxIcon);
      btnMax.setTip(localized("decorator.max"));
    }
    // 自定义创建最大化
    if (PlatformUtils.isMac()) {
      if (maximized) {
        // 获取屏幕大小，记录原始窗口
        originalBox = new Rectangle(stage.getX(), stage.getY(), stage.getWidth(),
          stage.getHeight());
        BoundingBox maximizedBox = new BoundingBox(0, 0,
          Screen.getPrimary().getVisualBounds().getWidth(),
          Screen.getPrimary().getVisualBounds().getHeight());
        stage.setX(maximizedBox.getMinX());
        stage.setY(maximizedBox.getMinY());
        stage.setWidth(maximizedBox.getWidth());
        stage.setHeight(maximizedBox.getHeight());
      } else {
        // 恢复原始窗口
        stage.setX(originalBox.getX());
        stage.setY(originalBox.getY());
        stage.setWidth(originalBox.getWidth());
        stage.setHeight(originalBox.getHeight());
        originalBox = null;
      }
    } else {
      stage.setMaximized(maximized);
    }
  }

  /**
   * 处理action按钮事件
   */
  public interface ActionHandler {

    /**
     * 主题按钮点击时触发
     *
     * @param view        舞台装饰View
     * @param themeButton 主题按钮
     */
    default void onTheme(StageDecorator view, IconButton themeButton) {

    }

    /**
     * 设置按钮点击时触发
     *
     * @param view          舞台装饰View
     * @param settingButton 设置按钮
     */
    default void onSetting(StageDecorator view, IconButton settingButton) {
    }

    /**
     * 窗口关闭时触发 重写之后需要自己手动调用 close
     *
     * @param view        舞台装饰View
     * @param closeButton 关闭按钮
     */
    default void onClose(StageDecorator view, IconButton closeButton) {
      view.stage.fireEvent(new WindowEvent(view.stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
  }
}
