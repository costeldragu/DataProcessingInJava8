package com.mdc.optional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OptionalExample {

    public static void main(String... argv) {
        List<Double> result = new ArrayList<>();
//        ThreadLocalRandom.current().doubles(10_000).boxed().parallel()
//                .forEach(d -> NewMath.inv(d).ifPresent(inv -> NewMath.sqrt(inv).ifPresent(result::add)));


        Function<Double, Stream<Double>> flatMapper = d -> NewMath.inv(d).flatMap(inv -> NewMath.sqrt((inv)))
                .map(Stream::of)
                .orElseGet(Stream::empty);
        System.out.println("# results " + result.size());
        List<Double> rightResults;

        long time = System.currentTimeMillis();
        rightResults = ThreadLocalRandom.current().doubles(10_000_000)
                .parallel()
//                .map(d -> d * 20 - 10)
                .boxed()
                .flatMap(flatMapper)
                .collect(Collectors.toList());
        long time2 = System.currentTimeMillis();
        System.out.println("# rightResults " + rightResults.size() + " time " + (time2 - time));
        time = System.currentTimeMillis();
        rightResults = ThreadLocalRandom.current().doubles(10_000_000)

//                .map(d -> d * 20 - 10)
                .boxed()
                .flatMap(flatMapper)
                .collect(Collectors.toList());
        time2 = System.currentTimeMillis();

        System.out.println("# rightResults " + rightResults.size() + " time " + (time2 - time));

    }
}
