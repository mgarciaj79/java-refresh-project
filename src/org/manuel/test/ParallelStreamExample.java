package org.manuel.test;

import java.util.Arrays;
import java.util.List;

public class ParallelStreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        numbers.parallelStream() // Create a parallel stream
               .filter(number -> number % 2 != 0)
               .map(number -> {
                   System.out.println("Processing " + number + " in thread: " + Thread.currentThread().getName());
                   // Simulate some computationally intensive task
                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       Thread.currentThread().interrupt();
                   }
                   return number * number;
               })
               .forEach(result -> System.out.println("Result: " + result + " in thread: " + Thread.currentThread().getName()));
    }
}
