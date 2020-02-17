package com.unclezs.mapper;

import com.unclezs.model.ReaderConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/*
 *阅读器配置
 *@author unclezs.com
 *@date 2019.06.25 21:36
 */
@Mapper
public interface ReaderMapper {

    //查询阅读器配置
    @Select("select * from readconfig where id=1")
    ReaderConfig queryConfig();

    //更新阅读器配置
    @Update("update readconfig set bgColor=#{bgColor},fontSize=#{fontSize},fontStyle=#{fontStyle},pageWidth=#{pageWidth},stageWidth=#{stageWidth},stageHeight=#{stageHeight},fontColor=#{fontColor} where id=1")
    void updateConfig(ReaderConfig config);
}
