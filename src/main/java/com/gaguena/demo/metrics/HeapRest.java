package com.gaguena.demo.metrics;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/custom-metrics")
public class HeapRest {

    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping("/heap")
    public Map<String, String> heapInfo() {
        Map<String, String> result = new LinkedHashMap<>();

        // pegando todas as métricas de jvm.memory.used
        meterRegistry.get("jvm.memory.used").meters().forEach(meter -> {
            meter.measure().forEach(ms -> {
                meter.getId().getTags().forEach(tag -> {
                    String key = tag.getKey() + ":" + tag.getValue();
                    result.put(key, toReadable((long) ms.getValue()));
                });
            });
        });

        return result;
    }

    private String toReadable(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
}