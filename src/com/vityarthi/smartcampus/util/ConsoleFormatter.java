package com.vityarthi.smartcampus.util;

/**
 * Utility for producing clean terminal styling, box drawing, and headers.
 */
public final class ConsoleFormatter {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";

    private ConsoleFormatter() {}

    public static void printHeader(String title) {
        int width = 72;
        String line = "=".repeat(width);
        System.out.println("\n" + line);
        int padding = (width - title.length() - 2) / 2;
        String padStr = " ".repeat(Math.max(0, padding));
        System.out.println(" " + padStr + title + padStr);
        System.out.println(line);
    }

    public static void printSubHeader(String subtitle) {
        System.out.println("\n--- " + subtitle + " ---");
    }

    public static void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    public static void printWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

    public static void printError(String message) {
        System.out.println("[ERROR]   " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO]    " + message);
    }
}
