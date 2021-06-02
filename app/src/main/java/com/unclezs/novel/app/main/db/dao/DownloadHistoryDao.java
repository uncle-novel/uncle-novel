package com.unclezs.novel.app.main.db.dao;

import com.j256.ormlite.table.TableUtils;
import com.unclezs.novel.app.main.db.BaseDao;
import com.unclezs.novel.app.main.db.beans.DownloadHistory;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

/**
 * 下载历史DAO
 *
 * @author blog.unclezs.com
 * @date 2021/5/5 12:20
 */
@Slf4j
public class DownloadHistoryDao extends BaseDao<DownloadHistory> {

  public static void main(String[] args) throws SQLException {
    DownloadHistoryDao dao = new DownloadHistoryDao();
    TableUtils.dropTable(dao.dao, true);
    TableUtils.createTable(dao.dao);
  }
}
