package com.nacei.service.impl;

import com.nacei.service.BaseDao;
import com.nacei.util.SqlSessionFactoryUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class BaseDaoImpl implements BaseDao {

    @Override
    public SqlSession getSqlSession() {
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtil.getSqlSessionFactory();
        return sqlSessionFactory.openSession();
    }

}

