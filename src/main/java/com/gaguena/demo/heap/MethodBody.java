package com.gaguena.demo.heap;

public class MethodBody {

    public static String intercept(String arg) {
        // corpo irrelevante para Metaspace
        return "ok-" + arg;
    }
}