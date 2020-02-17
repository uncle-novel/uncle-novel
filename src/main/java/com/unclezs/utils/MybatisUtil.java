package com.unclezs.utils;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/*
 *mybatis工具类
 *@author unclezs.com
 *@date 2019.05.17 11:15
 */
public class MybatisUtil {
    private static SqlSessionFactory factory;
    private static SqlSession sqlSession;
    static {
        String resource="conf/SqlMapConfig.xml";
        //加载mybatis配置文件
        InputStream stream = MybatisUtil.class.getClassLoader().getResourceAsStream(resource);
        //创建session工厂
        factory = new SqlSessionFactoryBuilder().build(stream);
    }
    //获取factoey
    public static SqlSessionFactory getFactory(){
        return factory;
    }
    //获取mapper
    public static <E> E getMapper(Class<E> clazz){
        sqlSession=factory.openSession(true);
        return sqlSession.getMapper(clazz);
    }
    //获取当前session
    public static SqlSession getCurrentSqlSession(){
        return sqlSession;
    }
    //打开session
    public static SqlSession openSqlSession(boolean autoComit){
        sqlSession=factory.openSession(autoComit);
        return sqlSession;
    }
}
