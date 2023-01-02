package com.manu.day09;

import com.manu.Puzzle;

import java.util.*;
import java.util.stream.IntStream;

public class Day9 extends Puzzle {

  public Day9(String input) {
    super(input);
  }


  @Override
  public String part1() {
    return getAnswer(2); // 1 tail + head
  }

  @Override
  public String part2() {
    return getAnswer(10); // 9 tails + head
  }

  private String getAnswer(int size) {
    Board board = new Board(size);
    List<Instruction> instructions = Arrays.stream(this.getInputLines()).map(Instruction::new).toList();
    for (Instruction instruction : instructions) {
      for (int i = 0; i < instruction.repeat; i++) {
        board.performMove(instruction.direction);
      }
    }
    return String.valueOf(board.howManyPositionsTheTailVisitedOnce());
  }

  private static class Instruction {
    private final int repeat;
    private Direction direction;

    private Instruction(String s) {
      String[] s1 = s.split(" ");
      switch (s1[0]) {
        case "U" -> this.direction = Direction.UP;
        case "D" -> this.direction = Direction.DOWN;
        case "L" -> this.direction = Direction.LEFT;
        case "R" -> this.direction = Direction.RIGHT;
      }
      this.repeat = Integer.parseInt(s1[1]);
    }

    private enum Direction {
      UP, DOWN, LEFT, RIGHT;
    }
  }

  private record Board(List<Position> rope, Set<Position> tailPositionMap, int size) {

    public Board(int size) {
      this(new ArrayList<>(), new HashSet<>(), size);
      IntStream.range(0, size).forEach(i -> {
        this.rope.add(new Position(0, 0));
      });
      this.tailPositionMap.add(rope.get(this.size - 1));
    }

    private boolean areTwoPositionsFarAway(Position a, Position b) {
      return Math.abs(a.y - b.y) > 1 || Math.abs(a.x - b.x) > 1;
    }

    private void accommodateTail() {
      for (int i = 1; i < this.size; i++) {
        Position head = rope.get(i - 1);
        Position tail = rope.get(i);
        if (areTwoPositionsFarAway(head, tail)) {
          rope.set(i, moveTailOneSlotCloserToHead(head, tail));
        }
      }
      tailPositionMap.add(rope.get(this.size - 1));
    }

    private Position moveTailOneSlotCloserToHead(Position head, Position tail) {
      if (head.y == tail.y) {
        return tail.move((head.x > tail.x) ? 1 : -1, 0);
      } else if (head.x == tail.x) {
        return tail.move(0, (head.y > tail.y) ? 1 : -1);
      } else {
        return tail.move((head.x > tail.x) ? 1 : -1, (head.y > tail.y) ? 1 : -1);
      }
    }

    public int howManyPositionsTheTailVisitedOnce() {
      return tailPositionMap.size();
    }

    public Position moveHead(int xDelta, int yDelta) {
      return this.rope.get(0).move(xDelta, yDelta);
    }

    public Position moveHead(Instruction.Direction direction) {
      return switch (direction) {
        case UP -> moveHead(0, 1);
        case DOWN -> moveHead(0, -1);
        case RIGHT -> moveHead(1, 0);
        case LEFT -> moveHead(-1, 0);
        default -> null;
      };
    }

    public void performMove(Instruction.Direction direction) {
      this.rope.set(0, this.moveHead(direction));
      this.accommodateTail();
    }

    private record Position(int x, int y) {

      public Position move(int xDelta, int yDelta) {
        return new Position(this.x + xDelta, this.y + yDelta);
      }

    }
  }
}
