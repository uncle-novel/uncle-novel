package com.unclezs.novel.app.main.model;

import com.unclezs.novel.analyzer.model.Chapter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * 章节属性
 *
 * @author blog.unclezs.com
 * @date 2021/4/25 16:14
 */
public class ChapterWrapper {

  private final BooleanProperty selected;
  private Chapter chapter;

  public ChapterWrapper(Chapter chapter) {
    this.chapter = chapter;
    this.selected = new SimpleBooleanProperty(true);
  }

  public boolean isSelected() {
    return selected.get();
  }

  public void setSelected(boolean selected) {
    this.selected.set(selected);
  }

  public BooleanProperty selectedProperty() {
    return selected;
  }

  public Chapter getChapter() {
    return chapter;
  }

  public void setChapter(Chapter chapter) {
    this.chapter = chapter;
  }

  @Override
  public String toString() {
    return chapter.getName();
  }
}
