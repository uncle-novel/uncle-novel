package com.unclezs.mapper;

import com.unclezs.model.AnalysisConfig;
import org.apache.ibatis.annotations.*;

/*
 *@author unclezs.com
 *@date 2019.07.06 20:23
 */
@Mapper
public interface AnalysisMapper {
    //删除某本书的解析配置
    @Delete("delete from spiderConfig where aid=#{aid}")
    void deleteSpiderConfig(@Param("aid") Integer aid);


    //保存网络小说解析配置
    @Insert("insert into spiderConfig(aid,chapterHead,chapterTail,contentHead,contentTail,cookies,userAgent,adStr,rule,chapterFilter,chapterSort,ncrToZh,traToSimple,startDynamic) " +
            "values(#{aid},#{c.chapterHead},#{c.chapterTail},#{c.contentHead},#{c.contentTail},#{c.cookies},#{c.userAgent},#{c.adStr},#{c.rule},#{c.chapterFilter},#{c.chapterSort},#{c.ncrToZh},#{c.traToSimple},#{c.startDynamic})")
    void saveAnalysisConfig(@Param("c") AnalysisConfig c, @Param("aid") Integer aid);

    //读取网络小说解析配置
    @Select("select * from spiderConfig where aid=#{aid}")
    AnalysisConfig queryAnalysisConfig(@Param("aid") Integer aid);

    //更新Cookies
    @Update("update spiderConfig set cookies=#{cookies} where aid=#{aid}")
    void updateCookies(@Param("aid") Integer id, @Param("cookies") String cookies);

}
