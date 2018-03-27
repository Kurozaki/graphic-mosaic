package com.yotwei.client;

import com.yotwei.core.GraphicsMosaicHandler;


/**
 * Created by YotWei on 2018/3/26.
 */
public class GMClient {

    public static void main(String[] args) throws Exception {
        GraphicsMosaicHandler handler = new GraphicsMosaicHandler();
        System.out.println("GM process with return code: " + handler.handle("imgs/shout.png","imgs/"));

    }
}
