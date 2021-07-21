package com.nacei.util;

import java.awt.*;

public class WindowUtil {

    /**
     * 窗口居中
     * @param frame
     */
    public static void windowCentered(Frame frame) {
        Toolkit kit = frame.getToolkit();
        try {
            Image image = kit.getImage(WindowUtil.class.getClass().getResource("/images/icon.png"));
            frame.setIconImage(image);
            Dimension screenSize = kit.getScreenSize();
            int screenSizeWidth = screenSize.width;
            int screenSizeheight = screenSize.height;
            int windownWidth = frame.getWidth();
            int windownHeight = frame.getHeight();
            frame.setLocation((screenSizeWidth - windownWidth) / 2, (screenSizeheight - windownHeight) / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
