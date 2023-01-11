package com.manu.day22;


import com.manu.Puzzle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day22 extends Puzzle {

  public Day22(String input) {
    super(input);
  }


  @Override
  public String part1() {
    Board b = buildBoard();

    String stringInstructions = getInput().split("\n\n")[1];
    List<Integer> steps = new ArrayList<>();
    Pattern p1 = Pattern.compile("\\d+");
    Matcher m1 = p1.matcher(stringInstructions);
    while (m1.find()) {
      steps.add(Integer.parseInt(m1.group()));
    }
//    List<String> directions = new ArrayList<>();
//    Pattern p2 = Pattern.compile("R");
//    Matcher m2 = p2.matcher(stringInstructions);
//    while (m2.find()) {
//      directions.add(m1.group());
//    }
    char[] directions = stringInstructions.replaceAll("\\d", "").toCharArray();
//    b.draw();


    for (int i = 0; i < directions.length; i++) {
      // move
      b.move(steps.get(i));
//      b.draw();
      //direction
      b.changeDirection(directions[i]);
    }
    // move
    b.move(steps.get(steps.size() - 1));
//    b.draw();



    return b.getFinalPassword();
  }

  private Board buildBoard() {

    String stringBoard = getInput().split("\n\n")[0];
    String[] lines = stringBoard.split("\n");
    int rows = lines.length;
    int columns = stringBoard.lines().map(String::length).max(Comparator.naturalOrder()).orElse(0);
    Tile[][] board = new Tile[rows][columns];

    // let's iterate our input and populate the tiles in board
    for (int j = 0; j < rows; j++) {
      for (int i = 0; i < columns; i++) {
        if (lines[j].length() > i) {
          if (lines[j].charAt(i) == '.') board[j][i] = Tile.WALKABLE_TILE;
          else if (lines[j].charAt(i) == ' ') board[j][i] = Tile.NOTHING;
          else board[j][i] = Tile.WALL;
        } else {
          board[j][i] = Tile.NOTHING;
        }
      }
    }
    return new Board(board);
  }

  @Override
  public String part2() {
    // now for part 2 you have a cube, 50x50x50
    // if you leave one side of the cube, you are going to change you direction (or not), based on whichever new side of
    // the cube you land


    // input is 200 x 150


    // whenever you go to a new face left or right, even going entirely out from the left
    return null;
  }

  private class Board {
    Tile[][] board;
    int x;
    int y;

    final int rows;
    final int columns;
    // 0: facing right >
    // 1: facing down v
    // 2: facing left <
    // 3: facing up ^
    int direction;

    public Board(Tile[][] board) {
      this.rows = board.length;
      this.columns = board[0].length;
      this.board = board;
      this.x = getStartingPosition().x();
      this.y = getStartingPosition().y();
    }

    public void draw() {
      StringBuffer sb = new StringBuffer();
      for (int j = 0; j < rows; j++) {
        for (int i = 0; i < columns; i++) {
          if (j == x && i == y) {
            sb.append("*");
          } else if (this.board[j][i] == Tile.WALKABLE_TILE) sb.append(".");
          else if (this.board[j][i] == Tile.WALL) sb.append("#");
          else if (this.board[j][i] == Tile.NOTHING) sb.append(" ");


        }
        sb.append("\n");
      }
      System.out.println(sb);
    }

    private Position getStartingPosition() {
      // first row, first column available from the left
      int y = 0;
      for (int i = 0; i < this.board[y].length; i++) {
        if (this.board[y][i] == Tile.WALKABLE_TILE) return new Position(i, y);
      }
      throw new RuntimeException("no walkable tile in first row");
    }

    private Position getStartingPositionForRow(int row, boolean startLeft) {
      int y = row;
      if (startLeft) {

        for (int i = 0; i < this.board[y].length; i++) {
          if (this.board[y][i] != Tile.NOTHING) return new Position(i, y);
        }
      } else {
        //start on the right
        for (int i = this.board[y].length - 1; i >= 0; i--) {
          if (this.board[y][i] != Tile.NOTHING) return new Position(i, y);
        }
      }
      throw new RuntimeException("getStartingPositionForRow");
    }

    private Position getStartingPositionForColumn(int column, boolean startTop) {
      int x = column;
      if (startTop) {

        for (int j = 0; j < this.board.length; j++) {
          if (this.board[j][x] != Tile.NOTHING) return new Position(x, j);
        }
      } else {
        //start on the right
        for (int j = this.board.length - 1; j >= 0; j--) {
          if (this.board[j][x] != Tile.NOTHING) return new Position(x, j);
        }
      }
      throw new RuntimeException("getStartingPositionForColumn");
    }

    private void move(int steps) {
      // todo
      for (int i = 0; i < steps; i++) {
        // we hit a wall
        if (!move()) return;
      }
      return;
    }

    private Boolean move() {
      // todo
      // Special cases:
      // if the next tile is a wall, return false
      // if the next tile is a walkable tile just walk and return true
      // if the next tile is out of the board, then calculate the next position if we would tp to it, if it's a wall then
      // it doesn't go that way and returns false

      // check the direction
      Position movePosition;
      switch (direction) {
        case 0 -> movePosition = new Position(1, 0);
        case 1 -> movePosition = new Position(0, 1);
        case 2 -> movePosition = new Position(-1, 0);
        case 3 -> movePosition = new Position(0, -1);
        default -> throw new RuntimeException("error");
      }
      Position newPosition = new Position(this.x, this.y).move(movePosition);
      if (newPosition.x < 0 || newPosition.y < 0 || newPosition.x >= columns || newPosition.y >= rows || this.board[newPosition.y][newPosition.x] == Tile.NOTHING) {
        // we go outside the board
        if (direction == 0) {
          newPosition = getStartingPositionForRow(newPosition.y, true);
        } else if (direction == 1) {
          newPosition = getStartingPositionForColumn(newPosition.x, true);
        } else if (direction == 2) {
          newPosition = getStartingPositionForRow(newPosition.y, false);
        } else if (direction == 3) {
          newPosition = getStartingPositionForColumn(newPosition.x, false);
        }
        if (this.board[newPosition.y][newPosition.x] == Tile.WALL) {
          return false;
        } else {
          x = newPosition.x();
          y = newPosition.y();
          return true;
        }

      } else {
        // inside the board
        if (this.board[newPosition.y][newPosition.x] == Tile.WALKABLE_TILE) {
          x = newPosition.x();
          y = newPosition.y();
          return true;
        } else return this.board[newPosition.y][newPosition.x] != Tile.WALL;
      }
    }

    private void changeDirection(Character c) {
      this.direction = Math.floorMod(c.equals('R') ? (direction + 1) : (direction - 1), 4);
    }

    public String getFinalPassword() {

      //The final password is the sum of 1000 times the row, 4 times the column, and the facing.
      return String.valueOf(1000 * (y + 1) + 4 * (x + 1) + direction);
    }
  }

  private enum Tile {NOTHING, WALKABLE_TILE, WALL}

  private record Position(int x, int y) {
    private Position move(Position p) {
      return new Position(this.x + p.x, this.y + p.y);
    }
  }

}
