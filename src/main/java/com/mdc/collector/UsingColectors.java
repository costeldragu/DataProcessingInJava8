package com.mdc.collector;

import com.mdc.stream_of_number.Shakespeare;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UsingColectors {

    public static void main(String... args) throws URISyntaxException, IOException {
        Set<String> shakespeareWords = Files.lines(Paths.get(Shakespeare.class.getClassLoader()
                .getResource("words.shakespeare.txt").toURI()))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<String> scrableWords = Files.lines(Paths.get(Shakespeare.class.getClassLoader()
                .getResource("ospd.txt").toURI()))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        System.out.println("shakespeareWords : " + shakespeareWords.size());
        System.out.println("scrableWords : " + scrableWords.size());

        final int[] scrabbleENScore = {
                // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p,  q, r, s, t, u, v, w, x, y, z
                1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10
        };

        Function<String, Integer> score = word -> word.chars().map(letter -> scrabbleENScore[letter - 'a']).sum();

        Map<Integer, List<String>> histoWordsByScore =
                shakespeareWords.stream()
                        .filter(scrableWords::contains)
                        .collect(
                                Collectors.groupingBy(score)
                        );

        System.out.println("histoWordsByScore : " + histoWordsByScore.size());

        histoWordsByScore.entrySet().stream()
                .sorted(Comparator.comparing(entry -> -entry.getKey()))
                .limit(3)
                .forEach(System.out::println);


        final int[] scrabbleENDistribution = {
                // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p,  q, r, s, t, u, v, w, x, y, z
                9, 2, 2, 1, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1
        };

        Function<String, Map<Integer, Long>> histoWord =
                word -> word.chars().boxed()
                        .collect(Collectors.groupingBy(letter -> letter, Collectors.counting()));

        System.out.println("histoWord : " + histoWord.apply("whizzing"));

        Function<String, Long> nBlanks =
                word -> histoWord.apply(word)
                        .entrySet().stream()
                        .mapToLong(
                                entry -> Long.max(entry.getValue() -
                                        (long) scrabbleENDistribution[entry.getKey() - 'a'], 0L)
                        )
                        .sum();

        System.out.println("# of blanks for whizzing : " + nBlanks.apply("whizzing"));


        Function<String, Integer> score2 =
                word -> histoWord.apply(word)
                        .entrySet()
                        .stream()
                        .mapToInt(
                                entry -> scrabbleENScore[entry.getKey() - 'a'] *
                                        Integer.min(
                                                entry.getKey(),
                                                scrabbleENDistribution[entry.getKey() - 'a']
                                        )
                        )
                        .sum();

        System.out.println("# score for whizzing : " + score.apply("whizzing"));
        System.out.println("# score for whizzing : " + score2.apply("whizzing"));

        // Map<Integer, List<String>> histoWordsByScore2 =
        shakespeareWords.stream()
                .filter(scrableWords::contains)
                .filter(word -> nBlanks.apply(word) <= 2)
                .collect(
                        Collectors.groupingBy(score2)
                ).entrySet().stream()
                .sorted(Comparator.comparing(entry -> -entry.getKey()))
                .limit(3)
                .forEach(System.out::println);


    }
}
