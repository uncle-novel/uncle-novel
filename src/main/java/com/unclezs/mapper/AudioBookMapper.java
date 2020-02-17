package com.unclezs.mapper;

import com.unclezs.model.AudioBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

/*
 *有声小说
 *@author unclezs.com
 *@date 2019.07.09 13:42
 */
@Mapper
public interface AudioBookMapper {
    //删除指定书籍
    @Delete("delete from audio where id=#{id}")
    void deleteById(@Param("id") Integer id);

    //添加书籍
    @Insert("insert into audio(title,author,broadCasting,imageUrl,url,lastIndex,lastLocation,lastChapter) " +
            "values(#{title},#{author},#{broadCasting},#{imageUrl},#{url},#{lastIndex},#{lastLocation},#{lastChapter})")
    Integer saveBook(AudioBook book);
    //读取全部书籍
    @Select("select * from audio")
    List<AudioBook> findAll();

    //更新书籍听的信息
    @Update("update audio set lastIndex=#{lastIndex},lastLocation=#{lastLocation},lastChapter=#{lastChapter} where id=#{id}")
    void updateBook(AudioBook book);

    //找到最后的id
    @Select("select max(id) from audio")
    Integer findLastKey();
}

