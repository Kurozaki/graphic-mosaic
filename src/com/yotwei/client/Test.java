package com.yotwei.client;

import java.io.File;
import java.util.Stack;

/**
 * Created by YotWei on 2018/3/27.
 */
public class Test {

    private static String path = "F:\\htmls\\avatar";
    private static String outPath = "weixinAvatars\\";

    public static void main(String[] args) {
        File outDir = new File(outPath);
        if (!outDir.exists() || !outDir.isDirectory()) {
            outDir.mkdir();
        }
        Stack<File> stack = new Stack<>();
        stack.push(new File(path));
        while (!stack.empty()) {
            File file = stack.pop();
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    stack.push(f);
                }
            } else {
                file.renameTo(new File(outPath, file.getName()));
            }
        }
    }
}
