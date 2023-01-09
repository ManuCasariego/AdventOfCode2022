package com.manu.day03;

import com.manu.Puzzle;

import java.util.ArrayList;
import java.util.List;

public class Day3 extends Puzzle {
  public Day3(String input) {
    super(input);
  }

  @Override
  public String part1() {
    Integer count = 0;
    for (String s : getInputLines()) {
      List<String> stringInHalf = splitStringInHalf(s);
      Character repeatedChar = getRepeatedChar(stringInHalf);
      count += getPriority(repeatedChar);
    }
    return String.valueOf(count);
  }

  @Override
  public String part2() {
    Integer count = 0;
    List<String> elfGroup = new ArrayList<>();
    for (String s : getInputLines()) {
      elfGroup.add(s);
      if (elfGroup.size() == 3) {
        Character c = getRepeatedChar(elfGroup);
        count += getPriority(c);
        elfGroup = new ArrayList<>();
      }
    }
    return String.valueOf(count);
  }

  private List<String> splitStringInHalf(String s) {
    String firstHalf = s.substring(0, s.length() / 2);
    String secondHalf = s.substring(s.length() / 2);
    return List.of(firstHalf, secondHalf);
  }

  private String sortString(String s) {
    return s.chars()
      .sorted()
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();
  }

  private Character getRepeatedChar(List<String> list) {
    if (list.size() == 0) return null;
    int n = list.size();
    List<String> sortedStrings = new ArrayList<>();

    for (String s : list) {
      sortedStrings.add(sortString(s));
    }

    int[] pointers = new int[n];

    while (!allTheSameChar(pointers, sortedStrings) && everyPointerInBound(pointers, sortedStrings)) {
      pointers[getIndexOfMinChar(pointers, sortedStrings)]++;
    }

    return sortedStrings.get(0).charAt(pointers[0]);
  }

  private int getIndexOfMinChar(int[] pointers, List<String> sortedStrings) {
    int indexOfMinChar = 0;
    char minChar = sortedStrings.get(0).charAt(pointers[0]);
    for (int i = 0; i < pointers.length; i++) {
      if (sortedStrings.get(i).charAt(pointers[i]) < minChar) {
        minChar = sortedStrings.get(i).charAt(pointers[i]);
        indexOfMinChar = i;
      }
    }

    return indexOfMinChar;
  }

  private boolean everyPointerInBound(int[] pointers, List<String> sortedStrings) {
    for (int i = 0; i < pointers.length; i++) {
      if (pointers[i] >= sortedStrings.get(i).length()) {
        return false;
      }
    }
    return true;
  }

  private boolean allTheSameChar(int[] pointers, List<String> sortedStrings) {
    char c = sortedStrings.get(0).charAt(pointers[0]);
    for (int i = 0; i < pointers.length; i++) {
      if (c != sortedStrings.get(i).charAt(pointers[i])) {
        return false;
      }
    }
    return true;
  }

  public int getPriority(Character c) {
    if (Character.isUpperCase(c)) {
      return c - 38;
    } else {
      return c - 96;
    }
  }
}
