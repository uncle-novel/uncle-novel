package com.unclezs.novel.app.main.dao;

import java.sql.SQLException;

/**
 * @author blog.unclezs.com
 * @date 2021/6/1 1:04
 */
public class DataHelper {

  public static void main(String[] args) throws SQLException {
    AudioBookDao.main(args);
    BookDao.main(args);
    DownloadHistoryDao.main(args);
    TxtTocRuleDao.main(args);
    SearchEngineDao.main(args);
  }
}
