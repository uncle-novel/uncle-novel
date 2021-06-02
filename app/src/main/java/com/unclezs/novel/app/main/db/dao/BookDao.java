package com.unclezs.novel.app.main.db.dao;

import com.j256.ormlite.table.TableUtils;
import com.unclezs.novel.app.main.db.BaseDao;
import com.unclezs.novel.app.main.db.beans.Book;
import java.sql.SQLException;

/**
 * @author blog.unclezs.com
 * @date 2021/5/7 11:00
 */
public class BookDao extends BaseDao<Book> {

  public static void main(String[] args) throws SQLException {
    BookDao dao = new BookDao();
    TableUtils.dropTable(dao.dao, true);
    TableUtils.createTable(dao.dao);
  }
}
