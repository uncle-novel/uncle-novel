package com.unclezs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unclezs.model.DownloadRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 下载历史Mapper
 *
 * @author unclezs.com
 * @date 2019.07.06 20:24
 */
@Mapper
public interface DownloadRecordMapper extends BaseMapper<DownloadRecord> {
    /**
     * 清空表
     *
     * @return /
     */
    @Delete("delete from download_record")
    int deleteAll();
}
