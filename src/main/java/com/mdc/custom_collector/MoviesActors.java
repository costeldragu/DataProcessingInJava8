package com.mdc.custom_collector;

import com.mdc.model.Actor;
import com.mdc.model.Movie;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoviesActors {
    public static void main(String... arg) throws URISyntaxException, IOException {

        Stream<String> lines = Files.lines(Paths.get(MoviesActors.class.getClassLoader()
                .getResource("movies-mpaa.txt").toURI()), Charset.forName("windows-1252"));

        List<Movie> movies = new ArrayList<>();

        lines.forEach((String line) -> {
            String[] elements = line.split("/");
            String title = elements[0].substring(0, elements[0].lastIndexOf("(")).trim();
            String releaseYear = elements[0].substring(elements[0].lastIndexOf("(") + 1, elements[0].lastIndexOf(")"));

            if (releaseYear.contains(",")) {
                return;
            }

            Movie movie = new Movie(title, Integer.valueOf(releaseYear));

            for (int i = 1; i < elements.length; i++) {
                String[] name = elements[i].split(", ");
                String lastName = name[0].trim();
                String firstName = "";
                if (name.length > 1) {
                    firstName = name[1].trim();
                }
                Actor actor = new Actor(lastName, firstName);
                movie.addActor(actor);
            }

            movies.add(movie);
        });
        System.out.println("# movies = " + movies.size());
//# nr of actors
        long numberActors = movies.stream().flatMap(movie -> movie.getActors().stream())
                .distinct()
                .count();
        System.out.println("# nr of actors: " + numberActors);

        //# of actors that played in the greatest # of movies

//        Map<Actor, Long> collect =
        Map.Entry<Actor, Long> mostViewActor = movies.stream().flatMap(movie -> movie.getActors().stream())
                .collect(Collectors.groupingBy(
//                        actor -> actor
                        Function.identity()
                        , Collectors.counting()))
                .entrySet().stream()
                .max(
//                        Comparator.comparing(entry -> entry.getValue())
                        Map.Entry.comparingByValue()
                )
                .get();

        System.out.println("Most view actor :" + mostViewActor);

        // actor that played in the greatest # of movies during year
        // Map<release year, Map<Actor, # of Movies during that year>>

        //Map<Integer, HashMap<Actor, AtomicLong>> collect =
        Map.Entry<Integer, Map.Entry<Actor, AtomicLong>> integerEntryEntry = movies.stream()
                .collect(
                        Collectors.groupingBy(movie -> movie.getReleaseYear(),
                                Collector.of(
                                        //supplier
                                        () -> new HashMap<Actor, AtomicLong>(),
                                        //accumulator
                                        (map, movie) -> {
                                            movie.getActors().forEach(actor -> map.computeIfAbsent(actor, a -> new AtomicLong()).incrementAndGet());
                                        },
                                        //combiner
                                        (map1, map2) -> {
                                            map2.entrySet().forEach(entry2 -> map1.merge(
                                                    entry2.getKey(),
                                                    entry2.getValue(),
                                                    (al1, al2) -> {
                                                        al1.addAndGet(al2.get());
                                                        return al1;
                                                    })
                                            );
                                            return map1;
                                        },
                                        Collector.Characteristics.IDENTITY_FINISH
                                ))).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream().max(
                                Map.Entry.comparingByValue(Comparator.comparing(l -> l.get()))
                        ).get()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue(Comparator.comparing(entry -> entry.getValue().get()))
                ).get();

        System.out.println(integerEntryEntry);
    }
}
