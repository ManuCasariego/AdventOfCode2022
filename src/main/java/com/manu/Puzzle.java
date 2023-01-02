package com.manu;

public abstract class Puzzle {
  protected final String input;

  public Puzzle(String input) {
    this.input = input;
  }

  public abstract String part1();

  public abstract String part2();

  public void printSolutions() {
    System.out.println("First part solution: " + part1());
    System.out.println("Second part solution: " + part2());
  }

  public String[] getInputLines() {
    return input.split("\n");
  }

  public String getInput() {
    return this.input;
  }

}
