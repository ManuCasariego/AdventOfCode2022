package com.manu.day04;

import com.manu.Puzzle;


public class Day4 extends Puzzle {
  public Day4(String input) {
    super(input);
  }

  @Override
  public String part1() {
    int count = 0;
    for (String s : getInputLines()) {
      Pair<Range> rangePair = getRangePair(s);
      Range first = rangePair.first();
      Range second = rangePair.second();
      if (first.overlapFullyContaining(second) || second.overlapFullyContaining(first))
        count++;

    }

    return String.valueOf(count);
  }

  @Override
  public String part2() {
    int count = 0;
    for (String s : getInputLines()) {
      Pair<Range> rangePair = getRangePair(s);
      Range first = rangePair.first();
      Range second = rangePair.second();
      if (first.overlap(second) || second.overlap(first))
        count++;

    }

    return String.valueOf(count);
  }

  private Pair<Range> getRangePair(String s) {
    String[] split = s.split(",");
    Range firstRange = new Range(split[0].split("-")[0], split[0].split("-")[1]);
    Range secondRange = new Range(split[1].split("-")[0], split[1].split("-")[1]);
    return new Pair<>(firstRange, secondRange);
  }

  private record Range(int first, int second) {

    public Range(String first, String second) {
      this(Integer.parseInt(first), Integer.parseInt(second));
    }

    public boolean overlap(Range range) {
      return this.first <= range.first && this.second >= range.first || this.first <= range.second && this.second >= range.second;
    }

    public boolean overlapFullyContaining(Range range) {
      return this.first <= range.first && this.second >= range.second;
    }

  }

  private record Pair<T>(T first, T second) {
  }

}
