package com.nacei;

import com.nacei.util.FileLockUtil;
import com.nacei.util.WindowUtil;
import com.nacei.view.MainAnaly;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * 启动类
 * 2021年7月15日10:11:08
 *
 * @author HP
 */
public class GenerateAnalysisExcel {

    private static MainAnaly main = null;

    public static void main(String[] args) {
        FileLockUtil lockUtil = new FileLockUtil();
        final JFrame jFrame = new JFrame();
        jFrame.setTitle("分析工具");
        jFrame.setSize(new Dimension(600, 500));
        //jFrame.setMaximumSize(new Dimension(400,300));
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WindowUtil.windowCentered(jFrame);
        main = new MainAnaly(jFrame);
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(main);
        jFrame.add(main);
        jFrame.setVisible(true);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                try {
                    lockUtil.getFileLock();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(0);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                lockUtil.releaseFileLock();
            }
        });
    }
}
