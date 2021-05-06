package com.unclezs.novel.app.framework.serialize;

import com.google.gson.Gson;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/4/28 20:21
 */
@UtilityClass
public class PropertyJsonSerializer {

  public static final Gson GSON;

  static {
    ValuePropertyTypeAdapter valuePropertyTypeAdapter = new ValuePropertyTypeAdapter();
    GSON = new Gson().newBuilder()
      .setPrettyPrinting()
      .registerTypeAdapter(SimpleStringProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(SimpleBooleanProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(SimpleDoubleProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(SimpleFloatProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(SimpleIntegerProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(StringProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(BooleanProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(DoubleProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(FloatProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(IntegerProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(ObjectProperty.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(ObservableValue.class, valuePropertyTypeAdapter)
      .registerTypeAdapter(ObservableList.class, new ListPropertyTypeAdapter())
      .create();
  }

  public static String toJson(Object obj) {
    return GSON.toJson(obj);
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    return GSON.fromJson(json, clazz);
  }
}
