package com.unclezs.novel.app.main.db;

import com.j256.ormlite.dao.Dao;
import com.unclezs.novel.app.framework.util.ReflectUtils;
import com.unclezs.novel.app.main.db.beans.DownloadHistory;
import com.unclezs.novel.app.main.exception.DbException;
import com.unclezs.novel.app.main.util.DbHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;

/**
 * 基础DAO
 *
 * @author blog.unclezs.com
 * @since 2021/5/5 21:01
 */
@Slf4j
public class BaseDao<T> {

  @Getter
  protected Dao<T, Integer> dao;

  protected BaseDao() {
    Class<T> daoClass = ReflectUtils.getSuperClassGenericType(getClass());
    this.dao = DbHelper.getDao(daoClass);
  }

  /**
   * 保存
   *
   * @param data 数据
   */
  public void save(T data) {
    try {
      dao.create(data);
    } catch (SQLException e) {
      log.error("保存失败：{}", data, e);
    }
  }

  /**
   * 更新数据
   *
   * @param data 数据
   */
  public void update(T data) {
    try {
      dao.update(data);
    } catch (SQLException e) {
      log.error("更新失败：{}", data, e);
    }
  }


  /**
   * 查询全部
   *
   * @return list
   */
  public List<T> selectAll() {
    try {
      return dao.queryForAll();
    } catch (SQLException e) {
      log.error("查询全部失败", e);
      throw new DbException(e);
    }
  }

  /**
   * 删除
   *
   * @param data 数据
   */
  public void delete(T data) {
    try {
      dao.delete(data);
    } catch (SQLException e) {
      log.error("删除失败：{}", data, e);
    }
  }

  /**
   * 清空表
   */
  public void clear() {
    try {
      DbHelper.clearTable(DownloadHistory.class);
    } catch (SQLException e) {
      log.error("清空表失败", e);
    }
  }

  /**
   * 新增或更新失败
   *
   * @param data 数据
   */
  public void createOrUpdate(T data) {
    try {
      dao.createOrUpdate(data);
    } catch (SQLException e) {
      log.error("新增或更新失败：{}", data, e);
    }
  }
}
