package com.unclezs.novel.app.main.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/5/10 1:38
 */
@Data
@DatabaseTable(tableName = "txt_toc_rule")
public class TxtTocRule {

  @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
  private int id;
  @DatabaseField
  private String name;
  @DatabaseField
  private String rule;
  @DatabaseField
  private int order;
}
