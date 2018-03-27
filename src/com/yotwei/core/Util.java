package com.yotwei.core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;

/**
 * Created by YotWei on 2018/3/26.
 */
public class Util {
    public static BufferedImage compressImage(BufferedImage src, int dstWidth, int dstHeight) {
        BufferedImage compress = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_RGB);
        compress.getGraphics().drawImage(
                src.getScaledInstance(dstWidth, dstHeight, Image.SCALE_SMOOTH),
                0, 0, dstWidth, dstHeight, null);
        return compress;
    }

    /**
     * 平均灰度值
     */
    public static int getAvgGray(BufferedImage img) {
        int x, y, temp, graySum = 0;
        for (y = 0; y < img.getHeight(); y++) {
            for (x = 0; x < img.getWidth(); x++) {
                temp = img.getRGB(x, y);
                graySum += (((77 * ((temp & 0xff0000) >> 16))
                        + (150 * ((temp & 0x00ff00) >> 8))
                        + (29 * (temp & 0x0000ff))) >> 8);
            }
        }
        return graySum / (img.getWidth() * img.getHeight());
    }

    public static void message(String message) {
        System.out.println(message);
    }
}
