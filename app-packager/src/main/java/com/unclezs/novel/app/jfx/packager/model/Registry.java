package com.unclezs.novel.app.jfx.packager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Windows Registry entries to be created when installing using Setup
 */
@Data
public class Registry implements Serializable {

  private static final long serialVersionUID = 8310081277297116023L;
  private List<RegistryEntry> entries = new ArrayList<>();
}
