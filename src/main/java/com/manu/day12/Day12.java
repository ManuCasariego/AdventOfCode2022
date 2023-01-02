package com.manu.day12;

import com.manu.Puzzle;

import java.util.*;

public class Day12 extends Puzzle {

  public Day12(String input) {
    super(input);
  }


  @Override
  public String part1() {
    Board board = createBoard();
    return findShortestPathFromStartingPointToEnd(board);
  }


  @Override
  public String part2() {
    Board board = reverseBoard(createBoard());
    return lowestElevationToAny26(board);
  }


  private Board reverseBoard(Board board) {
    int numberOfRows = board.board.length;
    int numberOfColumns = board.board()[0].length;

    Height[][] heightMatrix = new Height[numberOfRows][numberOfColumns];
    for (int i = 0; i < numberOfRows; i++) {
      for (int j = 0; j < numberOfColumns; j++) {
        heightMatrix[i][j] = reverseHeight(board.board()[i][j]);
      }
    }
    return new Board(heightMatrix);

  }

  private Height reverseHeight(Height height) {
    int elevation = Math.abs(height.elevation - 27);
    Position position = height.position;
    boolean startingPoint = height.endGoal;
    boolean endGoal = height.startingPoint;
    return new Height(elevation, position, startingPoint, endGoal);
  }


  public String findShortestPathFromStartingPointToEnd(Board board) {
    Map<Position, Integer> bfs = bfs(board);
    return String.valueOf(bfs.get(board.getEndPosition().position()));
  }

  private String lowestElevationToAny26(Board board) {
    Map<Position, Integer> bfs = bfs(board);
    // iterate everything, give me the minimumNumberOfSteps of any elevation 26
    int min = bfs.entrySet().stream().filter(entry -> {
      return board.getHeightInPosition(entry.getKey()).elevation() == 26;
    }).map(Map.Entry::getValue).min(Comparator.naturalOrder()).get();
    return String.valueOf(min);

  }

  private Map<Position, Integer> bfs(Board board) {
    Map<Position, Integer> positionMinimumNumberOfStepsMap = new HashMap<>();
    LinkedList<Position> positionsToVisit = new LinkedList<>();
    Position startingPosition = board.getStartingPosition().position();
    positionsToVisit.add(startingPosition);
    positionMinimumNumberOfStepsMap.put(startingPosition, 0);
    while (!positionsToVisit.isEmpty()) {
      Position currentPosition = positionsToVisit.removeFirst();
      Height currentHeight = board.getHeightInPosition(currentPosition);
      currentPosition.getNeighbors().stream().map(board::getHeightInPosition)
        .filter(Objects::nonNull).filter(neighbor -> {
          return !positionMinimumNumberOfStepsMap.containsKey(neighbor.position());
        }).filter(neighbor -> {
          assert currentHeight != null;
          return currentHeight.elevation() + 1 >= neighbor.elevation();
        }).forEach(neighbor -> {
          positionsToVisit.add(neighbor.position());
          positionMinimumNumberOfStepsMap.put(neighbor.position(), positionMinimumNumberOfStepsMap.get(currentPosition) + 1);
        });
    }
//    board.printElevations(positionMinimumNumberOfStepsMap);
    return positionMinimumNumberOfStepsMap;
  }

  private Board createBoard() {
    int numberOfRows = getInputLines().length;
    int numberOfColumns = getInputLines()[0].length();

    Height[][] heightMatrix = new Height[numberOfRows][numberOfColumns];
    for (int i = 0; i < numberOfRows; i++) {
      for (int j = 0; j < numberOfColumns; j++) {
        char c = getInputLines()[i].charAt(j);
        Height height = new Height(getElevation(c), new Position(i, j), c == 'S', c == 'E');
        heightMatrix[i][j] = height;
      }
    }

    return new Board(heightMatrix);
  }

  public int getElevation(char c) {
    if (c == 'S') return getElevation('a');
    if (c == 'E') return getElevation('z');
    return c - 96;
  }

  private record Board(Height[][] board) {

    private Height getStartingPosition() {
      for (int i = 0; i < this.board().length; i++) {
        for (int j = 0; j < this.board()[i].length; j++) {
          Height h = this.board()[i][j];
          if (h.startingPoint()) return h;
        }
      }
      return new Height();
    }

    private Height getEndPosition() {
      for (int i = 0; i < this.board().length; i++) {
        for (int j = 0; j < this.board()[i].length; j++) {
          Height h = this.board()[i][j];
          if (h.endGoal()) return h;
        }
      }
      return new Height();
    }

    private Height getHeightInPosition(Position position) {
      int numberOfRows = this.board().length;
      int numberOfColumns = this.board()[0].length;
      int x = position.x();
      int y = position.y();
      if (numberOfRows > x && x >= 0 && numberOfColumns > y && y >= 0)
        return this.board()[position.x][position.y];
      else return null;
    }

    public void printElevations(Map<Position, Integer> positionMinimumNumberOfStepsMap) {
      StringBuffer stringBuffer = new StringBuffer();
      for (int i = 0; i < this.board().length; i++) {
        for (int j = 0; j < this.board()[i].length; j++) {
          Height h = this.board()[i][j];
          Integer minimumNumberOfSteps = positionMinimumNumberOfStepsMap.get(h.position);
          stringBuffer.append(String.format("%03d", (minimumNumberOfSteps != null) ? minimumNumberOfSteps : 999)).append(" ");
        }
        stringBuffer.append("\n");
      }
      System.out.println(stringBuffer);
    }
  }

  private record Height(int elevation, Position position, boolean startingPoint, boolean endGoal) {
    public Height() {
      this(0, new Position(0, 0), false, false);
    }
  }

  private record Position(int x, int y) {
    public Position move(int xDelta, int yDelta) {
      return new Position(x + xDelta, y + yDelta);
    }

    public List<Position> getNeighbors() {
      return List.of(move(1, 0), move(-1, 0), move(0, 1), move(0, -1));
    }
  }
}
