package com.manu.day17;


import com.manu.Puzzle;

import java.util.*;
import java.util.stream.Collectors;

public class Day17 extends Puzzle {

  public Day17(String input) {
    super(input);
  }


  @Override
  public String part1() {

    // Custom tetris exercise
    // movement order -> first wind side, then down, repeat...
    // board is seven units wide and as tall as you want
    // Each rock appears so that its left edge is two units away from the left wall and its bottom edge is three units
    // above the highest rock in the room (or the floor).

    Board b = buildBoard();
    b.playUntilNumberOfRocks(2022);
    return String.valueOf(b.getCurrentCeilingMaxY() + Board.cutHeight);
  }

  @Override
  public String part2() {
    // for this part instead of storing every fallen rock I'm just storing the "ceiling"
    // since they want a trillion rocks, I'm trying to detect a cycle of states (rockCeiling, windTick and rockTypeTick)
    Board b = buildBoard();
    b.playUntilNumberOfRocks(1_000_000_000_000L);
    return String.valueOf(b.getCurrentCeilingMaxY() + Board.cutHeight);
  }


  private Board buildBoard() {
    List<Wind> input = new ArrayList<>();
    for (Character c : getInput().toCharArray()) {
      input.add(c == '<' ? Wind.WIND_LEFT : Wind.WIND_RIGHT);
    }
    return new Board(new ArrayList<>(), input);
  }

  private record Board(List<Position> currentCeiling, List<Wind> input) {

    private static final int floor = 0, wide = 7;
    private static int rockVariationCount = 0, windTickCount = 0;
    private static long cutHeight = 0;
    private static HashMap<State, RockInfo> stateIntegerHashMap = new HashMap<>();


    private Board(List<Position> currentCeiling, List<Wind> input) {
      this.input = input;
      this.currentCeiling = currentCeiling;
      stateIntegerHashMap = new HashMap<>();
      rockVariationCount = 0;
      windTickCount = 0;
      cutHeight = 0;
    }

    private Wind getNextWind() {
      Wind wind = input.get(windTickCount % input.size());
      windTickCount = (windTickCount + 1) % input.size();
      return wind;
    }

    private int getCurrentCeilingMaxY() {
      if (currentCeiling.isEmpty()) return floor;
      return currentCeiling.stream().map(Position::y).max(Comparator.naturalOrder()).orElse(floor);
    }

    private Rock generateNextRock() {
      Position p = new Position(2, getCurrentCeilingMaxY() + 4);

      Rock rock = switch (rockVariationCount % 5) {
        case 0 -> Rock.horizontalLineRock(p);
        case 1 -> Rock.plusRock(p);
        case 2 -> Rock.reversedLRock(p);
        case 3 -> Rock.verticalLineRock(p);
        case 4 -> Rock.squareRock(p);
        default -> throw new IllegalStateException("Unexpected value: " + rockVariationCount % 5);
      };
      rockVariationCount = (rockVariationCount + 1) % 5;
      return rock;
    }


    public void playUntilNumberOfRocks(long maxNumberOfRocks) {
      long rockCount = 0;
      while (rockCount < maxNumberOfRocks) {
        Rock fallingRock = this.generateNextRock();
        rockCount++;
        boolean rockGoingDown = true;
        while (rockGoingDown) {
          fallingRock = this.moveRock(fallingRock, getNextWind());
          Rock rockTriesToGoDown = this.moveRockDown(fallingRock);
          if (rockTriesToGoDown.equals(fallingRock)) {
            // the rock didn't move, we found an obstacle -> new rock
            rockGoingDown = false;
          } else {
            fallingRock = rockTriesToGoDown;
          }
        }
        buildCeilingCast(fallingRock);
        State s = new State(this.currentCeiling, rockVariationCount, windTickCount);
        if (stateIntegerHashMap.containsKey(s)) {
          // we found a cycle, then we can skip n cycles to save time
          long howManyRocksWerePlaced = rockCount - stateIntegerHashMap.get(s).rockCount;
          long cutHeightDiff = cutHeight - stateIntegerHashMap.get(s).cutHeight;
          long div = Math.floorDiv((maxNumberOfRocks - rockCount), howManyRocksWerePlaced);
          rockCount = rockCount + howManyRocksWerePlaced * div;
          cutHeight = cutHeight + cutHeightDiff * div;
        }
        stateIntegerHashMap.put(s, new RockInfo(rockCount, cutHeight));
      }
    }

    private void buildCeilingCast(Rock fallingRock) {
      // fallingRock shouldn't have any common Position with current ceiling
      this.currentCeiling.addAll(fallingRock.positions);

      Set<Position> newCeiling = buildNewCeiling();

      // moving the newceiling down and updating heightcut with the y amount we cut
      int minY = newCeiling.stream().map(Position::y).min(Comparator.naturalOrder()).orElse(0);
      cutHeight += minY - 1;
      newCeiling = newCeiling.stream().map(p -> p.move(0, 1 - minY)).collect(Collectors.toSet());

      // clearing the current ceiling and storing the new one
      currentCeiling.clear();
      currentCeiling.addAll(newCeiling);
    }

    private Set<Position> buildNewCeiling() {
      int startingY = getCurrentCeilingMaxY() + 2;
      Position traversingPosition = new Position(2, startingY);
      // we create the ceilingcast of air
      Set<Position> ceilingCast = buildCeilingCast(traversingPosition.down(), traversingPosition.y, new HashSet<>());

      // from the cast of air, we can build the ceiling finding the edge of the cast
      Set<Position> newCeiling = new HashSet<>();
      for (Position ceilingCastPosition : ceilingCast) {
        Position down = ceilingCastPosition.down();
        if (!ceilingCast.contains(down) && isPositionInsideTheBoard(down, startingY)) newCeiling.add(down);

        Position up = ceilingCastPosition.up();
        if (!ceilingCast.contains(up) && isPositionInsideTheBoard(up, startingY)) newCeiling.add(up);

        Position left = ceilingCastPosition.left();
        if (!ceilingCast.contains(left) && isPositionInsideTheBoard(left, startingY)) newCeiling.add(left);


        Position right = ceilingCastPosition.right();
        if (!ceilingCast.contains(right) && isPositionInsideTheBoard(right, startingY)) newCeiling.add(right);
      }
      return newCeiling;
    }

    private boolean isPositionInsideTheBoard(Position p, int maxY) {
      return p.x >= 0 && p.x < wide && p.y > floor && p.y < maxY;
    }

    private Set<Position> buildCeilingCast(Position p, int maxY, Set<Position> visitedPositions) {
      // we need a maxY so the method has an upper Y ceiling
      if (visitedPositions.contains(p) || currentCeiling.contains(p)) return new HashSet<>();
      if (p.x < 0 || p.x >= wide || p.y <= floor || p.y >= maxY) return new HashSet<>();

      visitedPositions.add(p);

      visitedPositions.addAll(buildCeilingCast(p.down(), maxY, visitedPositions));
      visitedPositions.addAll(buildCeilingCast(p.right(), maxY, visitedPositions));
      visitedPositions.addAll(buildCeilingCast(p.left(), maxY, visitedPositions));
      visitedPositions.addAll(buildCeilingCast(p.up(), maxY, visitedPositions));

      return visitedPositions;
    }

    private void draw() {
      StringBuffer sb = new StringBuffer();
      int maxY = getCurrentCeilingMaxY() + 5;
      for (int j = maxY; j >= floor; j--) {
        for (int i = 0; i < 7; i++) {
          boolean doWeNeedToPaint = borderCheck(new Position(i, j));

          if (doWeNeedToPaint) sb.append("███");
          else sb.append("...");
        }
        sb.append("\n");
      }
      System.out.println(sb);
    }


    private Rock moveRock(Rock fallingRock, Wind wind) {
      return moveRock(fallingRock, (wind == Wind.WIND_LEFT) ? -1 : 1, 0);
    }

    private Rock moveRockDown(Rock fallingRock) {
      return moveRock(fallingRock, 0, -1);
    }

    private Rock moveRock(Rock fallingRock, int deltaX, int deltaY) {
      // moves the falling rock and returns it, if it can't move then it will return the same rock
      Rock movedRock = fallingRock.move(deltaX, deltaY);
      return borderCheck(movedRock) ? fallingRock : movedRock;
    }

    private boolean borderCheck(Rock rock) {
      for (Position p : rock.positions) {
        if (this.borderCheck(p)) return true;
      }
      return false;
    }

    /**
     * @param p position
     * @return true if it collides with the wall or an existing rock
     */
    private boolean borderCheck(Position p) {
      if (p.x < 0 || p.x >= wide || p.y <= floor) return true;
      return currentCeiling.contains(p);
    }
  }

  private record Rock(List<Position> positions) {

    private Rock move(int deltaX, int deltaY) {
      return new Rock(positions.stream().map(position -> position.move(deltaX, deltaY)).toList());
    }

    // for the static methods, position would be their left bottom edge
    private static Rock horizontalLineRock(Position p) {
      return new Rock(List.of(p, p.right(), p.right().right(), p.right().right().right()));
    }

    private static Rock plusRock(Position p) {
      return new Rock(List.of(p.right(), p.up(), p.right().up(), p.right().up().up(), p.right().up().right()));
    }

    private static Rock reversedLRock(Position p) {
      return new Rock(List.of(p, p.right(), p.right().right(), p.right().right().up(), p.right().right().up().up()));
    }

    private static Rock verticalLineRock(Position p) {
      return new Rock(List.of(p, p.up(), p.up().up(), p.up().up().up()));
    }

    private static Rock squareRock(Position p) {
      return new Rock(List.of(p, p.up(), p.right(), p.right().up()));
    }

  }


  private record Position(int x, int y) {
    private Position up() {
      return this.move(0, 1);
    }

    private Position down() {
      return this.move(0, -1);
    }

    private Position right() {
      return this.move(1, 0);
    }

    private Position left() {
      return this.move(-1, 0);
    }

    private Position move(int deltaX, int deltaY) {
      return new Position(this.x + deltaX, this.y + deltaY);
    }
  }

  private enum Wind {WIND_RIGHT, WIND_LEFT}

  private record State(List<Position> ceiling, int rockCount, int windCount) {
  }

  private record RockInfo(long rockCount, long cutHeight) {
  }
}
