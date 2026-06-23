package com.inventory.util;

import java.math.BigDecimal;
import java.util.Scanner;

public final class InputUtil {

    private static final Scanner SCANNER = new Scanner(System.in);

    private InputUtil() {
    }

    public static int readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public static String readString(String message) {
        System.out.print(message);
        return SCANNER.nextLine().trim();
    }

    public static BigDecimal readBigDecimal(String message) {
        while (true) {
            System.out.print(message);
            String input = SCANNER.nextLine().trim();
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid amount. Please try again.");
            }
        }
    }
}

