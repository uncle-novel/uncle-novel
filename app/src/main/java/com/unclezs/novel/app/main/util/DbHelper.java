package com.unclezs.novel.app.main.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.unclezs.novel.app.main.db.beans.DownloadHistory;
import com.unclezs.novel.app.main.exception.DbException;
import java.sql.SQLException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Ormlite工具
 *
 * @author blog.unclezs.com
 * @date 2021/5/5 11:15
 */
@Slf4j
@UtilityClass
public class DbHelper {

  private static final String DATABASE_URL = "jdbc:sqlite:conf/core.db";
  private static ConnectionSource connectionSource;

  /**
   * 获取DAO
   *
   * @param clazz 类型
   * @param <T>   类型泛型
   * @param <I>   ID类型
   * @return DAO
   */
  public static <T, I> Dao<T, I> getDao(Class<T> clazz) {
    try {
      return DaoManager.createDao(getConnectionSource(), clazz);
    } catch (SQLException e) {
      log.error("获取DAO失败：{}", clazz, e);
      throw new DbException("获取DAO失败");
    }
  }

  /**
   * 清空表
   */
  public static void clearTable(Class<?> dataClazz) throws SQLException {
    TableUtils.clearTable(getConnectionSource(), dataClazz);
  }

  /**
   * 获取链接资源
   *
   * @return 连接池
   * @throws SQLException 获取链接失败
   */
  public static ConnectionSource getConnectionSource() throws SQLException {
    if (connectionSource == null) {
      connectionSource = new JdbcConnectionSource(DATABASE_URL);
    }
    return connectionSource;
  }

  public static void main(String[] args) throws SQLException {
    TableUtils.dropTable(getDao(DownloadHistory.class), false);
    TableUtils.createTable(getConnectionSource(), DownloadHistory.class);
  }
}
