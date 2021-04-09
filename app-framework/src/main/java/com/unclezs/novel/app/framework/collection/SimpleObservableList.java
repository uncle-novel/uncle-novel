package com.unclezs.novel.app.framework.collection;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableListBase;

/**
 * @author blog.unclezs.com
 * @date 2021/4/8 16:40
 */
public class SimpleObservableList<E> extends ObservableListBase<E> {

  private final List<E> list;

  public SimpleObservableList(List<E> backingList) {
    this.list = backingList;
  }

  public SimpleObservableList() {
    this.list = new ArrayList<>();
  }

  @Override
  public E get(int index) {
    return list.get(index);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public E remove(int index) {
    onRemove(list.get(index));
    return list.remove(index);
  }

  @Override
  public void add(int index, E element) {
    onAdd(element);
    list.add(index, element);
  }

  public void onAdd(E element) {
    // do it
  }

  public void onRemove(E element) {
    // do it
  }
}
