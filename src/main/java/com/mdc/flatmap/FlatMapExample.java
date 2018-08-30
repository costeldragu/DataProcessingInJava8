package com.mdc.flatmap;

import com.mdc.spliterator.CreatingSpliterator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FlatMapExample {

    public static void main(String... arg) throws URISyntaxException, IOException {
        Stream<String> stream1 = Files.lines(Paths.get(CreatingSpliterator.class.getClassLoader()
                .getResource("TomSawyer_01.txt").toURI()));

        Stream<String> stream2 = Files.lines(Paths.get(CreatingSpliterator.class.getClassLoader()
                .getResource("TomSawyer_02.txt").toURI()));

        Stream<String> stream3 = Files.lines(Paths.get(CreatingSpliterator.class.getClassLoader()
                .getResource("TomSawyer_03.txt").toURI()));

        Stream<String> stream4 = Files.lines(Paths.get(CreatingSpliterator.class.getClassLoader()
                .getResource("TomSawyer_04.txt").toURI()));

//        System.out.println("Stream 1 :" + stream1.count());
//        System.out.println("Stream 2 :" + stream2.count());
//        System.out.println("Stream 3 :" + stream3.count());
//        System.out.println("Stream 4 :" + stream4.count());

        Stream<Stream<String>> streamOfStream = Stream.of(stream1, stream2, stream3, stream4);
        //System.out.println("streamOfStream :" + streamOfStream.count());

        Stream<String> streamOfLines = streamOfStream.flatMap(Function.identity());

//        System.out.println("streamOfLines :" + streamOfLines.count());
        Function<String, Stream<String>> lineSplitter = line -> Pattern.compile(" ").splitAsStream(line);
        Stream<String> streamOfWords = streamOfLines.flatMap(lineSplitter).map(String::toLowerCase)
                .distinct();
        System.out.println("streamOfWords :" + streamOfWords.count());
    }
}
