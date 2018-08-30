package com.mdc.stream_of_number;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class Shakespeare {

    public static void main(String... args) throws URISyntaxException, IOException {
        Set<String> shakespeareWords = Files.lines(Paths.get(Shakespeare.class.getClassLoader()
                .getResource("words.shakespeare.txt").toURI()))
                .map(word -> word.toLowerCase())
                .collect(Collectors.toSet());

        Set<String> scrableWords = Files.lines(Paths.get(Shakespeare.class.getClassLoader()
                .getResource("ospd.txt").toURI()))
                .map(word -> word.toLowerCase())
                .collect(Collectors.toSet());

        System.out.println("shakespeareWords : " + shakespeareWords.size());
        System.out.println("scrableWords : " + scrableWords.size());

        final int[] scrabbleENScore = {
             // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p,  q, r, s, t, u, v, w, x, y, z
                1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10
        };

        Function<String,Integer> score = word -> word.chars().map(letter -> scrabbleENScore[letter - 'a']).sum();

        ToIntFunction<String> intScore =  word -> word.chars().map(letter -> scrabbleENScore[letter - 'a']).sum();

        System.out.println("Score of hello: " + intScore.applyAsInt("hello"));

       String bestWord = shakespeareWords.stream()
               .filter(scrableWords::contains)
               .max(Comparator.comparing(score)).get();

        System.out.println("Best words :" + bestWord);

        IntSummaryStatistics summaryStatistics = shakespeareWords.stream().parallel()
                .filter(scrableWords::contains)
                .mapToInt(intScore)
                .summaryStatistics();

        System.out.println("Stats:" + summaryStatistics);
    }

}
