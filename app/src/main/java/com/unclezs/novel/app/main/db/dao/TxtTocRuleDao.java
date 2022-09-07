package com.unclezs.novel.app.main.db.dao;

import cn.hutool.core.io.IoUtil;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.table.TableUtils;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.db.BaseDao;
import com.unclezs.novel.app.main.db.beans.TxtTocRule;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author blog.unclezs.com
 * @since 2021/5/10 2:04
 */
@Slf4j
public class TxtTocRuleDao extends BaseDao<TxtTocRule> {

  public static final String PATH = "assets/defaults/toc-rule.json";

  public static void main(String[] args) throws SQLException {
    TxtTocRuleDao dao = new TxtTocRuleDao();
    TableUtils.dropTable(dao.dao, true);
    TableUtils.createTable(dao.dao);
    dao.importDefault();
  }


  /**
   * 导入默认数据
   *
   * @return 默认数据
   */
  public List<TxtTocRule> importDefault() {
    String defaultRuleJson = IoUtil.readUtf8(ResourceUtils.stream(PATH));
    List<TxtTocRule> rules = GsonUtils.me().fromJson(defaultRuleJson, new TypeToken<List<TxtTocRule>>() {
    }.getType());
    try {
      dao.deleteIds(rules.stream().map(TxtTocRule::getId).collect(Collectors.toList()));
      dao.create(rules);
    } catch (SQLException e) {
      log.error("导入默认章节解析规则数据失败");
      e.printStackTrace();
    }
    return rules;
  }

  /**
   * 查询所有，按照order正序
   */
  public List<TxtTocRule> selectAllByOrder() {
    try {
      return dao.queryBuilder().orderBy("order", true).query();
    } catch (SQLException e) {
      log.error("查询全部TXT目录规则失败", e);
    }
    return Collections.emptyList();
  }
}
