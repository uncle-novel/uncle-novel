package com.unclezs.novel.app.framework.serialize;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

/**
 * 序列化javafx的基本类型的property
 *
 * @author blog.unclezs.com
 * @date 2021/4/28 20:14
 */
public class ValuePropertyTypeAdapter implements JsonSerializer<ObservableValue<Object>>, JsonDeserializer<Object> {


  @Override
  public JsonElement serialize(ObservableValue<Object> objectProperty, Type type, JsonSerializationContext jsonSerializationContext) {
    Object value = objectProperty.getValue();
    if (value instanceof String) {
      return new JsonPrimitive((String) value);
    }
    if (value instanceof Integer) {
      return new JsonPrimitive((Integer) value);
    }
    if (value instanceof Boolean) {
      return new JsonPrimitive((Boolean) value);
    }
    if (value instanceof Double) {
      return new JsonPrimitive((Double) value);
    }
    if (value instanceof Float) {
      return new JsonPrimitive((Float) value);
    }
    return null;
  }

  @Override
  public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    Type actualType = ((ParameterizedType) type).getActualTypeArguments()[0];
    JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
    if (primitive.isBoolean()) {
      return new SimpleObjectProperty<>(primitive.getAsBoolean());
    }
    if (primitive.isString()) {
      return new SimpleObjectProperty<>(primitive.getAsString());
    }
    if (actualType == String.class) {
      return new SimpleObjectProperty<>(primitive.getAsString());
    }
    if (actualType == Integer.class) {
      return new SimpleObjectProperty<>(primitive.getAsInt());
    }
    if (actualType == Boolean.class) {
      return new SimpleObjectProperty<>(primitive.getAsBoolean());
    }
    if (actualType == Double.class) {
      return new SimpleObjectProperty<>(primitive.getAsDouble());
    }
    if (actualType == Float.class) {
      return new SimpleObjectProperty<>(primitive.getAsFloat());
    }
    return null;
  }
}
