package com.example.demo;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class TestJMH {


    @Benchmark
    public void chashMap() {
        Random random = new Random();
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap();
        for (int i = 0; i < 10000; i++) {
            map.put("String" + random.nextInt(), "String");
        }

    }

    @Benchmark
    public void treeemap() {
        Random random = new Random();
        TreeMap<String, String> map = new TreeMap();
        for (int i = 0; i < 10000; i++) {
            map.put("String" + random.nextInt(), "String");
        }

    }

    @Benchmark
    public void skipaMap() {
        Random random = new Random();
        ConcurrentSkipListMap<String, String> map = new ConcurrentSkipListMap();
        for (int i = 0; i < 10000; i++) {
            map.put("String" + random.nextInt(), "String");
        }

    }

    @Benchmark
    public void hashMap() {
        Random random = new Random();
        HashMap<String, String> maps = new HashMap();
        for (int i = 0; i < 10000; i++) {
            maps.put("String" + random.nextInt(), "String");
        }
    }

    @Test
    void runBenchmarks() throws RunnerException {
        Options options = new OptionsBuilder()
                .measurementIterations(2)
                .warmupIterations(1)
                .forks(1)
                .verbosity(VerboseMode.NORMAL)
                .include(getClass().getSimpleName())
                .build();

        new Runner(options).run();
    }
}

