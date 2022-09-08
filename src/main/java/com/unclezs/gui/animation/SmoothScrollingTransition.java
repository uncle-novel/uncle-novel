package com.unclezs.gui.animation;

import com.jfoenix.transitions.CachedTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;


/**
 * @author uncle
 * @date 2020/6/19 15:18
 */
public class SmoothScrollingTransition extends CachedTransition {
    public SmoothScrollingTransition(ScrollPane contentHolder, double to) {
        super(contentHolder, new Timeline(
            new KeyFrame(Duration.millis(2000),
                new KeyValue(contentHolder.vvalueProperty(), to, Interpolator.EASE_BOTH)))
        );
        setCycleDuration(Duration.seconds(1));
        setDelay(Duration.seconds(0));
    }
}
