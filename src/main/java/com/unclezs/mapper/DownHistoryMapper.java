package com.unclezs.mapper;

import com.unclezs.model.DownloadHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/*
 *@author unclezs.com
 *@date 2019.07.06 20:24
 */
@Mapper
public interface DownHistoryMapper {

    //保存下载历史
    @Insert("insert into downHistory(title,type,time,path,imgPath) values(#{title},#{type},#{time},#{path},#{imgPath})")
    void saveDownloadHistory(DownloadHistory history);

    //查询下载历史
    @Select("select * from downHistory order by id desc")
    List<DownloadHistory> findAllDownloadHistory();

    //删除某个下载历史
    @Delete("delete from downHistory where id=#{id}")
    void deleteDownLoadHistroy(@Param("id") String id);

    //查找最后一条记录
    @Select("SELECT * FROM downHistory ORDER BY id DESC LIMIT 1")
    DownloadHistory findLastOne();
}
