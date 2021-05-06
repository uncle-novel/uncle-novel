package com.unclezs.novel.app.main.dao;

import com.j256.ormlite.table.TableUtils;
import com.unclezs.novel.app.main.model.AudioBook;
import java.sql.SQLException;

/**
 * 有声书 DAO
 *
 * @author blog.unclezs.com
 * @date 2021/5/5 21:00
 */
public class AudioBookDao extends BaseDao<AudioBook> {


  public static void main(String[] args) throws SQLException {
    AudioBookDao dao = new AudioBookDao();
    TableUtils.dropTable(dao.dao, true);
    TableUtils.createTable(dao.dao);
  }
}
