package com.unclezs.novel.app.main.test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.unclezs.novel.app.main.model.DownloadHistory;
import com.unclezs.novel.app.main.util.DbHelper;
import java.sql.SQLException;

/**
 * @author blog.unclezs.com
 * @date 2021/5/4 22:37
 */
public class BoxTest {

  public static void main(String[] args) throws SQLException {
    ConnectionSource connectionSource = DbHelper.getConnectionSource();
    Dao<DownloadHistory, Object> dao = DbHelper.getDao(DownloadHistory.class);
    TableUtils.dropTable(dao, false);
    TableUtils.createTableIfNotExists(connectionSource, DownloadHistory.class);
    System.out.println(dao.queryForAll());
  }

}
