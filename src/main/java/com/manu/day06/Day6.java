package com.manu.day06;

import com.manu.Puzzle;

import java.util.HashSet;
import java.util.Set;

public class Day6 extends Puzzle {

  public Day6(String input) {
    super(input);
  }


  @Override
  public String part1() {
    return firstCharWithoutRepeat(4);
  }

  @Override
  public String part2() {
    return firstCharWithoutRepeat(14);
  }

  private String firstCharWithoutRepeat(int digits) {
    for (String s : getInputLines()) {
      for (int i = 0; i < s.length() - digits; i++) {
        if (!doesItRepeatAChar(s.substring(i, i + digits))) {
          return String.valueOf(i + digits);
        }
      }
    }
    return null;
  }

  private boolean doesItRepeatAChar(String s) {
//    return s.chars().distinct().count() != s.length();
    Set<Character> characterSet = new HashSet<>();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (characterSet.contains(c)) {
        return true;
      }
      characterSet.add(c);
    }
    return false;
  }
}
