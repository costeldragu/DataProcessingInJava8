package com.mdc.optional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class OptionalExample {

    public static void main(String... argv) {
        List<Double> result = new ArrayList<>();
//        ThreadLocalRandom.current().doubles(10_000).boxed().parallel()
//                .forEach(d -> NewMath.inv(d).ifPresent(inv -> NewMath.sqrt(inv).ifPresent(result::add)));


        Function<Double, Stream<Double>> flatMapper = d -> NewMath.inv(d).flatMap(inv -> NewMath.sqrt((inv)))
                .map(sqrt -> Stream.of(sqrt))
        System.out.printf("# results " + result.size());
    }
}
