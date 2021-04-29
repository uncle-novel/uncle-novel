package com.unclezs.novel.app.framework.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 序列化List
 *
 * @author blog.unclezs.com
 * @date 2021/4/28 21:13
 */
public class ListPropertyTypeAdapter implements JsonSerializer<ObservableList<Object>>, JsonDeserializer<ObservableList<Object>> {

  @Override
  public JsonElement serialize(ObservableList<Object> list, Type type, JsonSerializationContext jsonSerializationContext) {
    JsonArray array = new JsonArray();
    for (Object o : list) {
      array.add(jsonSerializationContext.serialize(o));
    }
    return array;
  }

  @Override
  public ObservableList<Object> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    Type actualType = ((ParameterizedType) type).getActualTypeArguments()[0];
    JsonArray array = jsonElement.getAsJsonArray();
    ObservableList<Object> list = FXCollections.observableArrayList();
    for (JsonElement element : array) {
      list.add(jsonDeserializationContext.deserialize(element, actualType));
    }
    return list;
  }
}
