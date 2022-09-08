/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.unclezs.gui.components;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.svg.SVGGlyph;
import com.unclezs.gui.utils.ResourceUtil;
import com.unclezs.gui.utils.TrayUtil;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.controlsfx.glyphfont.Glyph;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * Window Decorator allow to resize/move its content Note: the default close button will call stage.close() which will
 * only close the current stage. it will not close the java application, however it can be customized by calling {@link
 * #setOnCloseButtonAction(Runnable)}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class StageDecorator extends VBox {

    protected StringProperty title = new SimpleStringProperty();
    protected Text text;
    private Stage primaryStage;
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
    private Timeline windowDecoratorAnimation;
    private StackPane contentPlaceHolder = new StackPane();
    private HBox buttonsContainer;
    private ObjectProperty<Runnable> onCloseButtonAction = new SimpleObjectProperty<>(() ->
        primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
    private BooleanProperty customMaximize = new SimpleBooleanProperty(false);
    private boolean maximized = false;
    private BoundingBox originalBox;
    private JFXButton btnMax;
    private JFXButton btnTray;
    private JFXButton btnSetting;
    private JFXButton btnClose;
    private JFXButton btnMin;
    private JFXButton btnTheme;
    private Node graphic;
    private HBox graphicContainer;
    private List<Node> headerNodes = new ArrayList<>(10);

    /**
     * Create a window decorator for the specified components with the options:
     * - full screen
     * - maximize
     * - minimize
     *
     * @param stage the primary stage used by the application
     * @param node  the components to be decorated
     */
    public StageDecorator(Stage stage, Node node) {
        this(stage, node, true, true, true, false, true);
    }

    /**
     * Create a window decorator for the specified components with the options:
     * - full screen
     * - maximize
     * - minimize
     *
     * @param stage the primary stage used by the application
     * @param node  the components to be decorated
     * @param theme indicates whether to show full screen option or not
     * @param max   indicates whether to show maximize option or not
     * @param min   indicates whether to show minimize option or not
     */
    public StageDecorator(Stage stage, Node node, boolean theme, boolean max, boolean min, boolean tray,
        boolean setting) {
        primaryStage = stage;
        // Note that setting the style to TRANSPARENT is causing performance
        // degradation, as an alternative we set it to UNDECORATED instead.
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        setPickOnBounds(false);
        getStyleClass().add("stage-decorator");
        initializeButtons();
        initializeContainers(node, theme, max, min, tray, setting);
        primaryStage.fullScreenProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                // stop border
                contentPlaceHolder.getStyleClass().remove("resize-border");
                /*
                 *  note the border property MUST NOT be bound to another property
                 *  when going full screen mode, thus the binding will be lost if exisited
                 */
                contentPlaceHolder.borderProperty().unbind();
                contentPlaceHolder.setBorder(Border.EMPTY);
                if (windowDecoratorAnimation != null) {
                    windowDecoratorAnimation.stop();
                }
                windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                    new KeyValue(this.translateYProperty(),
                        -buttonsContainer.getHeight(),
                        Interpolator.EASE_BOTH)));
                windowDecoratorAnimation.setOnFinished((finish) -> {
                    this.getChildren().remove(buttonsContainer);
                    this.setTranslateY(0);
                });
                windowDecoratorAnimation.play();
            } else {
                // add border
                if (windowDecoratorAnimation != null) {
                    if (windowDecoratorAnimation.getStatus() == Animation.Status.RUNNING) {
                        windowDecoratorAnimation.stop();
                    } else {
                        this.getChildren().add(0, buttonsContainer);
                    }
                }
                this.setTranslateY(-buttonsContainer.getHeight());
                windowDecoratorAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                    new KeyValue(this.translateYProperty(),
                        0,
                        Interpolator.EASE_BOTH)));
                windowDecoratorAnimation.setOnFinished((finish) -> {
                    contentPlaceHolder.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(0, 10, 10, 10))));
                    contentPlaceHolder.getStyleClass().add("resize-border");
                });
                windowDecoratorAnimation.play();
            }
        });

        contentPlaceHolder.addEventHandler(MouseEvent.MOUSE_PRESSED, this::updateInitMouseValues);
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, this::updateInitMouseValues);
        buttonsContainer.setStyle(
            "-fx-background-insets: 0 5 0 5 !important;"
        );
        // show the drag cursor on the borders
        addEventFilter(MouseEvent.MOUSE_MOVED, this::showDragCursorOnBorders);

        // handle drag events on the decorator pane
        addEventFilter(MouseEvent.MOUSE_RELEASED, (mouseEvent) -> isDragging = false);
        this.setOnMouseDragged(this::handleDragEventOnDecoratorPane);
        if (setting) {
            this.getStylesheets().setAll(ResourceUtil.loadCss("/css/header.css"));
        }
    }

    /**
     * 换主题
     *
     * @param action /
     */
    public void setOnChangeThemeAction(Consumer<Button> action) {
        this.btnTheme.setOnAction(e -> action.accept(this.btnTheme));
    }

    /**
     * 设置被点击
     *
     * @param action /
     */
    public void setOnSettingAction(Consumer<Button> action) {
        this.btnSetting.setOnAction(e -> action.accept(this.btnSetting));
    }

    private void initializeButtons() {

        btnTheme = new JFXButton();
        SVGGlyph themeIcon = new SVGGlyph(
            "M772.8 96v64l163.2 161.6-91.2 91.2c-12.8-11.2-27.2-16-43.2-16-36.8 0-65.6 28.8-65.6 65.6V800c0 35.2-28.8 64-64 64H352c-35.2 0-64-28.8-64-64V462.4c0-36.8-28.8-65.6-65.6-65.6-16 0-32 6.4-43.2 16l-91.2-91.2L249.6 160h40l1.6 1.6C336 228.8 420.8 272 512 272c91.2 0 176-41.6 220.8-110.4 0-1.6 1.6-1.6 1.6-1.6h38.4V96M291.2 96H256c-22.4 0-38.4 6.4-49.6 19.2L43.2 276.8c-25.6 25.6-25.6 65.6 0 89.6l94.4 94.4c11.2 11.2 27.2 17.6 41.6 17.6s30.4-6.4 41.6-17.6h1.6c1.6 0 1.6 0 1.6 1.6V800c0 70.4 57.6 128 128 128h320c70.4 0 128-57.6 128-128V462.4c0-1.6 0-1.6 1.6-1.6h1.6c11.2 11.2 27.2 17.6 41.6 17.6 16 0 30.4-6.4 41.6-17.6l94.4-94.4c25.6-25.6 25.6-65.6 0-89.6L819.2 115.2c-12.8-12.8-28.8-19.2-46.4-19.2h-40c-22.4 0-41.6 11.2-54.4 30.4-33.6 49.6-96 81.6-168 81.6s-134.4-33.6-168-81.6C332.8 107.2 312 96 291.2 96z");
        btnTheme.setGraphic(themeIcon);
        btnTheme.setCursor(Cursor.HAND);
        btnTheme.setTooltip(new Tooltip("换肤"));

        btnSetting = new JFXButton();
        Glyph settingIcon = new Glyph("FontAwesome", '\uf0c9');
        btnSetting.setGraphic(settingIcon);
        btnSetting.setCursor(Cursor.HAND);
        btnSetting.setTooltip(new Tooltip("设置"));

        btnTray = new JFXButton();
        Glyph trayIcon = new Glyph("FontAwesome", '\uf107');
        btnTray.setGraphic(trayIcon);
        btnTray.setCursor(Cursor.HAND);
        btnTray.setOnAction(e -> TrayUtil.tray());
        btnTray.setTooltip(new Tooltip("最小化到托盘(Alt+U)"));

        btnClose = new JFXButton();
        btnClose.setCursor(Cursor.HAND);
        btnClose.setOnAction((action) -> onCloseButtonAction.get().run());
        Glyph closeIcon = new Glyph("FontAwesome", '\uf011');
        closeIcon.getStyleClass().addAll("header-icon-exit");
        btnClose.setGraphic(closeIcon);
        btnClose.setTooltip(new Tooltip("退出"));

        btnMin = new JFXButton();
        btnMin.setCursor(Cursor.HAND);
        btnMin.setOnAction((action) -> primaryStage.setIconified(true));
        Glyph minIcon = new Glyph("FontAwesome", '\uf068');
        btnMin.setGraphic(minIcon);
        btnMin.setRipplerFill(Color.WHITE);
        btnMin.setTooltip(new Tooltip("最小化"));

        btnMax = new JFXButton();
        btnMax.setCursor(Cursor.HAND);
        btnMax.setRipplerFill(Color.WHITE);
        Glyph resizeMaxIcon = new Glyph("FontAwesome", '\uf2d0');
        Glyph resizeMinIcon = new Glyph("FontAwesome", '\uf2d2');
        btnMax.setOnAction((action) -> maximize(resizeMinIcon, resizeMaxIcon));
        btnMax.setGraphic(resizeMaxIcon);
        btnMax.setTooltip(new Tooltip("最大化"));
    }

    private void maximize(Glyph resizeMin, Glyph resizeMax) {
        if (!isCustomMaximize()) {
            primaryStage.setMaximized(!primaryStage.isMaximized());
            maximized = primaryStage.isMaximized();
            if (primaryStage.isMaximized()) {
                btnMax.setGraphic(resizeMin);
                btnMax.setTooltip(new Tooltip("恢复"));
            } else {
                btnMax.setGraphic(resizeMax);
                btnMax.setTooltip(new Tooltip("最大化"));
            }
        } else {
            if (!maximized) {
                // store original bounds
                originalBox = new BoundingBox(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(),
                    primaryStage.getHeight());
                // get the max stage bounds
                Screen screen = Screen.getScreensForRectangle(primaryStage.getX(),
                    primaryStage.getY(),
                    primaryStage.getWidth(),
                    primaryStage.getHeight()).get(0);
                Rectangle2D bounds = screen.getVisualBounds();
                BoundingBox maximizedBox = new BoundingBox(bounds.getMinX(),
                    bounds.getMinY(),
                    bounds.getWidth(),
                    bounds.getHeight());
                // maximized the stage
                primaryStage.setX(maximizedBox.getMinX());
                primaryStage.setY(maximizedBox.getMinY());
                primaryStage.setWidth(maximizedBox.getWidth());
                primaryStage.setHeight(maximizedBox.getHeight());
                btnMax.setGraphic(resizeMin);
                btnMax.setTooltip(new Tooltip("恢复"));
            } else {
                // restore stage to its original size
                primaryStage.setX(originalBox.getMinX());
                primaryStage.setY(originalBox.getMinY());
                primaryStage.setWidth(originalBox.getWidth());
                primaryStage.setHeight(originalBox.getHeight());
                originalBox = null;
                btnMax.setGraphic(resizeMax);
                btnMax.setTooltip(new Tooltip("最大化"));
            }
            maximized = !maximized;
            primaryStage.setMaximized(maximized);
        }
    }

    private void initializeContainers(Node node, boolean theme, boolean max, boolean min, boolean tray,
        boolean setting) {
        buttonsContainer = new HBox();
        buttonsContainer.getStyleClass().add("header-container");
        // BINDING
        buttonsContainer.setPadding(new Insets(6));
        buttonsContainer.setAlignment(Pos.CENTER_RIGHT);
        // customize decorator buttons
        List<Node> btns = new ArrayList<>();
        if (theme) {
            btnTheme.setTranslateX(-10);
            btns.add(btnTheme);
        }
        if (setting) {
            btnSetting.setTranslateX(-10);
            btns.add(btnSetting);
            Pane separator = new Pane();
            separator.setTranslateX(-5);
            separator.getStyleClass().addAll("header-line");
            btns.add(separator);
        }
        if (tray) {
            btns.add(btnTray);
        }
        if (min) {
            btns.add(btnMin);
        }
        if (max) {
            btns.add(btnMax);
            // maximize/restore the window on header double click
            buttonsContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
                if (mouseEvent.getClickCount() == 2) {
                    btnMax.fire();
                }
            });
        }
        btns.add(btnClose);
        Label text = new Label();
        text.setFont(new Font(16));
        //binds the Text's text to title
        text.textProperty().bind(title);
        //binds title to the primaryStage's title
        title.bind(primaryStage.titleProperty());
        graphicContainer = new HBox();
        graphicContainer.setPickOnBounds(false);
        graphicContainer.setAlignment(Pos.CENTER_LEFT);
        graphicContainer.getChildren().setAll(text);
        HBox graphicTextContainer = new HBox(graphicContainer, text);
        graphicTextContainer.setAlignment(Pos.CENTER_LEFT);
        graphicTextContainer.setPickOnBounds(false);
        HBox.setHgrow(graphicTextContainer, Priority.ALWAYS);
        HBox.setMargin(graphicContainer, new Insets(0, 15, 0, 0));
        HBox.setMargin(btnClose, new Insets(0, 10, 0, 0));
        buttonsContainer.getChildren().setAll(graphicTextContainer);
        buttonsContainer.getChildren().addAll(btns);
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, (enter) -> allowMove = true);
        buttonsContainer.addEventHandler(MouseEvent.MOUSE_EXITED, (enter) -> {
            if (!isDragging) {
                allowMove = false;
            }
        });
        buttonsContainer.setMinWidth(180);
        contentPlaceHolder.getStyleClass().add("content-container");
        contentPlaceHolder.setMinSize(0, 0);
        contentPlaceHolder.getChildren().add(node);
        ((Region) node).setMinSize(0, 0);
        VBox.setVgrow(contentPlaceHolder, Priority.ALWAYS);
        contentPlaceHolder.getStyleClass().add("resize-border");
        // BINDING
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(((Region) node).widthProperty());
        clip.heightProperty().bind(((Region) node).heightProperty());
        node.setClip(clip);
        headerNodes.addAll(buttonsContainer.getChildren());
        this.getChildren().addAll(buttonsContainer, contentPlaceHolder);
    }


    private void showDragCursorOnBorders(MouseEvent mouseEvent) {
        if (primaryStage.isMaximized() || primaryStage.isFullScreen() || maximized) {
            this.setCursor(Cursor.DEFAULT);
            return; // maximized mode does not support resize
        }
        if (!primaryStage.isResizable()) {
            return;
        }
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        if (contentPlaceHolder.getBorder() != null && contentPlaceHolder.getBorder().getStrokes().size() > 0) {
            double borderWidth = contentPlaceHolder.snappedLeftInset();
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

    private void handleDragEventOnDecoratorPane(MouseEvent mouseEvent) {
        isDragging = true;
        if (!mouseEvent.isPrimaryButtonDown() || (xOffset == -1 && yOffset == -1)) {
            return;
        }
        //Long press generates drag event!
        if (primaryStage.isFullScreen() || mouseEvent.isStillSincePress() || maximized) {
            return;
        }
        double newX = mouseEvent.getScreenX();
        double newY = mouseEvent.getScreenY();


        double deltax = newX - initX;
        double deltay = newY - initY;
        Cursor cursor = this.getCursor();

        if (Cursor.E_RESIZE.equals(cursor)) {
            setStageWidth(initWidth + deltax);
            mouseEvent.consume();
        } else if (Cursor.NE_RESIZE.equals(cursor)) {
            if (setStageHeight(initHeight - deltay)) {
                primaryStage.setY(initStageY + deltay);
            }
            setStageWidth(initWidth + deltax);
            mouseEvent.consume();
        } else if (Cursor.SE_RESIZE.equals(cursor)) {
            setStageWidth(initWidth + deltax);
            setStageHeight(initHeight + deltay);
            mouseEvent.consume();
        } else if (Cursor.S_RESIZE.equals(cursor)) {
            setStageHeight(initHeight + deltay);
            mouseEvent.consume();
        } else if (Cursor.W_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltax)) {
                primaryStage.setX(initStageX + deltax);
            }
            mouseEvent.consume();
        } else if (Cursor.SW_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltax)) {
                primaryStage.setX(initStageX + deltax);
            }
            setStageHeight(initHeight + deltay);
            mouseEvent.consume();
        } else if (Cursor.NW_RESIZE.equals(cursor)) {
            if (setStageWidth(initWidth - deltax)) {
                primaryStage.setX(initStageX + deltax);
            }
            if (setStageHeight(initHeight - deltay)) {
                primaryStage.setY(initStageY + deltay);
            }
            mouseEvent.consume();
        } else if (Cursor.N_RESIZE.equals(cursor)) {
            if (setStageHeight(initHeight - deltay)) {
                primaryStage.setY(initStageY + deltay);
            }
            mouseEvent.consume();
        } else if (allowMove) {
            primaryStage.setX(mouseEvent.getScreenX() - xOffset);
            primaryStage.setY(mouseEvent.getScreenY() - yOffset);
            mouseEvent.consume();
        }
    }

    private void updateInitMouseValues(MouseEvent mouseEvent) {
        initStageX = primaryStage.getX();
        initStageY = primaryStage.getY();
        initWidth = primaryStage.getWidth();
        initHeight = primaryStage.getHeight();
        initX = mouseEvent.getScreenX();
        initY = mouseEvent.getScreenY();
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }

    public JFXButton getBtnSetting() {
        return btnSetting;
    }

    private boolean isRightEdge(double x) {
        final double width = this.getWidth();
        return x < width && x > width - contentPlaceHolder.snappedLeftInset();
    }

    private boolean isTopEdge(double y) {
        return y >= 0 && y < contentPlaceHolder.snappedLeftInset();
    }

    private boolean isBottomEdge(double y) {
        final double height = this.getHeight();
        return y < height && y > height - contentPlaceHolder.snappedLeftInset();
    }

    private boolean isLeftEdge(double x) {
        return x >= 0 && x < contentPlaceHolder.snappedLeftInset();
    }

    private boolean setStageWidth(double width) {
        if (width >= primaryStage.getMinWidth() && width >= buttonsContainer.getMinWidth()) {
            primaryStage.setWidth(width);
            return true;
        } else if (width >= primaryStage.getMinWidth() && width <= buttonsContainer.getMinWidth()) {
            width = buttonsContainer.getMinWidth();
            primaryStage.setWidth(width);
        }
        return false;
    }

    private boolean setStageHeight(double height) {
        if (height >= primaryStage.getMinHeight() && height >= buttonsContainer.getHeight()) {
            primaryStage.setHeight(height);
            return true;
        } else if (height >= primaryStage.getMinHeight() && height <= buttonsContainer.getHeight()) {
            height = buttonsContainer.getHeight();
            primaryStage.setHeight(height);
        }
        return false;
    }

    /**
     * set a speficed runnable when clicking on the close button
     *
     * @param onCloseButtonAction runnable to be executed
     */
    public void setOnCloseButtonAction(Runnable onCloseButtonAction) {
        this.onCloseButtonAction.set(onCloseButtonAction);
    }

    /**
     * this property is used to replace JavaFX maximization
     * with a custom one that prevents hiding windows taskbar when
     * the StageDecorator is maximized.
     *
     * @return customMaximizeProperty whether to use custom maximization or not.
     */
    public final BooleanProperty customMaximizeProperty() {
        return this.customMaximize;
    }

    /**
     * @return whether customMaximizeProperty is active or not
     */
    public final boolean isCustomMaximize() {
        return this.customMaximizeProperty().get();
    }

    /**
     * set customMaximize property
     *
     * @param customMaximize
     */
    public final void setCustomMaximize(final boolean customMaximize) {
        this.customMaximizeProperty().set(customMaximize);
    }

    /**
     * @param maximized
     */
    public void setMaximized(boolean maximized) {
        if (this.maximized != maximized) {
            Platform.runLater(() -> {
                btnMax.fire();
            });
        }
    }

    /**
     * will change the decorator content
     *
     * @param content
     */
    public void setContent(Node content) {
        this.contentPlaceHolder.getChildren().setAll(content);
    }

    /**
     * will get the title
     *
     * @deprecated Use {@link StageDecorator#setTitle(java.lang.String)} instead.
     */
    public String getText() {
        return getTitle();
    }

    /**
     * will set the title
     *
     * @param text
     * @deprecated Use {@link StageDecorator#setTitle(java.lang.String)} instead.
     */
    public void setText(String text) {
        setTitle(text);
    }

    public String getTitle() {
        return title.get();
    }

    /**
     * If you want the {@code primaryStage}'s title and the {@code StageDecorator}'s title to be different, then
     * go ahead and use this method.
     * <p>
     * By default, this title property is bound to the {@code primaryStage}'s title property-so merely setting the
     * {@code primaryStage}'s title, will set the {@code StageDecorator}'s title.
     */
    public void setTitle(String title) {
        this.title.unbind();
        this.title.set(title);
    }

    /**
     * By default this title property is bound to the primaryStage's title property.
     * <p>
     * To change it to something else, use <pre>
     *     {@code jfxDecorator.titleProperty().unbind();}</pre> first.
     */
    public StringProperty titleProperty() {
        return title;
    }

    public void setGraphic(Node node) {
        if (graphic != null) {
            graphicContainer.getChildren().remove(graphic);
        }
        if (node != null) {
            graphicContainer.getChildren().add(0, node);
        }
        graphic = node;
    }

    public Node getGraphic(Node node) {
        return graphic;
    }

    public void showHeader() {
        buttonsContainer.getChildren().setAll(headerNodes);
    }

    public void hiddenHeader() {
        buttonsContainer.getChildren().clear();
    }

    public HBox header() {
        return buttonsContainer;
    }

    public JFXButton getBtnTheme() {
        return btnTheme;
    }
}
