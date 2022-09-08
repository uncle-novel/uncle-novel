package com.unclezs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unclezs.model.rule.SearchTextRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文本小说搜索mapper
 *
 * @author unclezs.com
 * @date 2020.04.23 10:36
 */
@Mapper
public interface SearchTextRuleMapper extends BaseMapper<SearchTextRule> {
}
