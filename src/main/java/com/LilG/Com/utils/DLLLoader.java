package com.LilG.Com.utils;

/**
 * Created by ggonz on 8/21/2016.
 */
public class DLLLoader {

    public DLLLoader() {
    }

    public static void loadLibrary(String lib) {
        System.loadLibrary(lib);
    }

    public static void loadFile(String filename) {
        System.load(filename);
    }
}
