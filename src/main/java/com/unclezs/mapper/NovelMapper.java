package com.unclezs.mapper;

import com.unclezs.model.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

/*
 *@author unclezs.com
 *@date 2019.06.23 08:28
 */
@Mapper
public interface NovelMapper {
    //插入
    @Insert("insert into article (name,img,path,cpage,charset,isWeb,vValue) values(#{name},#{img},#{path},#{cpage},#{charset},#{isWeb},#{vValue})")
    void save(Book book);

    //查询所有
    @Select("select * from article")
    List<Book> findAll();

    //查询一本书
    @Select("select * from article where id=#{id}")
    Book findById(@Param("id") Integer id);

    //查最后一条记录
    @Select("select * from article order by id DESC limit 1")
    Book findLastOne();

    //删除数据
    @Delete("delete from article where id=#{id}")
    void deleteById(@Param("id") Integer id);

    //更新阅读位置
    @Update("update article set cpage=#{cpage},vValue=#{vValue} where id=#{id}")
    void updateCPage(@Param("id") Integer id, @Param("cpage") Integer cpage, @Param("vValue") Double vValue);

    //更新书名
    @Update("update article set name=#{name} where id=#{id}")
    void updateBookName(@Param("id") Integer id, @Param("name") String name);

    //更换封面
    @Update("update article set img=#{img} where id=#{id}")
    void updateBookCover(@Param("id") Integer id, @Param("img") String img);

}
