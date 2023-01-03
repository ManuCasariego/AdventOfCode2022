package com.manu.day14;

import com.manu.Puzzle;

import java.util.*;
import java.util.stream.IntStream;

public class Day14 extends Puzzle {

  public Day14(String input) {
    super(input);
  }


  @Override
  public String part1() {
    // snow falls from 500, 0
    Board b = buildBoard();
    int cycles = sandCyclesOnBoard(b);
    b.draw();
    return String.valueOf(cycles);
  }

  @Override
  public String part2() {
    Board b = buildBoard();
    addFloorToBoard(b);
    int cycles = sandCyclesOnBoard(b);
    b.draw();
    return String.valueOf(cycles);
  }

  private int sandCyclesOnBoard(Board b) {
    int cycles = 0;
    int count = 0;

    Position sand = new Position(500, 0);
    while (count < 1000 && !b.positions.contains(new Position(500, 0))) {
      if (!b.positions.contains(sand.moveDown())) {
        sand = sand.moveDown();
        count++;
      } else if (!b.positions.contains(sand.moveDown().moveLeft())) {
        sand = sand.moveDown().moveLeft();
      } else if (!b.positions.contains(sand.moveDown().moveRight())) {
        sand = sand.moveDown().moveRight();
      } else {
        // nowhere to go
        b.positions.add(sand);
        sand = new Position(500, 0);
        cycles++;
        count = 0;
      }
    }

    return cycles;
  }


  private Board addFloorToBoard(Board b) {
    int maxX = b.positions.stream().map(position -> position.x).max(Comparator.naturalOrder()).orElse(0) + 1000;
    int minX = b.positions.stream().map(position -> position.x).min(Comparator.naturalOrder()).orElse(0) - 1000;
    int floorY = b.positions.stream().map(position -> position.y).max(Comparator.naturalOrder()).orElse(0) + 2;
    b.positions.addAll(getLine(new Position(minX, floorY), new Position(maxX, floorY)));
    return b;
  }

  private Board buildBoard() {
    Board board = new Board(new HashSet<>());

    for (String s : this.getInputLines()) {
      var rockLine = getRockLine(s);
      for (int i = 1; i < rockLine.size(); i++) {
        board.positions().addAll(getLine(rockLine.get(i - 1), rockLine.get(i)));

      }
    }
    return board;
  }

  private Collection<Position> getLine(Position a, Position b) {
    if (a.x == b.x) return getIntCollection(a.y, b.y).mapToObj(y -> new Position(a.x, y)).toList();
    else return getIntCollection(a.x, b.x).mapToObj(x -> new Position(x, a.y)).toList();
  }

  private IntStream getIntCollection(Integer a, Integer b) {
    if (a > b) return getIntCollection(b, a);
    return IntStream.rangeClosed(a, b);
  }

  private List<Position> getRockLine(String s) {
    var rockLine = new ArrayList<Position>();
    for (String strPosition : s.split("->")) {
      rockLine.add(Position.parse(strPosition));
    }
    return rockLine;
  }

  private record Board(Set<Position> positions) {
    public void draw() {
      int border = 3;
      int maxX = this.positions.stream().map(position -> position.x).max(Comparator.naturalOrder()).orElse(0) + border;
      int minX = this.positions.stream().map(position -> position.x).min(Comparator.naturalOrder()).orElse(0) - border;
      int maxY = this.positions.stream().map(position -> position.y).max(Comparator.naturalOrder()).orElse(0) + border;
      int minY = this.positions.stream().map(position -> position.y).min(Comparator.naturalOrder()).orElse(0) - border;
      StringBuffer sb = new StringBuffer().append("\n");
      for (int y = minY; y <= maxY; y++) {
        for (int x = minX; x <= maxX; x++) {
          if (this.positions.contains(new Position(x, y))) sb.append("███");
          else sb.append("...");
        }
        sb.append("\n");
      }
      System.out.println(sb);
    }
  }

  private record Position(int x, int y) {
    private Position moveRight() {
      return new Position(this.x + 1, this.y);
    }

    private Position moveLeft() {
      return new Position(this.x - 1, this.y);
    }

    private Position moveDown() {
      return new Position(this.x, this.y + 1);
    }

    private static Position parse(String s) {
      String[] s1 = s.replace(" ", "").split(",");
      return new Position(Integer.parseInt(s1[0]), Integer.parseInt(s1[1]));
    }
  }

}
