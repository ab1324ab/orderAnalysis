package com.nacei.service.impl;

import com.nacei.model.OrderAnalysis;
import com.nacei.model.OrderAnalysisColumn;
import com.nacei.service.OrderAnalysisService;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class OrderAnalysisServiceImpl extends BaseDaoImpl implements OrderAnalysisService {

    @Override
    public OrderAnalysis selectOrderAnalysisById(String id) {
        SqlSession sqlSession = getSqlSession();
        OrderAnalysis analysis = null;
        try {
            analysis = sqlSession.selectOne("com.nacei.service.OrderAnalysisService.selectOrderAnalysisById", id);
        } catch (Exception e) {
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return analysis;
    }

    @Override
    public List<OrderAnalysis> selectOrderAnalysisOverhaul(OrderAnalysis orderAnalysis) {
        SqlSession sqlSession = getSqlSession();
        List<OrderAnalysis> analysis = null;
        try {
            analysis = sqlSession.selectList("com.nacei.service.OrderAnalysisService.selectOrderAnalysisOverhaul", orderAnalysis);
        } catch (Exception e) {
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return analysis;
    }

    @Override
    public OrderAnalysis selectOrderAnalysis() {
        SqlSession sqlSession = getSqlSession();
        OrderAnalysis analysis = null;
        try {
            analysis = sqlSession.selectOne("com.nacei.service.OrderAnalysisService.selectOrderAnalysis");
        } catch (Exception e) {
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return analysis;
    }

    @Override
    public void insertOrderAnalysis(OrderAnalysis orderAnalysis) {
        SqlSession sqlSession = getSqlSession();
        try {
            sqlSession.selectOne("com.nacei.service.OrderAnalysisService.insertOrderAnalysis", orderAnalysis);
        } catch (Exception e) {
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<OrderAnalysisColumn> informationColumn() {
        SqlSession sqlSession = getSqlSession();
        List<OrderAnalysisColumn> column = null;
        try {
            column = sqlSession.selectList("com.nacei.service.OrderAnalysisService.informationColumn");
        } catch (Exception e) {
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return column;
    }
}
