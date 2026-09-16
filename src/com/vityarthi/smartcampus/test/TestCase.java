package com.vityarthi.smartcampus.test;

/**
 * Lightweight test assertion framework.
 */
public final class TestCase {
    private TestCase() {}

    public static void assertTrue(String testName, boolean condition) {
        if (!condition) {
            throw new AssertionError("FAILED [" + testName + "]: Expected true but was false.");
        }
    }

    public static void assertFalse(String testName, boolean condition) {
        if (condition) {
            throw new AssertionError("FAILED [" + testName + "]: Expected false but was true.");
        }
    }

    public static void assertEquals(String testName, Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError("FAILED [" + testName + "]: Expected [" + expected + "] but found [" + actual + "]");
        }
    }

    public static void assertDoubleEquals(String testName, double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("FAILED [" + testName + "]: Expected [" + expected + "] but found [" + actual + "]");
        }
    }
}
