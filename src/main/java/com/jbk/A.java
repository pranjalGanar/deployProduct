package com.jbk;
import java.util.Random;


public class A {
	public static void main(String[] args) {
	        Random random = new Random();
	        int randomNumber = random.nextInt(90) + 10; // generates a random number between 10 and 99
	        System.out.println("Random 2-digit number: " + randomNumber);
	    }
	}


