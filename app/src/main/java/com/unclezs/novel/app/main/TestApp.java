package com.unclezs.novel.app.main;


import com.unclezs.novel.app.framework.serialize.PropertyJsonSerializer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
@Data
public class TestApp {

  private SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(true);
  private SimpleStringProperty stringProperty = new SimpleStringProperty("123");
  private SimpleDoubleProperty doubleProperty = new SimpleDoubleProperty(123D);
  private SimpleIntegerProperty integerProperty = new SimpleIntegerProperty(1122);
  private ObservableList<TestApp> list = FXCollections.observableArrayList();

  public static void main(String[] args) throws Exception {
    String res = "com/sun/javafx/scene/control/skin/modena/modena.css";
    TestApp app = new TestApp();
    app.list.add(new TestApp());
    System.out.println(PropertyJsonSerializer.toJson(app));
    System.out.println(PropertyJsonSerializer.fromJson(PropertyJsonSerializer.toJson(app), TestApp.class));
  }
}
