package com.unclezs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unclezs.model.Book;
import org.apache.ibatis.annotations.Mapper;

/**
 * 书架书籍
 *
 * @author uncle
 * @date 2020/5/13 19:20
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
