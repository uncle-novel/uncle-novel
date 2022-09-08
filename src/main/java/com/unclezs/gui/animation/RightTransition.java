package com.unclezs.gui.animation;

import com.jfoenix.transitions.CachedTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * 左向又渐入动画
 *
 * @author unclezs.com
 * @date 2020.04.25 11:29
 */
public class RightTransition extends CachedTransition {
    public RightTransition(Node contentHolder) {
        super(contentHolder, new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(contentHolder.translateXProperty(), 200, Interpolator.EASE_BOTH),
                new KeyValue(contentHolder.visibleProperty(), false, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(Duration.millis(10),
                new KeyValue(contentHolder.visibleProperty(), true, Interpolator.EASE_BOTH),
                new KeyValue(contentHolder.opacityProperty(), 0, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(Duration.millis(600),
                new KeyValue(contentHolder.translateXProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(contentHolder.opacityProperty(), 1, Interpolator.EASE_BOTH)))
        );
        setCycleDuration(Duration.seconds(0.5));
        setDelay(Duration.seconds(0));
    }
}
