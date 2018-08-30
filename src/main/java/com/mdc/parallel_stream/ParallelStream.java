package com.mdc.parallel_stream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelStream {
    public static void main(String... args) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "2");

//        List<String> strings = new ArrayList<>();
//        List<String> strings = new CopyOnWriteArrayList<>();

        List<String> strings = Stream.iterate("+", s -> s + "+")
                .parallel()
                .limit(1000)
//                .peek(s-> System.out.println(s + " processed in the thread " + Thread.currentThread().getName()))
//        .forEach(strings::add);
                .collect(Collectors.toList());

        System.out.println(strings.size());

    }
}
