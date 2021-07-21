package com.nacei.service;

import org.apache.ibatis.session.SqlSession;

public interface BaseDao {

    public SqlSession getSqlSession() ;
}
