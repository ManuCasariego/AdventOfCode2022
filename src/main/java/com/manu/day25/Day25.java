package com.manu.day25;


import com.manu.Puzzle;

import java.util.ArrayList;
import java.util.List;

public class Day25 extends Puzzle {

  public Day25(String input) {
    super(input);
  }


  @Override
  public String part1() {
    List<SnafuNumber> snafuNumbers = buildSnafuNumbersList();

    for (SnafuNumber snafuNumber : snafuNumbers) {
      System.out.println(snafuNumber.getDecimalValue());
    }
    long sum = snafuNumbers.stream().map(SnafuNumber::getDecimalValue).reduce(0L, Long::sum);
    return SnafuNumber.getSnafuNumber(sum);
  }

  @Override
  public String part2() {
    return null;
  }

  private List<SnafuNumber> buildSnafuNumbersList() {
    return getInput().lines().map(SnafuNumber::new).toList();
  }

  private record SnafuNumber(String s) {
    private static final long POWER = 5;
    private static final long MINUS = -1;
    private static final long DOUBLE_MINUS = -2;

    private long getDecimalValue() {
      long count = 0;
      for (int i = 0; i < s.length(); i++) {
        long position = s.length() - 1 - i;
        char c = s.charAt(i);
        switch (c) {
          case '2' -> count += 2 * Math.pow(POWER, position);
          case '1' -> count += 1 * Math.pow(POWER, position);
          case '-' -> count += MINUS * Math.pow(POWER, position);
          case '=' -> count += DOUBLE_MINUS * Math.pow(POWER, position);
        }
      }
      return count;
    }

    public static String getSnafuNumber(long decimal) {
      // how to reverse do this

      // find the number of digits
      int numberOfDigits = 0;
      while (decimal > Math.pow(POWER, numberOfDigits)) {
        numberOfDigits++;
      }
      int[] digits = new int[numberOfDigits];
      for (int i = 0; i < numberOfDigits; i++) {
        long position = numberOfDigits - 1 - i;
        int digit = (int) Math.floorDiv(decimal, (long) Math.pow(POWER, position));
        // add the digit
        digits[i] = digit;
        decimal = Math.floorMod(decimal, (long) Math.pow(POWER, position));
      }

      ArrayList<Character> finalList = new ArrayList<>();
      long carryOn = 0;
      for (int i = digits.length - 1; i >= 0; i--) {
        if (digits[i] + carryOn == 3) {
          carryOn = 1;
          finalList.add('=');
        } else if (digits[i] + carryOn == 4) {
          finalList.add('-');
          carryOn = 1;
        } else if (digits[i] + carryOn == 5) {
          finalList.add('0');
          carryOn = 1;
        } else if (digits[i] + carryOn == 2) {
          finalList.add('2');
          carryOn = 0;
        } else if (digits[i] + carryOn == 1) {
          finalList.add('1');
          carryOn = 0;
        } else if (digits[i] + carryOn == 0) {
          finalList.add('0');
          carryOn = 0;
        }
      }
      if (carryOn > 0) {
        finalList.add('1');
      }
      StringBuilder sb = new StringBuilder();
      for (int i = finalList.size() - 1; i >= 0; i--) {
        sb.append(finalList.get(i));
      }
      return sb.toString();
    }
  }
}
