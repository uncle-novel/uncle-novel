package com.unclezs.novel.app.jfx.packager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * JAR manifest configuration
 */
@Data
public class Manifest implements Serializable {

  private static final long serialVersionUID = -7271763575775465174L;
  private Map<String, String> additionalEntries = new HashMap<>();
  private List<ManifestSection> sections = new ArrayList<>();

  @Override
  public String toString() {
    return "Manifest [additionalEntries=" + additionalEntries + ", sections=" + sections + "]";
  }
}
