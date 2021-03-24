package com.unclezs.novel.app.jfx.plugin.sass;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lars Grefer
 */
@Getter
@Setter
@NoArgsConstructor
public class SassError {

  private int status;
  private String file;
  private int line;
  private int column;
  private String message;
  private String formatted;
}
