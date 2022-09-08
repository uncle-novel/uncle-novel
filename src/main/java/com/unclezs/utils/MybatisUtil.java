package com.unclezs.utils;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.InputStream;
import java.util.function.Function;

/**
 * mybatis工具类 mbp
 *
 * @author unclezs.com
 * @date 2020.04.22 11:15
 */
public class MybatisUtil {
    private static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<>();
    private static SqlSessionFactory factory;

    static {
        String resource = "conf/mybatis.cfg.xml";
        //加载mybatis配置文件
        InputStream stream = MybatisUtil.class.getClassLoader().getResourceAsStream(resource);
        //通过配置文件获取session工厂
        factory = new MybatisSqlSessionFactoryBuilder().build(stream);
    }

    /**
     * 获取factory
     *
     * @return /
     */
    public static SqlSessionFactory getFactory() {
        return factory;
    }

    /**
     * 获取mapper
     *
     * @param clazz /
     * @param <E>   /
     * @return /
     */
    public static <E> E getMapper(Class<E> clazz) {
        SqlSession session = getSqlSession(true);
        return session.getMapper(clazz);
    }

    /**
     * 打开session
     *
     * @param autoCommit /
     * @return /
     */
    public static SqlSession getSqlSession(boolean autoCommit) {
        //从当前线程中获取SqlSession对象
        SqlSession sqlSession = threadLocal.get();
        //如果SqlSession对象为空
        if (sqlSession == null) {
            //在SqlSessionFactory非空的情况下，获取SqlSession对象
            sqlSession = factory.openSession(autoCommit);
            //将SqlSession对象与当前线程绑定在一起
            threadLocal.set(sqlSession);
        }
        //返回SqlSession对象
        return sqlSession;
    }

    /**
     * 打开session
     *
     * @return /
     */
    public static SqlSession getSqlSession() {
        return getSqlSession(true);
    }

    /**
     * 关闭SqlSession与当前线程分开
     */
    public static void closeSqlSession() {
        //从当前线程中获取SqlSession对象
        SqlSession sqlSession = threadLocal.get();
        //如果SqlSession对象非空
        if (sqlSession != null) {
            //关闭SqlSession对象
            sqlSession.close();
            //分开当前线程与SqlSession对象的关系，目的是让GC尽早回收
            threadLocal.remove();
        }
    }

    /**
     * 执行  然后自动关闭session
     *
     * @param mapperClazz /
     * @param function    /
     * @param <T>         /
     * @param <R>         /
     * @return /
     */
    public static <T, R> R execute(Class<T> mapperClazz, Function<T, R> function) {
        SqlSession session = factory.openSession(true);
        T mapper = session.getMapper(mapperClazz);
        R apply = function.apply(mapper);
        session.close();
        return apply;
    }
}
