package com.manu.day02;

import com.manu.Puzzle;

public class Day2 extends Puzzle {
  public Day2(String input) {
    super(input);
  }

  @Override
  public String part1() {
    int points = 0;
    for (String s : getInputLines()) {
      Play opponent = getPlay(s.charAt(0));
      Play mine = getPlay(s.charAt(2));
      Outcome outcome = getOutcome(opponent, mine);
      points += mine.getPoints() + outcome.getPoints();
    }
    return String.valueOf(points);
  }

  @Override
  public String part2() {
    int points = 0;
    for (String s : getInputLines()) {
      Play opponent = getPlay(s.charAt(0));
      Outcome expectedOutcome = getOutcome(s.charAt(2));
      Play mine = getPlay(opponent, expectedOutcome);
      points += mine.getPoints() + expectedOutcome.getPoints();
    }
    return String.valueOf(points);
  }

  private Play getPlay(Play enemy, Outcome expectedOutcome) {
    if (enemy == Play.ROCK && expectedOutcome == Outcome.WIN || enemy == Play.PAPER && expectedOutcome == Outcome.DRAW || enemy == Play.SCISSOR && expectedOutcome == Outcome.LOSE) {
      return Play.PAPER;
    }

    if (enemy == Play.ROCK && expectedOutcome == Outcome.DRAW || enemy == Play.PAPER && expectedOutcome == Outcome.LOSE || enemy == Play.SCISSOR && expectedOutcome == Outcome.WIN) {
      return Play.ROCK;
    }
    return Play.SCISSOR;

  }

  public Play getPlay(char c) {
    if (c == 'A' || c == 'X') {
      return Play.ROCK;
    }
    if (c == 'B' || c == 'Y') {
      return Play.PAPER;
    }
    if (c == 'C' || c == 'Z') {
      return Play.SCISSOR;
    }
    return null;
  }

  public Outcome getOutcome(Play a, Play b) {
    if (a == b) return Outcome.DRAW;
    if (a == Play.ROCK && b == Play.PAPER || a == Play.PAPER && b == Play.SCISSOR || a == Play.SCISSOR && b == Play.ROCK) {
      return Outcome.WIN;
    } else return Outcome.LOSE;
  }

  public Outcome getOutcome(char c) {
    if (c == 'X') return Outcome.LOSE;
    if (c == 'Y') return Outcome.DRAW;
    return Outcome.WIN;
  }


  public enum Play {
    ROCK, PAPER, SCISSOR;

    public int getPoints() {
      if (this == ROCK) return 1;
      if (this == PAPER) return 2;
      if (this == SCISSOR) return 3;
      return 0;
    }
  }

  public enum Outcome {
    WIN, LOSE, DRAW;

    public int getPoints() {
      if (this == WIN) return 6;
      if (this == DRAW) return 3;
      return 0;
    }

  }

}
