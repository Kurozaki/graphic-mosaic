package com.yotwei.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by YotWei on 2018/3/26.
 */
public class GraphicsMosaicHandler {

    public int handle(String srcPath, String dstPath) throws Exception {
        //read origin image
        Util.message("正在读入图片...");
        BufferedImage inputImg = ImageIO.read(new File(srcPath));
        BufferedImage outputImg = new BufferedImage(
                inputImg.getWidth() * GMConfig.MOSAIC_WIDTH,
                inputImg.getHeight() * GMConfig.MOSAIC_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        //read mosaic images
        Util.message("正在读入马赛克...");
        Map<String, List<String>> filenames = new HashMap<>();
        Map<String, FileNameList> fnameMap = new HashMap<>();
        File pDir = new File(GMConfig.MOSAIC_PROC_PATH);

        for (String name : pDir.list()) {
            String key = name.substring(0, name.indexOf("_"));
            if (!filenames.containsKey(key)) {
                filenames.put(key, new ArrayList<>());
            }
            filenames.get(key).add(name);
        }
        for (Map.Entry<String, List<String>> entry : filenames.entrySet()) {
            fnameMap.put(entry.getKey(), new FileNameList(entry.getValue()));
        }
        int rgb, gray;
        String k;
        Util.message("正在绘制...");
        for (int y = 0; y < inputImg.getHeight(); y++) {
            for (int x = 0; x < inputImg.getWidth(); x++) {
                rgb = inputImg.getRGB(x, y);
                gray = ((77 * ((rgb & 0xff0000) >> 16))
                        + (150 * ((rgb & 0x00ff00) >> 8))
                        + (29 * (rgb & 0x0000ff))) >> 8;
                k = "L" + (gray / GMConfig.GRAY_LEVEL_INTERVAL);
                BufferedImage mosaicImg = ImageIO
                        .read(new File(GMConfig.MOSAIC_PROC_PATH, fnameMap.get(k).next()));
                outputImg.getGraphics().drawImage(mosaicImg,
                        x * GMConfig.MOSAIC_WIDTH,
                        y * GMConfig.MOSAIC_HEIGHT,
                        GMConfig.MOSAIC_WIDTH,
                        GMConfig.MOSAIC_HEIGHT, null);
            }
        }
        Util.message("绘制完成，正在写入文件...");
        ImageIO.write(outputImg, "jpg", new File(dstPath, "out_" + System.currentTimeMillis() + ".jpg"));
        Util.message("写入成功！");
        return 0;
    }

    public void processOriginResources() throws IOException {
        String procPath = GMConfig.MOSAIC_PROC_PATH;
        File dir = new File(procPath);

        if (!dir.isDirectory() || dir.list().length == 0) {
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("无法创建文件夹: " + procPath);
            }

            String originPath = GMConfig.MOSAIC_ORIGIN_PATH;
            File originDir = new File(originPath);
            if (!originDir.isDirectory()) {
                throw new IOException(String.format("文件夹\"%s\"不存在", originPath));
            }

            //开始读取图片源地
            ExecutorService executor = Executors.newFixedThreadPool(10);
            for (File file : originDir.listFiles()) {
                BufferedImage readImg = ImageIO.read(file);
                if (readImg == null) {
                    continue;
                }
                executor.execute(new CompressTaskRunnable(readImg));
            }
            executor.shutdown();
        }
    }

    static class FileNameList {

        public FileNameList(List<String> list) {
            if (list != null) {
                this.list = list;
                Collections.shuffle(list);
            }
        }

        public String next() {
            if (list == null) {
                return null;
            }
            if (counter == list.size()) {
                counter = 0;
                Collections.shuffle(list);
            }
            return list.get(counter++);
        }

        private List<String> list;
        private int counter = 0;
    }

    static class CompressTaskRunnable implements Runnable {

        private BufferedImage src;

        CompressTaskRunnable(BufferedImage src) {
            this.src = src;
        }

        @Override
        public void run() {
            BufferedImage compress = Util.compressImage(src, GMConfig.MOSAIC_WIDTH, GMConfig.MOSAIC_HEIGHT);
            int gray = Util.getAvgGray(compress);
            try {
                String fileName = "L" + (gray / GMConfig.GRAY_LEVEL_INTERVAL) + "_" +
                        System.currentTimeMillis() + ".jpg";
                ImageIO.write(compress, "jpg", new File(GMConfig.MOSAIC_PROC_PATH, fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
