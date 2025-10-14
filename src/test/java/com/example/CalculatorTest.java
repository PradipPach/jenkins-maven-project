package com.example;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for Calculator
 */
public class CalculatorTest {
    
    private Calculator calculator;
    
    @Before
    public void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    public void testAdd() {
        assertEquals(8, calculator.add(5, 3));
        assertEquals(0, calculator.add(-5, 5));
        assertEquals(-8, calculator.add(-5, -3));
    }
    
    @Test
    public void testSubtract() {
        assertEquals(2, calculator.subtract(5, 3));
        assertEquals(-10, calculator.subtract(-5, 5));
        assertEquals(-2, calculator.subtract(-5, -3));
    }
    
    @Test
    public void testMultiply() {
        assertEquals(15, calculator.multiply(5, 3));
        assertEquals(-25, calculator.multiply(-5, 5));
        assertEquals(15, calculator.multiply(-5, -3));
    }
    
    @Test
    public void testDivide() {
        assertEquals(2.0, calculator.divide(6, 3), 0.001);
        assertEquals(-1.0, calculator.divide(-5, 5), 0.001);
        assertEquals(1.666, calculator.divide(5, 3), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDivideByZero() {
        calculator.divide(5, 0);
    }
}
