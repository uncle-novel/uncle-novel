package com.uncles.novel.app.jfx.framework.ui.components.decorator;

import com.sun.javafx.PlatformUtil;
import com.uncles.novel.app.jfx.framework.i18n.I18nSupport;
import com.uncles.novel.app.jfx.framework.ui.components.button.IconButton;
import com.uncles.novel.app.jfx.framework.ui.components.icon.Icon;
import com.uncles.novel.app.jfx.framework.ui.components.image.UnImageView;
import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import com.uncles.novel.app.jfx.framework.util.ViewUtils;
import javafx.beans.DefaultProperty;
import javafx.geometry.BoundingBox;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;

/**
 * 舞台装饰器
 *
 * @author blog.unclezs.com
 * @date 2021/02/28 15:12
 */
@DefaultProperty("content")
public class StageDecorator extends VBox implements I18nSupport {
    /**
     * css类名
     */
    public static final String STAGE_DECORATOR = "stage-decorator";
    public static final String STAGE_DECORATOR_HEADER = "stage-decorator-header";
    public static final String STAGE_DECORATOR_LOGO = "stage-decorator-logo";
    public static final String STAGE_DECORATOR_ACTIONS = "stage-decorator-actions";
    public static final String STAGE_DECORATOR_LOGO_ICON = "stage-decorator-logo-icon";
    public static final String STAGE_DECORATOR_LOGO_TITLE = "stage-decorator-logo-title";
    public static final String STAGE_DECORATOR_ACTION_SEPARATOR = "stage-decorator-action-separator";
    public static final String STAGE_DECORATOR_ACTIONS_EXIT = "stage-decorator-actions-exit";
    public static final int CLICK_COUNT_TO_MAX_WINDOW = 2;
    private Stage stage;
    /**
     * 顶部logo HBox
     */
    public HBox logoBox;
    @Getter
    public UnImageView logoImage;
    @Getter
    public Label titleLabel;
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
    private boolean maximized = false;
    /**
     * 默认边框
     */
    public static final Border DEFAULT_BORDER = new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(10)));
    private ActionHandler actionHandler;

    private Rectangle originalBox;
    private IconButton btnMax;

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
    public String logo;
    @Getter
    public String title;


    public static final String USER_AGENT_STYLESHEET = ResourceUtils.loadCss("/css/components/stage-decorator.css");

    /**
     * 无参构造，fxml使用，直接创建用带参构造
     * 创建之后要手动调用setStage方法进行初始化
     */
    public StageDecorator() {
        createHeader();
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
        // 双向绑定舞台标题
//        if (titleLabel.getText() != null && stage.getTitle() == null) {
//            stage.setTitle(getTitle());
//        }
//        titleLabel.textProperty().bindBidirectional(stage.titleProperty());
        // 创建actions按钮组
        createActions();
    }

    /**
     * 创建头部容器，不需要装配属性就可以调用
     */
    private void createHeader() {
        ViewUtils.addStyleSheetAndClass(this, USER_AGENT_STYLESHEET, STAGE_DECORATOR);
        this.headerContainer = ViewUtils.addClass(new HBox(), STAGE_DECORATOR_HEADER);
        this.logoBox = ViewUtils.addClass(new HBox(), STAGE_DECORATOR_LOGO);
        this.actions = ViewUtils.addClass(new HBox(), STAGE_DECORATOR_ACTIONS);
        HBox.setHgrow(this.actions, Priority.ALWAYS);
        this.headerContainer.getChildren().addAll(logoBox, actions);
        // 头部组件添加到容器
        this.getChildren().add(this.headerContainer);
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
        this.setOnMousePressed(this::updateInitMouseValues);
        headerContainer.setOnMousePressed(this::updateInitMouseValues);
        // 在边框上显示拖动光标
        this.addEventFilter(MouseEvent.MOUSE_MOVED, this::showDragCursorOnTheBorder);
        // 处理舞台拖动事件
        this.setOnMouseReleased(e -> isDragging = false);
        this.setOnMouseDragged(this::onStageDragged);
        // 点击header拖动监听
        headerContainer.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> allowMove = true);
        headerContainer.addEventFilter(MouseEvent.MOUSE_EXITED, e -> allowMove = isDragging);
    }

    /**
     * 创建actions按钮
     */
    private void createActions() {
        if (theme) {
            IconButton btnTheme = new IconButton();
            btnTheme.setSvg("_theme");
            btnTheme.setTip(localized("换肤"));
            this.actions.getChildren().add(btnTheme);
            btnTheme.setOnMouseClicked(e -> actionHandler.onTheme(this));
        }
        if (setting) {
            IconButton btnSetting = new IconButton(null, "\uf0c9", localized("设置"));
            this.actions.getChildren().addAll(btnSetting, ViewUtils.addClass(new Pane(), STAGE_DECORATOR_ACTION_SEPARATOR));
            btnSetting.setOnMouseClicked(e -> actionHandler.onSetting(this));
        }
        if (min) {
            IconButton btnMin = new IconButton(null, "\uf068", "最小化");
            btnMin.setOnAction((action) -> stage.setIconified(true));
            this.actions.getChildren().add(btnMin);
        }
        if (max) {
            Icon resizeMaxIcon = new Icon('\uf2d0');
            Icon resizeMinIcon = new Icon('\uf2d2');
            btnMax = new IconButton();
            btnMax.setTip("最大化");
            btnMax.setIcon(resizeMaxIcon);
            btnMax.setOnAction((action) -> maximize(resizeMinIcon, resizeMaxIcon));
            headerContainer.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == CLICK_COUNT_TO_MAX_WINDOW) {
                    updateInitMouseValues(mouseEvent);
                    btnMax.fire();
                }
            });
            this.actions.getChildren().add(btnMax);
        }
        IconButton btnClose = ViewUtils.addClass(new IconButton(null, "\uf011", "退出"), STAGE_DECORATOR_ACTIONS_EXIT);
        btnClose.setOnMouseClicked(e -> actionHandler.onClose(this));
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
            this.logoImage = ViewUtils.addClass(new UnImageView(), STAGE_DECORATOR_LOGO_ICON);
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
            getChildren().remove(content);
        }
        getChildren().add(content);
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
        if (stage.isMaximized() || stage.isFullScreen() || maximized) {
            this.setCursor(Cursor.DEFAULT);
            return; // maximized mode does not support resize
        }
        if (!stage.isResizable()) {
            return;
        }
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        if (getBorder() != null && getBorder().getStrokes().size() > 0) {
            double borderWidth = snappedLeftInset();
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
        final double width = this.getWidth();
        return x < width && x > width - snappedLeftInset();
    }

    /**
     * 判断是否为顶部边界
     *
     * @param y 鼠标位置
     * @return true 是
     */
    private boolean isTopEdge(double y) {
        return y >= 0 && y < snappedLeftInset();
    }

    /**
     * 判断是否为底部边界
     *
     * @param y 鼠标位置
     * @return true 是
     */
    private boolean isBottomEdge(double y) {
        final double height = this.getHeight();
        return y < height && y > height - snappedLeftInset();
    }

    /**
     * 判断是否为左边界
     *
     * @param x 鼠标位置
     * @return true 是
     */
    private boolean isLeftEdge(double x) {
        return x >= 0 && x < snappedLeftInset();
    }

    /**
     * 设置舞台宽度
     *
     * @param width 舞台宽度
     * @return 舞台宽度
     */
    private boolean setStageWidth(double width) {
        System.out.println(width + "  " + stage.getMinWidth() + " " + headerContainer.getMinWidth());
        if (width >= stage.getMinWidth() + this.snappedRightInset() + this.snappedLeftInset() && width >= headerContainer.getMinWidth()) {
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
        if (height >= (stage.getMinHeight() + this.snappedRightInset() + this.snappedLeftInset()) && height >= headerContainer.getHeight()) {
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
        // 自定义创建最大化
        if (PlatformUtil.isMac()) {
            if (maximized) {
                // 获取屏幕大小，记录原始窗口
                originalBox = new Rectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
                BoundingBox maximizedBox = new BoundingBox(0, 0, Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
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
        // 切换按钮图标及提示
        if (maximized) {
            setBorder(Border.EMPTY);
            btnMax.setGraphic(restoreIcon);
            btnMax.setTip("恢复");
        } else {
            setBorder(DEFAULT_BORDER);
            btnMax.setGraphic(maxIcon);
            btnMax.setTip("最大化");
        }
    }

    /**
     * 处理action按钮事件
     */
    public interface ActionHandler {
        /**
         * 主题按钮点击时触发
         *
         * @param view 舞台装饰View
         */
        default void onTheme(StageDecorator view) {

        }

        /**
         * 设置按钮点击时触发
         *
         * @param view 舞台装饰View
         */
        default void onSetting(StageDecorator view) {

        }

        /**
         * 窗口关闭时触发
         *
         * @param view 舞台装饰View
         */
        default void onClose(StageDecorator view) {
            view.stage.close();
        }
    }
}
