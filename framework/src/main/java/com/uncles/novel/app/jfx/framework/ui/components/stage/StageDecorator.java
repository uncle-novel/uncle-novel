package com.uncles.novel.app.jfx.framework.ui.components.stage;

import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.util.FxmlLoader;
import javafx.beans.DefaultProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/02/28 15:12
 */
@DefaultProperty("content")
@FxView(fxml = "/layout/components/stage-decorator.fxml", bundle = "framework")
public class StageDecorator extends VBox {
    private Stage stage;
    public ImageView logo;
    public Label title;
    public Node content;


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

    public StageDecorator() {
        FXMLLoader fxmlLoader = FxmlLoader.getLoader(StageDecorator.class);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * @param stage 舞台
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        // 透明
        stage.initStyle(StageStyle.TRANSPARENT);
        // 双向绑定舞台标题
        if (title.getText() != null && stage.getTitle() == null) {
            stage.setTitle(getTitle());
        }
        title.textProperty().bindBidirectional(stage.titleProperty());
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        if (this.content != null) {
            getChildren().remove(content);
        }
        getChildren().add(content);
        this.content = content;
    }

    /**
     * 设置logo图标
     *
     * @param logoImagePath logo图标路径
     */
    public void setLogo(String logoImagePath) {
        this.logo.setImage(new Image(logoImagePath));
    }

    public String getTitle() {
        return title.getText();
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }
}
