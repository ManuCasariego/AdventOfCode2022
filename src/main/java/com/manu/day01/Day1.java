package com.manu.day01;

import com.manu.Puzzle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day1 extends Puzzle {

  public Day1(String input) {
    super(input);
  }

  @Override
  public String part1() {
    return getSumOfNFirstElfs(1);
  }

  @Override
  public String part2() {
    return getSumOfNFirstElfs(3);
  }

  private String getSumOfNFirstElfs(int n) {
    List<Integer> caloriesPerElf = new ArrayList<>();
    int elfCalories = 0;

    for (String s : getInputLines()) {
      if (s.isEmpty()) {
        caloriesPerElf.add(elfCalories);
        elfCalories = 0;
      } else {
        elfCalories += Integer.parseInt(s);
      }
    }
    caloriesPerElf.add(elfCalories);
    return String.valueOf(caloriesPerElf.stream().sorted(Comparator.reverseOrder()).limit(n).mapToInt(Integer::intValue).sum());
  }

}
