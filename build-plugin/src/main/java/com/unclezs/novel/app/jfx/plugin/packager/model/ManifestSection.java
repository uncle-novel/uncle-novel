package com.unclezs.novel.app.jfx.plugin.packager.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Manifest section
 */
@Data
public class ManifestSection implements Serializable {

  private static final long serialVersionUID = 118641813298011799L;
  private String name;
  private Map<String, String> entries = new HashMap<>();
}
