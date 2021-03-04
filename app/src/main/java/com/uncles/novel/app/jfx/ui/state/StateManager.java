package com.uncles.novel.app.jfx.ui.state;

import javafx.stage.Stage;
import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @since 2021/03/04 12:05
 */
@UtilityClass
public class StateManager {
    @Getter(lazy = true)
    private final Stage stage = new Stage();
}
