package com.manu;

public abstract class Puzzle {
  protected final String input;

  public Puzzle(String input) {
    this.input = input;
  }

  public abstract String part1();

  public abstract String part2();

  public void printSolutions() {
    long timeBefore = System.currentTimeMillis();
    String solution1 = part1();
    long timeMiddle = System.currentTimeMillis();
    System.out.println("First part solution: (time taken: " + (timeMiddle - timeBefore) + "ms) --> " + solution1);
    String solution2 = part2();
    long timeAfter = System.currentTimeMillis();
    System.out.println("Second part solution: (time taken: " + (timeAfter - timeMiddle) + "ms) --> " + solution2);
  }

  public String[] getInputLines() {
    return input.split("\\r?\n");
  }

  public String getInput() {
    return this.input;
  }

}
