package com.manu.day13;

import com.manu.Puzzle;

import java.util.ArrayList;
import java.util.List;

public class Day13 extends Puzzle {

  public Day13(String input) {
    super(input);
  }


  @Override
  public String part1() {
    Paper paper = buildPaper();
    // only first fold instruction
    List<Instruction> instructions = getInstructions().subList(0, 1);
    Paper outputPaper = executeInstructionsOnPaper(instructions, paper);
    int howManyDotsOnPaper = outputPaper.howManyDotsOnPaper();
    return String.valueOf(howManyDotsOnPaper);
  }

  @Override
  public String part2() {
    Paper paper = buildPaper();
    List<Instruction> instructions = getInstructions();
    Paper outputPaper = executeInstructionsOnPaper(instructions, paper);
    String paperVisualization = outputPaper.visualize();
    return paperVisualization;
  }


  private Paper executeInstructionsOnPaper(List<Instruction> instructions, Paper paper) {
    Paper auxPaper = paper;
    for (Instruction instruction : instructions) {
      auxPaper = executeInstructionOnPaper(instruction, auxPaper);
    }
    return auxPaper;
  }

  private Paper executeInstructionOnPaper(Instruction instruction, Paper paper) {
    Paper outputPaper = null;
    // todo
    if (instruction.foldAlong.equals("x")) {
      // x
      outputPaper = new Paper(paper.columns / 2, paper.rows);
      for (int i = 0; i < outputPaper.columns; i++) {
        for (int j = 0; j < outputPaper.rows; j++) {
          int foldedI = Math.abs(i - paper.columns + 1);
          outputPaper.board[i][j] = paper.board[i][j] || paper.board[foldedI][j];
        }
      }
    } else {
      // y
      outputPaper = new Paper(paper.columns, paper.rows / 2);
      for (int i = 0; i < outputPaper.columns; i++) {
        for (int j = 0; j < outputPaper.rows; j++) {
          int foldedJ = Math.abs(j - paper.rows + 1);
          outputPaper.board[i][j] = paper.board[i][j] || paper.board[i][foldedJ];
        }
      }
    }

    return outputPaper;
  }


  private List<Instruction> getInstructions() {
    List<Instruction> instructions = new ArrayList<>();
    for (String s : getInput().split("\n\n")[1].split("\n")) {
      instructions.add(Instruction.parse(s));
    }
    return instructions;
  }

  private Paper buildPaper() {
    List<Position> positions = getPositions();
    // get number of rows and number of columns for the paper
    int numberOfRows = 0;
    int numberOfColumns = 0;
    for (Position position : positions) {
      if (position.x() > numberOfColumns) numberOfColumns = position.x();
      if (position.y() > numberOfRows) numberOfRows = position.y();
    }
    Paper paper = new Paper(numberOfColumns + 1, numberOfRows + 1);
    // populate the paper
    for (Position position : positions) {
      paper.board[position.x][position.y] = true;
    }

    return paper;
  }

  private List<Position> getPositions() {
    List<Position> positions = new ArrayList<>();
    for (String s : getInput().split("\n\n")[0].split("\n")) {
      positions.add(Position.parse(s));
    }
    return positions;
  }


  private record Position(int x, int y) {

    public static Position parse(String s) {
      String[] s1 = s.split(",");
      return new Position(Integer.parseInt(s1[0]), Integer.parseInt(s1[1]));
    }
  }

  private record Instruction(String foldAlong, int units) {
    public static Instruction parse(String s) {
      String[] s1 = s.split(" ")[2].split("=");
      return new Instruction(s1[0], Integer.parseInt(s1[1]));

    }
  }

  private class Paper {
    int rows;
    int columns;
    boolean[][] board;

    public Paper(int columns, int rows) {
      this.columns = columns;
      this.rows = rows;
      this.board = new boolean[columns][rows];
    }

    public int howManyDotsOnPaper() {
      // return how many trues we have on board
      int count = 0;
      for (int i = 0; i < this.columns; i++) {
        for (int j = 0; j < this.rows; j++) {
          if (this.board[i][j]) count++;
        }
      }
      return count;
    }

    public String visualize() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("\n");
      for (int j = 0; j < this.rows; j++) {
        for (int i = 0; i < this.columns; i++) {
          if (this.board[i][j]) stringBuffer.append("███");
          else stringBuffer.append("   ");
        }
        stringBuffer.append("\n");
      }
      return stringBuffer.toString();
    }
  }
}
