package com.gaguena.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gaguena.demo.heap.FullHeap;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        // vamos popular o heap
        FullHeap.toFill();
        // vamos popular o metaspace
        //FullMetaspace.fill();

    }

}
