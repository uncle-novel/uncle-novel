package com.unclezs.novel.app.main.views.animation;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author blog.unclezs.com
 * @since 2021/5/8 16:49
 */
public class NextPageTransition extends Transition {

  private static final DropShadow SHADOW =
    new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.26), 15, 0.26, 3, 0);
  private final Timeline timeline;
  private final Region node;
  private final Region container;

  public NextPageTransition(Region node, Region container) {
    this.container = container;
    this.node = node;
    this.timeline = new Timeline(
      new KeyFrame(Duration.ZERO,
        new KeyValue(node.translateXProperty(), 0, Interpolator.EASE_BOTH)
      ),
      new KeyFrame(Duration.millis(10),
        new KeyValue(node.effectProperty(), SHADOW, Interpolator.EASE_BOTH)
      ),
      new KeyFrame(Duration.millis(600),
        new KeyValue(node.translateXProperty(), -container.getWidth(), Interpolator.EASE_BOTH),
        new KeyValue(node.effectProperty(), null, Interpolator.EASE_BOTH)
      ));
    setCycleDuration(Duration.millis(600));
    setDelay(Duration.seconds(0));
  }

  @Override
  protected void interpolate(double d) {
    timeline.getKeyFrames().set(2, new KeyFrame(Duration.millis(600),
      new KeyValue(node.translateXProperty(), -container.getWidth(), Interpolator.EASE_BOTH),
      new KeyValue(node.effectProperty(), null, Interpolator.EASE_BOTH)
    ));
    timeline.playFrom(Duration.seconds(d));
    timeline.stop();
  }
}
