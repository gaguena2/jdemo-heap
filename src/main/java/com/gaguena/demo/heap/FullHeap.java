package com.gaguena.demo.heap;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class FullHeap {

    private static final List<byte[]> HOLDER = new ArrayList<>();

    public static void toFill() {
        try {
            var memoryMXBean = ManagementFactory.getMemoryMXBean();
            var heapUsage = memoryMXBean.getHeapMemoryUsage();

            long maxHeap = heapUsage.getMax(); // Xmx
            long target = (long) (maxHeap * getMaxRamPercentage(1.0));//ou 100%

            System.out.println("Max heap: " + toMB(maxHeap) + " MB");
            System.out.println("Target:   " + toMB(target) + " MB");

            long allocated = 0;
            int chunk = 5 * 1024 * 1024; // 5 MB

            while (allocated < target) {
                HOLDER.add(new byte[chunk]);
                allocated += chunk;

                var current = memoryMXBean.getHeapMemoryUsage();
                System.out.println("Usado: " + toMB(current.getUsed()) + " MB");

                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static long toMB(long bytes) {
        return bytes / 1024 / 1024;
    }
    
    private static double getMaxRamPercentage(double defaultValue) {
        var runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        Optional<String> opt = runtimeMXBean.getInputArguments()
            .stream()
            .filter(arg -> arg.startsWith("-XX:MaxRAMPercentage="))
            .findFirst();

        if (opt.isPresent()) {
            String value = opt.get().split("=")[1];
            return Double.parseDouble(value) / 100.0;
        }

        return defaultValue;
    }
}
