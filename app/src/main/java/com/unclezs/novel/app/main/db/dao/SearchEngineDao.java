package com.unclezs.novel.app.main.db.dao;

import com.j256.ormlite.table.TableUtils;
import com.unclezs.novel.app.main.db.BaseDao;
import com.unclezs.novel.app.main.db.beans.SearchEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * @author blog.unclezs.com
 * @date 2021/5/7 11:00
 */
public class SearchEngineDao extends BaseDao<SearchEngine> {

  private static SearchEngineDao searchEngineDao;
  private ObservableList<SearchEngine> engines;

  private SearchEngineDao() {
  }

  public static SearchEngineDao me() {
    if (searchEngineDao == null) {
      searchEngineDao = new SearchEngineDao();
    }
    return searchEngineDao;
  }


  public static void main(String[] args) throws SQLException {
    SearchEngineDao dao = new SearchEngineDao();
    TableUtils.dropTable(dao.dao, true);
    TableUtils.createTable(dao.dao);
    // 默认数据
    dao.dao.create(SearchEngine.getDefault());
  }

  public ObservableList<SearchEngine> all() {
    if (engines == null) {
      engines = FXCollections.observableArrayList(selectAll());
    }
    return engines;
  }
}
