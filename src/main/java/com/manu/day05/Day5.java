package com.manu.day05;

import com.manu.Puzzle;

import java.util.*;
import java.util.stream.Collectors;

public class Day5 extends Puzzle {

  public Day5(String input) {
    super(input);
  }

  @Override
  public String part1() {
    Board board = getBoardFromInput();
    for (Operation op : getOperationsFromInput()) {
      for (int i = 0; i < op.move; i++) {
        board.push(op.to - 1, board.pop(op.from - 1));
      }
    }

    return board.topOfEachStack();
  }

  @Override
  public String part2() {
    Board board = getBoardFromInput();

    for (Operation op : getOperationsFromInput()) {

      Stack<Character> aux = new Stack<>();
      for (int i = 0; i < op.move; i++) {
        aux.add(board.pop(op.from - 1));
      }
      while (!aux.isEmpty()) {
        board.push(op.to - 1, aux.pop());
      }
    }
    return board.topOfEachStack();
  }

  private List<Operation> getOperationsFromInput() {
    return Arrays.stream(this.getInput().split("\n\n")[1].split("\n")).map(Operation::new).collect(Collectors.toList());
  }

  private Board getBoardFromInput() {
    String boardInput = this.getInput().split("\n\n")[0];
    Optional<Integer> maxLength = Arrays.stream(boardInput.split("\n")).map(String::length).max(Comparator.naturalOrder());
    int numberOfColumns = (int) Math.ceil(maxLength.orElse(0) / 4.0);

    Board board = new Board(numberOfColumns);
    for (String line : boardInput.split("\n")) {
      List<Character> chars = new ArrayList<>();
      int j = 0;
      for (int i = 0; i < line.length(); i++) {
        Character c = line.charAt(i);
        chars.add(c);
        if (chars.size() == 3) {
          if (chars.get(1) != ' ' && Character.isAlphabetic(chars.get(1))) {
            board.getColumns().get(j).add(0, chars.get(1));
          }
          chars = new ArrayList<>();
          i++;
          j++;
        }
      }
    }
    return board;
  }


  private class Board {
    private List<Stack<Character>> columns;
    private int numberOfColumns;

    public List<Stack<Character>> getColumns() {
      return columns;
    }

    public Character pop(int column) {
      return columns.get(column).pop();
    }

    public Character push(int column, char c) {
      return columns.get(column).push(c);
    }


    public Board(int numberOfColumns) {
      this.numberOfColumns = numberOfColumns;
      this.columns = new ArrayList<>();
      for (int i = 0; i < numberOfColumns; i++) {
        this.columns.add(new Stack<>());
      }
    }

    public String topOfEachStack() {
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 0; i < numberOfColumns; i++) {
        stringBuilder.append(this.pop(i));
      }
      return stringBuilder.toString();
    }
  }

  private class Operation {
    public int move;
    public int from;
    public int to;

    public Operation(String s) {
      s = s.replace("move ", "");
      s = s.replace(" from ", ",");
      s = s.replace(" to ", ",");
      String[] split = s.split(",");
      this.move = Integer.parseInt(split[0]);
      this.from = Integer.parseInt(split[1]);
      this.to = Integer.parseInt(split[2]);
    }

  }

}
