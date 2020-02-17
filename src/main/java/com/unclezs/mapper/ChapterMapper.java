package com.unclezs.mapper;

import com.unclezs.model.Chapter;
import org.apache.ibatis.annotations.*;

import java.util.List;

/*
 *@author unclezs.com
 *@date 2019.07.06 20:22
 */
@Mapper
public interface ChapterMapper {
    //保存章节
    @Insert({"<script> insert into chapters(chapterName,chapterUrl,aid) values" +
            "<foreach collection='chapters' index='index' item='item' separator=','>" +
            "(#{item.chapterName},#{item.chapterUrl},#{item.aid})" +
            "</foreach></script>"})
    void saveChapters(@Param("chapters") List<Chapter> list);

    //删除某本书的章节数据
    @Delete("delete from chapters where aid=#{aid}")
    void deleteAllChapters(@Param("aid") Integer aid);

    //查询指定aid所有书籍章节
    @Select("select * from chapters where aid=#{aid}")
    List<Chapter> findAllChapter(@Param("aid") Integer aid);

}
