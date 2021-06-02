package com.unclezs.novel.app.main.db;

import com.unclezs.novel.app.main.db.dao.AudioBookDao;
import com.unclezs.novel.app.main.db.dao.BookDao;
import com.unclezs.novel.app.main.db.dao.DownloadHistoryDao;
import com.unclezs.novel.app.main.db.dao.SearchEngineDao;
import com.unclezs.novel.app.main.db.dao.TxtTocRuleDao;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @date 2021/6/1 1:04
 */
@Slf4j
public class DataHelper {

  public static void main(String[] args) throws SQLException {
    initDb();
  }

  /**
   * 重建表与初始数据
   */
  public static void initDb() {
    try {
      String[] args = {};
      AudioBookDao.main(args);
      BookDao.main(args);
      DownloadHistoryDao.main(args);
      TxtTocRuleDao.main(args);
      SearchEngineDao.main(args);
    } catch (SQLException e) {
      log.error("初始化数据库失败", e);
    }
  }
}
