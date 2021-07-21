package com.nacei.service;

import com.nacei.model.OrderAnalysis;
import com.nacei.model.OrderAnalysisColumn;

import java.awt.*;
import java.util.List;

public interface OrderAnalysisService {

    OrderAnalysis selectOrderAnalysisById(String id);

    List<OrderAnalysis> selectOrderAnalysisOverhaul(OrderAnalysis orderAnalysis);

    OrderAnalysis selectOrderAnalysis();

    void insertOrderAnalysis(OrderAnalysis orderAnalysis);

    List<OrderAnalysisColumn> informationColumn();
}
