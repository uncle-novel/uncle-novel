package com.unclezs.gui.animation;

import com.jfoenix.transitions.CachedTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * 中心变大
 *
 * @author unclezs.com
 * @date 2020.04.25 11:29
 */
public class ScaleLargeTransition extends CachedTransition {
    public ScaleLargeTransition(Node contentHolder) {
        super(contentHolder, new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(contentHolder.scaleXProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(contentHolder.scaleYProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(contentHolder.visibleProperty(), false, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(Duration.millis(100),
                new KeyValue(contentHolder.visibleProperty(), true, Interpolator.EASE_BOTH),
                new KeyValue(contentHolder.opacityProperty(), 0, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(contentHolder.scaleXProperty(), 1, Interpolator.EASE_BOTH),
                new KeyValue(contentHolder.scaleYProperty(), 1, Interpolator.EASE_BOTH),
                new KeyValue(contentHolder.opacityProperty(), 1, Interpolator.EASE_BOTH)))
        );
        setCycleDuration(Duration.seconds(0.4));
        setDelay(Duration.seconds(0));
    }
}
