package com.unclezs.novel.app.main.test;

import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/5/10 21:04
 */
@Data
public class WebDavFile {

  private String name;
  private Long size;
  private boolean exist;
  private String parent;
  private String href;

}
