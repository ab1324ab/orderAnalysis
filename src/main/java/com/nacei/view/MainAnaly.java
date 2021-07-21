package com.nacei.view;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.nacei.model.OrderAnalysis;
import com.nacei.model.OrderAnalysisColumn;
import com.nacei.service.OrderAnalysisService;
import com.nacei.service.impl.OrderAnalysisServiceImpl;
import com.nacei.util.JFileChooserFilter;
import com.nacei.util.OrderDataListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class MainAnaly extends JPanel {
    private JPanel panel1;
    private JButton excelButton;
    private JTextArea textArea1;
    private JProgressBar progressBar1;
    private JTextArea textArea2;
    private JButton checkjbott;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JProgressBar progressBar2;
    // 文件路径
    private String filepath;

    public MainAnaly(final JFrame jFrame) {
        this.setLayout(new BorderLayout());
        this.add(panel1);

        OrderAnalysisService service = new OrderAnalysisServiceImpl();
        List<OrderAnalysisColumn> column = service.informationColumn();
        for (OrderAnalysisColumn val : column) {
            comboBox1.addItem(val.column_comment);
        }
        textArea1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                comboBox2.removeAllItems();
                String str = "";
                try {
                    str = e.getDocument().getText(0, e.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                String[] strings = str.split("\t");
                for (int i = 0; i < strings.length; i++) {
                    comboBox2.addItem(strings[i]);
                }
                System.out.println("输入改变");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                comboBox2.removeAllItems();
                String str = "";
                try {
                    str = e.getDocument().getText(0, e.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                String[] strings = str.split("\t");
                for (int i = 0; i < strings.length; i++) {
                    comboBox2.addItem(strings[i]);
                }
                System.out.println("删除改变");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("改变");
            }
        });

        // 设置默认值
        textArea1.setText("订单编号\t企业运单号\t商品价格（含抵扣金额）\t实际支付金额（最终支付）\t运费\t保价费\t非现金抵扣金额\t预收税金（9.1%）\t毛重（公斤）\t净重（公斤）\t件数（包裹数量）\t电商平台订购人姓名\t电商平台订购人电话\t电商平台订购人证件类型\t电商平台订购人证件号\t收件人省市区代码\t收件地址\t发货人姓名\t发货人国家代码\t发货人省市区代码\t发货人详细地址\t发货人手机\t原产国（地区）代码\t商品名称\t商品品牌\t商品规格\t商品数量\t商品单价\t商品总价\t商品货号\t商品条形码\t第一法定数量\t第一法定计量单位\t第二法定数量\t第二法定计量单位\t商品编码（原商品海关备案号）\t国检备案号\t订单备注\t码头/货场代码\t监管场所\t物流企业名称\t货物存放地\t包装种类\t分拣号\t计量单位\t运单创建日期\n");
        // 设置数据库默认选中
        comboBox1.setSelectedItem("企业运单号");
        // 设置Excel默认选中
        comboBox2.setSelectedItem("企业运单号");

        excelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excelButton.setEnabled(false);
                FileSystemView fsv = FileSystemView.getFileSystemView();
                JFileChooser chooser = new JFileChooser(fsv.getHomeDirectory().getPath());
                // 设置只能选择文档
                chooser.addChoosableFileFilter(new JFileChooserFilter("xlsx"));
                chooser.addChoosableFileFilter(new JFileChooserFilter("xls"));
                String directoryURL = "";
                int returnVal = chooser.showOpenDialog(jFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    directoryURL = chooser.getSelectedFile().getPath();
                }
                if (!StringUtils.isEmpty(directoryURL)) {
                    filepath = directoryURL;
                    textArea2.setText("文件处理中...请稍后");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EasyExcel.read(filepath, OrderAnalysis.class, new OrderDataListener(progressBar1, progressBar2, textArea2)).sheet().doReadSync();
                            } catch (RuntimeException ex) {
                                ex.printStackTrace();
                                textArea2.setText(ex.getMessage());
                                excelButton.setEnabled(true);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                textArea2.setText(ex.getMessage());
                                excelButton.setEnabled(true);
                            }
                        }
                    }).start();
                    // 这里 也可以不指定class，返回一个list，然后读取第一个sheet 同步读取会自动finish
                } else {
                    excelButton.setEnabled(true);
                }
            }
        });

        progressBar2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                JProgressBar comp = (JProgressBar) evt.getSource();
                if (comp.getValue() == comp.getMaximum()) {
                    textArea2.setText("处理完成没有查询到重复: " + comboBox1.getSelectedItem());
                    excelButton.setEnabled(true);
                }
            }
        });
    }


}
