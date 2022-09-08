package com.unclezs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unclezs.model.AudioBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 有声小说 书架
 *
 * @author unclezs.com
 * @date 2020.05.09 13:42
 */
@Mapper
public interface AudioBookMapper extends BaseMapper<AudioBook> {

}

