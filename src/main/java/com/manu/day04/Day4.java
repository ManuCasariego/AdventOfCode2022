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
      Range first = rangePair.getFirst();
      Range second = rangePair.getSecond();
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
      Range first = rangePair.getFirst();
      Range second = rangePair.getSecond();
      if (first.overlap(second) || second.overlap(first))
        count++;

    }

    return String.valueOf(count);
  }

  private Pair<Range> getRangePair(String s) {
    String[] split = s.split(",");
    Range firstRange = new Range().setFirst(split[0].split("-")[0]).setSecond(split[0].split("-")[1]);
    Range secondRange = new Range().setFirst(split[1].split("-")[0]).setSecond(split[1].split("-")[1]);
    return new Pair<>(firstRange, secondRange);
  }

  private class Range {
    public int first;
    public int second;

    public Range setFirst(int first) {
      this.first = first;
      return this;
    }

    public Range setFirst(String first) {
      return setFirst(Integer.parseInt(first));
    }

    public Range setSecond(int second) {
      this.second = second;
      return this;
    }

    public Range setSecond(String second) {
      return setSecond(Integer.parseInt(second));
    }

    public boolean contains(Range range) {
      return this.first <= range.first && this.second >= range.second;
    }

    public boolean overlap(Range range) {
      return this.first <= range.first && this.second >= range.first || this.first <= range.second && this.second >= range.second;
    }

    public boolean overlapFullyContaining(Range range) {
      return this.first <= range.first && this.second >= range.second;
    }

  }

  private class Pair<T> {
    private T first;
    private T second;

    public Pair(T first, T second) {
      setFirst(first);
      setSecond(second);
    }

    public T getFirst() {
      return this.first;
    }

    public T getSecond() {
      return this.second;
    }

    public void setFirst(T first) {
      this.first = first;
    }

    public void setSecond(T second) {
      this.second = second;
    }
  }

}
