package com.unclezs.novel.app.main.db;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.main.db.dao.AudioBookDao;
import com.unclezs.novel.app.main.db.dao.BookDao;
import com.unclezs.novel.app.main.db.dao.DownloadHistoryDao;
import com.unclezs.novel.app.main.db.dao.SearchEngineDao;
import com.unclezs.novel.app.main.db.dao.TxtTocRuleDao;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

/**
 * @author blog.unclezs.com
 * @since 2021/6/1 1:04
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
      FileUtil.mkdir(FileUtil.file("conf"));
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
