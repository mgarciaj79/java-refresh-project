package org.manuel.test;

import java.util.Arrays;
import java.util.List;

public class StreamsRefresh {
	
	 public static void main (String [] args) {
		 
		 List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		 
		 numeros.stream()
		 .filter(num -> num % 2 != 0) //intermediate operation: Filtra los numeros pares
		 .map(num -> num * num) // Intermediate operation: Eleva al cuadrado cada numero impar
		 .forEach(System.out::println); // Terminal operation: Imprime cada numero impar elevado al cuadrado
		 
	 }

}
