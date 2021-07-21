package com.nacei.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.nacei.model.OrderAnalysis;
import com.nacei.service.OrderAnalysisService;
import com.nacei.service.impl.OrderAnalysisServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模板的读取类
 *
 * @author Jiaju Zhuang
 */
// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
public class OrderDataListener extends AnalysisEventListener<OrderAnalysis> {

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 30000;

    List<OrderAnalysis> list = new ArrayList<OrderAnalysis>();

    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private OrderAnalysisService service;
    /**
     * 检测进度
     */
    private JProgressBar progressBar1;
    /**
     * 数据保存进度
     */
    private JProgressBar progressBar2;

    private JTextArea textArea;

    public OrderDataListener(JProgressBar progressBar1, JProgressBar progressBar2,JTextArea textArea2) {
        // 这里是demo，所以随便new一个。实际使用如果到了spring,请使用下面的有参构造函数
        service = new OrderAnalysisServiceImpl();
        this.progressBar1 = progressBar1;
        this.progressBar2 = progressBar2;
        this.textArea = textArea2;
        textArea.setText("文件检测中...请稍等");
        this.progressBar1.setValue(0);
        this.progressBar1.setMinimum(0);
        this.progressBar1.setMaximum(100);
        this.progressBar2.setValue(0);
        this.progressBar2.setMinimum(0);
        this.progressBar2.setMaximum(100);
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(OrderAnalysis data, AnalysisContext context) {
        // 当前行 context.readRowHolder().getRowIndex()
        // 总行 context.readSheetHolder().getApproximateTotalRowNumber() - 1
        List filter = list.stream().filter(val -> val.getEnterprise_waybill_no().equals(data.getEnterprise_waybill_no())).collect(Collectors.toList());
        if(filter.size() == 0){
            list.add(data);
        }
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list.clear();
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        // LOGGER.info("所有数据解析完成！");
        progressBar1.setValue(100);
        progressBar2.setValue(100);
    }

    /**
     * 加上存储数据库
     */
    private void saveData() throws RuntimeException {
        progressBar2.setVisible(false);
        List<String> error = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    progressBar1.setVisible(true);
                    BigDecimal aDouble = new BigDecimal(finalI).divide(new BigDecimal(list.size()), 4, BigDecimal.ROUND_HALF_UP);
                    BigDecimal value = aDouble.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    Dimension d = progressBar1.getSize();
                    Rectangle rect = new Rectangle(0, 0, d.width, d.height);
                    progressBar1.setValue(value.intValue());
                    progressBar1.setStringPainted(true);
                    progressBar1.paintImmediately(rect);
                }
            }).start();
            String enterprise_waybill_no = list.get(i).getEnterprise_waybill_no();
            OrderAnalysis orderAnalysis = new OrderAnalysis();
            orderAnalysis.setEnterprise_waybill_no(enterprise_waybill_no);
            List<OrderAnalysis> list = service.selectOrderAnalysisOverhaul(orderAnalysis);
            if (list != null && list.size() > 0) {
                error.add("运单号已存在:" + enterprise_waybill_no);
            }
        }
        if (error.size() > 0) {
            throw new RuntimeException(error.toString().replace("[", "").replace("]", ""));
        }
        progressBar1.setVisible(false);
        for (int i = 0; i < list.size(); i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    textArea.setText("数据保存中...请稍等");
                    progressBar2.setVisible(true);
                    BigDecimal aDouble = new BigDecimal(finalI).divide(new BigDecimal(list.size()), 4, BigDecimal.ROUND_HALF_UP);
                    BigDecimal value = aDouble.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    Dimension d = progressBar2.getSize();
                    Rectangle rect = new Rectangle(0, 0, d.width, d.height);
                    progressBar2.setValue(value.intValue());
                    progressBar2.setStringPainted(true);
                    progressBar2.paintImmediately(rect);
                }
            }).start();
            service.insertOrderAnalysis(list.get(i));
        }
    }
}