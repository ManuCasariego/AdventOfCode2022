package com.manu.day23;


import com.manu.Puzzle;

import java.util.*;
import java.util.stream.IntStream;

public class Day23 extends Puzzle {

  public Day23(String input) {
    super(input);
  }


  @Override
  public String part1() {

    // the input is a list of elves
    Board b = buildBoard();

    b.doRounds(10);
    return b.getNumberEmptyTilesInSmallestRectangle();
  }

  @Override
  public String part2() {

    Board b = buildBoard();

    int count = 0;
    int numberOfElvesMoved = 1;
    while (numberOfElvesMoved > 0) {
      numberOfElvesMoved = b.doRound();
      count++;
    }
    return String.valueOf(count);
  }


  private Board buildBoard() {
    Direction.resetCycles();
    Board b = new Board();

    for (int j = 0; j < getInputLines().length; j++) {
      for (int i = 0; i < getInputLines()[j].length(); i++) {
        char c = getInputLines()[j].charAt(i);
        if (c == '#') {
          // we found an elf
          b.elves.add(new Elf(i, j));
        }
      }
    }
    return b;
  }


  private record Board(List<Elf> elves) {
    public Board() {
      this(new ArrayList<>());
    }

    private String getNumberEmptyTilesInSmallestRectangle() {
      int minY = elves.stream().map(elf -> elf.y).min(Comparator.naturalOrder()).orElse(0);
      int maxY = elves.stream().map(elf -> elf.y).max(Comparator.naturalOrder()).orElse(0);
      int minX = elves.stream().map(elf -> elf.x).min(Comparator.naturalOrder()).orElse(0);
      int maxX = elves.stream().map(elf -> elf.x).max(Comparator.naturalOrder()).orElse(0);
      return String.valueOf((maxY - minY + 1) * (maxX - minX + 1) - elves.size());
    }

    private AuxiliaryBoard buildAuxBoard(List<Elf> elves) {
      // we adjust x and y accordingly in order to fit a native array that goes from 0 to n
      int minY = elves.stream().map(elf -> elf.y).min(Comparator.naturalOrder()).orElse(0);
      int maxY = elves.stream().map(elf -> elf.y).max(Comparator.naturalOrder()).orElse(0);
      int minX = elves.stream().map(elf -> elf.x).min(Comparator.naturalOrder()).orElse(0);
      int maxX = elves.stream().map(elf -> elf.x).max(Comparator.naturalOrder()).orElse(0);

      int numberOfColumns = maxX - minX + 1;
      int numberOfRows = maxY - minY + 1;
      int marginX = -minX;
      int marginY = -minY;
      AuxiliaryBoard auxBoard = new AuxiliaryBoard(numberOfColumns, numberOfRows, marginX, marginY);

      elves.forEach(auxBoard::put);

      return auxBoard;
    }

    public void doRounds(int noOfRounds) {
      IntStream.range(0, noOfRounds).forEach(i -> doRound());
    }

    public int doRound() {
      int numberOfElvesMoved = 0;
      AuxiliaryBoard auxBoard = buildAuxBoard(elves);
      List<Elf> elvesThatCouldMove = new ArrayList<>();
      // first half: check whether they have another adjacent elf or not
      for (Elf elf : elves) {
        if (elf.anyAdjacentElf(auxBoard)) {
          elvesThatCouldMove.add(elf);
        }
      }

      // second half: we need to check where the elves want to go, if only one elf wants to go to that position, then it moves,
      // if there are more than one elf that want to jump into the same position, then neither of them move
      Map<Elf, Elf> wantToGoPositions = new HashMap<>();

      // getting the directions we need to follow
      List<Direction> directions = Direction.getDirections();

      for (Elf elf : elvesThatCouldMove) {
        for (Direction direction : directions) {
          if (elf.canMoveToDirection(direction, auxBoard)) {
            Elf newPositionElf = elf.getMovedElf(direction);
            if (wantToGoPositions.containsKey(newPositionElf)) {
              // it means this position won't work
              wantToGoPositions.put(newPositionElf, null);
            } else {
              wantToGoPositions.put(newPositionElf, elf);
            }
            break;
          }
        }
      }

      // final check: now we only move the elves that want to jump into a position that no one else will jump to
      for (Map.Entry<Elf, Elf> entry : wantToGoPositions.entrySet()) {
        if (entry.getValue() != null) {
          numberOfElvesMoved++;
          this.elves.remove(entry.getValue());
          this.elves.add(entry.getKey());
        }
      }
//      draw();
      Direction.nextCycle();
      return numberOfElvesMoved;
    }

    private void draw() {
      StringBuffer sb = new StringBuffer();
      int minY = elves.stream().map(elf -> elf.y).min(Comparator.naturalOrder()).orElse(0);
      int maxY = elves.stream().map(elf -> elf.y).max(Comparator.naturalOrder()).orElse(0);
      int minX = elves.stream().map(elf -> elf.x).min(Comparator.naturalOrder()).orElse(0);
      int maxX = elves.stream().map(elf -> elf.x).max(Comparator.naturalOrder()).orElse(0);

      for (int j = minY; j <= maxY; j++) {
        for (int i = minX; i <= maxX; i++) {

          if (this.elves.contains(new Elf(i, j))) {
            sb.append("███");
          } else sb.append("...");
        }
        sb.append("\n");
      }
      System.out.println(sb);
    }
  }


  private enum Direction {
    NORTH, SOUTH, WEST, EAST;
    private static int flag = 0;

    private static List<Direction> getDirections() {
      switch (flag) {
        case 0 -> {
          return List.of(NORTH, SOUTH, WEST, EAST);
        }
        case 1 -> {
          return List.of(SOUTH, WEST, EAST, NORTH);
        }
        case 2 -> {
          return List.of(WEST, EAST, NORTH, SOUTH);
        }
        case 3 -> {
          return List.of(EAST, NORTH, SOUTH, WEST);
        }
        default -> throw new RuntimeException("direction not found");
      }
    }

    private static void nextCycle() {
      flag = Math.floorMod((flag + 1), 4);
    }

    private static void resetCycles() {
      flag = 0;
    }

  }

  private record Elf(int x, int y) {

    public boolean anyAdjacentElf(AuxiliaryBoard board) {
      for (Elf adjElf : this.adjacentElves()) {
        if (board.contains(adjElf.x, adjElf.y)) return true;
      }
      return false;
    }

    private List<Elf> adjacentElves() {
      List<Elf> adjacentElves = new ArrayList<>();
      adjacentElves.add(getMovedElf(Direction.NORTH));
      adjacentElves.add(getMovedElf(Direction.SOUTH));
      adjacentElves.add(getMovedElf(Direction.EAST));
      adjacentElves.add(getMovedElf(Direction.WEST));
      adjacentElves.add(getMovedElf(1, 1));
      adjacentElves.add(getMovedElf(1, -1));
      adjacentElves.add(getMovedElf(-1, 1));
      adjacentElves.add(getMovedElf(-1, -1));
      return adjacentElves;
    }

    private Elf getMovedElf(int deltaX, int deltaY) {
      return new Elf(x + deltaX, y + deltaY);
    }

    private Elf getMovedElf(Direction direction) {
      switch (direction) {
        case NORTH -> {
          return getMovedElf(0, -1);
        }
        case SOUTH -> {
          return getMovedElf(0, 1);
        }
        case WEST -> {
          return getMovedElf(-1, 0);
        }
        case EAST -> {
          return getMovedElf(1, 0);
        }
        default -> throw new RuntimeException("no direction found");
      }
    }

    public boolean canMoveToDirection(Direction direction, AuxiliaryBoard board) {
      switch (direction) {
        case WEST, EAST -> {
          if (board.contains(getMovedElf(direction)) || board.contains(getMovedElf(direction).getMovedElf(Direction.NORTH)) || board.contains(getMovedElf(direction).getMovedElf(Direction.SOUTH)))
            return false;
        }
        case NORTH, SOUTH -> {
          if (board.contains(getMovedElf(direction)) || board.contains(getMovedElf(direction).getMovedElf(Direction.EAST)) || board.contains(getMovedElf(direction).getMovedElf(Direction.WEST)))
            return false;
        }
      }
      return true;
    }
  }

  // creating an auxiliary board to add elves positions, this way we can do the checks way quicker than iterating a list
  private record AuxiliaryBoard(boolean[][] board, int marginX, int marginY) {
    public AuxiliaryBoard(int noOfColumns, int noOfRows, int marginX, int marginY) {
      this(new boolean[noOfColumns][noOfRows], marginX, marginY);
    }

    private boolean contains(int x, int y) {
      int adjustedX = x + marginX;
      int adjustedY = y + marginY;

      if (adjustedX >= 0 && adjustedY >= 0 && adjustedX < board.length && adjustedY < board[adjustedX].length) {
        return board[adjustedX][adjustedY];
      } else return false;
    }

    private boolean contains(Elf elf) {
      return contains(elf.x, elf.y);
    }

    public void put(Elf elf) {
      put(elf.x, elf.y);
    }

    public void put(int x, int y) {
      int adjustedX = x + marginX;
      int adjustedY = y + marginY;
      board[adjustedX][adjustedY] = true;
    }
  }

}
