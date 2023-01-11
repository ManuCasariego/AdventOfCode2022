package com.manu.day24;


import com.manu.Puzzle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Day24 extends Puzzle {

  public Day24(String input) {
    super(input);
  }


  @Override
  public String part1() {
    boolean[][] board = buildBoard();
    List<Wind> windList = buildWindList();
    Position player = getStartingPosition(board);
    Position endGoal = getEndGoal(board);

    int steps = numberOfStepsToReachTheEnd(player, board, windList, endGoal, 1);
    return String.valueOf(steps);
  }

  @Override
  public String part2() {
    boolean[][] board = buildBoard();
    List<Wind> windList = buildWindList();
    Position player = getStartingPosition(board);
    Position endGoal = getEndGoal(board);

    int steps = numberOfStepsToReachTheEnd(player, board, windList, endGoal, 3);
    return String.valueOf(steps);
  }

  private int numberOfStepsToReachTheEnd(Position startingPosition, boolean[][] board, List<Wind> windList, Position endGoal, int numberOfTrips) {

    Queue<TimePosition> positionQueue = new ArrayDeque<>();
    TimePosition currentPosition = new TimePosition(startingPosition.x, startingPosition.y, 0);
    positionQueue.add(currentPosition);
    int currentTime = -1;

    while (!positionQueue.isEmpty()) {
      currentPosition = positionQueue.poll();
      if (currentPosition.x == endGoal.x && currentPosition.y == endGoal.y) {
        // we found it
        break;
      }
      // check if time has changed
      if (currentTime != currentPosition.time) {
        // clear the board from previous windlist
        windList.forEach(wind -> board[wind.x][wind.y] = true);
        // wind list goes brr
        windList.forEach(wind -> wind.move(board));
        // update board with new wind positions
        windList.forEach(wind -> board[wind.x][wind.y] = false);

        // update current time
        currentTime = currentPosition.time;
      }

      // add places we could move to
      TimePosition up = currentPosition.goUp();
      TimePosition down = currentPosition.goDown();
      TimePosition right = currentPosition.goRight();
      TimePosition left = currentPosition.goLeft();
      TimePosition standStill = currentPosition.standStill();

      for (TimePosition possibleNextStep : List.of(up, down, left, right, standStill)) {
        if (possibleNextStep.x >= 0 && possibleNextStep.y >= 0 && possibleNextStep.x < board.length && possibleNextStep.y < board[possibleNextStep.x].length
          && board[possibleNextStep.x][possibleNextStep.y] && !positionQueue.contains(possibleNextStep)) {
          positionQueue.add(possibleNextStep);
        }
      }
    }
    if (numberOfTrips > 1) {
      // not sure why I need to add the + 1 per added trip
      return currentTime + 1 + numberOfStepsToReachTheEnd(endGoal, board, windList, startingPosition, numberOfTrips - 1);
    }
    return currentTime;
  }

  private Position getStartingPosition(boolean[][] board) {
    for (int i = 0; i < board.length; i++) {
      if (board[i][0]) {
        return new Position(i, 0);
      }
    }
    throw new RuntimeException("there's no starting position");
  }

  private Position getEndGoal(boolean[][] board) {
    for (int i = 0; i < board.length; i++) {
      if (board[i][board[0].length - 1]) {
        return new Position(i, board[0].length - 1);
      }
    }
    throw new RuntimeException("there's no end goal");
  }

  // builds a "can you walk?" board -> true for walkable tiles, false for walls
  private boolean[][] buildBoard() {
    String[] lines = getInputLines();
    int noOfColumns = lines[0].length();
    int noOfRows = lines.length;
    boolean[][] board = new boolean[noOfColumns][noOfRows];

    for (int j = 0; j < noOfRows; j++) {
      for (int i = 0; i < noOfColumns; i++) {
        if (lines[j].charAt(i) != '#') board[i][j] = true;
      }
    }
    return board;
  }

  private List<Wind> buildWindList() {
    List<Wind> windList = new ArrayList<>();
    String[] lines = getInputLines();
    int noOfRows = lines.length;
    int noOfColumns = lines[0].length();

    for (int y = 0; y < noOfRows; y++) {
      for (int x = 0; x < noOfColumns; x++) {
        if (lines[y].charAt(x) != '.' && lines[y].charAt(x) != '#') windList.add(new Wind(x, y, lines[y].charAt(x)));
      }
    }
    return windList;
  }

  private class Wind {
    int x;
    int y;
    char direction;

    public Wind(int x, int y, char direction) {
      this.x = x;
      this.y = y;
      this.direction = direction;
    }

    public void move(boolean[][] board) {

      switch (direction) {
        case '<': {
          // only changing x
          if (!board[x - 1][y]) {
            x = board.length - 2;
          } else {
            x -= 1;
          }
          break;
        }
        case '>': {
          // only changing x
          if (!board[x + 1][y]) {
            x = 1;
          } else {
            x += 1;
          }
          break;

        }
        case 'v': {
          // only changing y
          if (!board[x][y + 1]) {
            y = 1;
          } else {
            y += 1;
          }
          break;

        }
        case '^': {
          // only changing y
          if (!board[x][y - 1]) {
            y = board[0].length - 2;
          } else {
            y -= 1;
          }
          break;
        }
      }
    }
  }

  private record Position(int x, int y) {

  }

  private record TimePosition(int x, int y, int time) {

    private TimePosition goUp() {
      return new TimePosition(x, y - 1, time + 1);
    }

    private TimePosition goDown() {
      return new TimePosition(x, y + 1, time + 1);
    }

    private TimePosition goLeft() {
      return new TimePosition(x - 1, y, time + 1);
    }

    private TimePosition goRight() {
      return new TimePosition(x + 1, y, time + 1);
    }

    public TimePosition standStill() {
      return new TimePosition(x, y, time + 1);
    }
  }

}
