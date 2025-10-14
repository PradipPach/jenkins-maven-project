package com.example;

/**
 * Main Application Class
 */
public class App {
    
    public static void main(String[] args) {
        System.out.println("Hello from Jenkins Maven Project!");
        
        Calculator calculator = new Calculator();
        int result = calculator.add(5, 3);
        System.out.println("5 + 3 = " + result);
    }
}
