package com.manu.day08;

import com.manu.Puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class Day8 extends Puzzle {

  public Day8(String input) {
    super(input);
  }


  @Override
  public String part1() {
    int count = 0;
    Board board = createBoard();
    for (int i = 0; i < board.data.length; i++) {
      for (int j = 0; j < board.data[i].length; j++) {
        if (isTreeVisibleFromOutside(i, j, board)) {
          count++;
        }
      }
    }
    return String.valueOf(count);
  }

  @Override
  public String part2() {
    Board board = createBoard();
    List<Integer> scenicScores = new ArrayList<>();
    for (int i = 0; i < board.data.length; i++) {
      for (int j = 0; j < board.data[i].length; j++) {
        scenicScores.add(scenicScore(i, j, board));
      }
    }
    return scenicScores.stream().max(Integer::compareTo).map(String::valueOf).orElse(null);
  }


  private int scenicScore(int i, int j, Board board) {
    List<Integer> trees = new ArrayList<>();
    int height = board.data[i][j];
    for (int left = i - 1; left >= 0; left--) {
      trees.add(board.data[left][j]);
    }
    int a = scenicScore(height, trees);
    trees = new ArrayList<>();
    for (int right = i + 1; right < board.columns; right++) {
      trees.add(board.data[right][j]);
    }
    int b = scenicScore(height, trees);
    trees = new ArrayList<>();
    for (int top = j - 1; top >= 0; top--) {
      trees.add(board.data[i][top]);
    }
    int c = scenicScore(height, trees);
    trees = new ArrayList<>();
    for (int bottom = j + 1; bottom < board.rows; bottom++) {
      trees.add(board.data[i][bottom]);
    }
    int d = scenicScore(height, trees);

    return scenicScore(a, b, c, d);
  }

  private int scenicScore(int height, List<Integer> trees) {
    if (trees.isEmpty()) return 0;
    int count = 0;
    for (Integer tree : trees) {
      count ++;
      if (height <= tree) {
        return count;
      }
    }
    return count;
  }

  private int scenicScore(int a, int b, int c, int d) {
    return a * b * c * d;
  }

  private boolean isTreeVisibleFromOutside(int i, int j, Board board) {
    int height = board.data[i][j];
    IntPredicate biggerEqualThanZero = value -> value >= 0;
    boolean visibleFromLeft = IntStream.range(0, i).map(value -> board.data[value][j] - height).noneMatch(biggerEqualThanZero);
    boolean visibleFromRight = IntStream.range(i + 1, board.columns).map(value -> board.data[value][j] - height).noneMatch(biggerEqualThanZero);
    boolean visibleFromTop = IntStream.range(0, j).map(value -> board.data[i][value] - height).noneMatch(biggerEqualThanZero);
    boolean visibleFromBottom = IntStream.range(j + 1, board.rows).map(value -> board.data[i][value] - height).noneMatch(biggerEqualThanZero);

    return visibleFromLeft || visibleFromBottom || visibleFromRight || visibleFromTop;
  }

  private Board createBoard() {
    String[] inputLines = getInputLines();
    int i = inputLines.length, j = inputLines[0].length();
    int[][] data = new int[i][j];
    for (i = 0; i < inputLines.length; i++) {
      for (j = 0; j < inputLines[i].length(); j++) {
        data[i][j] = Character.getNumericValue(inputLines[i].charAt(j));
      }
    }
    return new Board(data);
  }

  private class Board {
    private int[][] data;
    private int columns, rows;

    public Board(int[][] data) {
      this.data = data;
      this.columns = data.length;
      this.rows = data[0].length;
    }
  }
}
