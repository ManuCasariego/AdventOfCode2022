package com.manu.day18;


import com.manu.Puzzle;

import java.util.*;

public class Day18 extends Puzzle {

  public Day18(String input) {
    super(input);
  }


  @Override
  public String part1() {
    // the puzzle gives us a 3d representation of lava droplets and asks us for the amount of cube sides that are not
    // connected to other cubes
    LavaDroplet lavaDroplet = buildLavaDroplet();
    return String.valueOf(lavaDroplet.surface());
  }

  @Override
  public String part2() {
    // for part 2 we can't count the air pockets inside the droplet, so we need to fill them before calculating the surface
    LavaDroplet lavaDroplet = buildLavaDroplet();
    return String.valueOf(lavaDroplet.surfaceWithoutAirPockets());
  }


  private LavaDroplet buildLavaDroplet() {
    List<Position> positions = new ArrayList<>();
    for (String s : getInputLines()) {
      positions.add(Position.parse(s));
    }

    int border = 1;
    int minX = positions.stream().map(Position::x).min(Comparator.naturalOrder()).orElse(0) - border;
    int maxX = positions.stream().map(Position::x).max(Comparator.naturalOrder()).orElse(0) + border;
    int minY = positions.stream().map(Position::y).min(Comparator.naturalOrder()).orElse(0) - border;
    int maxY = positions.stream().map(Position::y).max(Comparator.naturalOrder()).orElse(0) + border;
    int minZ = positions.stream().map(Position::z).min(Comparator.naturalOrder()).orElse(0) - border;
    int maxZ = positions.stream().map(Position::z).max(Comparator.naturalOrder()).orElse(0) + border;

    // leaving a size 1 border to create the board as it will help us to count all sides correctly

    int xSize = maxX - minX + 1;
    int ySize = maxY - minY + 1;
    int zSize = maxZ - minZ + 1;

    var lavaDroplet = new LavaDroplet(new int[xSize][ySize][zSize]);

    // the droplet shape will contain value -1
    for (Position p : positions) {
      lavaDroplet.cubes[p.x + 1][p.y + 1][p.z + 1] = -1;
    }

    return lavaDroplet;
  }

  public record LavaDroplet(int[][][] cubes) {

    private int surface() {
      int totalSurface = 0;

      for (int i = 0; i < cubes.length; i++) {
        for (int j = 0; j < cubes[i].length; j++) {
          for (int k = 0; k < cubes[i][j].length; k++) {
            // the droplet shape will have value -1
            if (cubes[i][j][k] == -1) {
              for (Position aP : new Position(i, j, k).adjacentPositions()) {
                if (cubes[aP.x][aP.y][aP.z] == 0) {
                  totalSurface++;
                }
              }
            }
          }
        }
      }
      return totalSurface;
    }

    public int surfaceWithoutAirPockets() {

      int totalSurface = 0;

      // here we need to fill the air pockets in this.board
      // through BFS we can go across the exterior surface of the board and change its value to 1
      // then we can calculate the surface again, only caring about the 1s


      // BFS
      Queue<Position> positionQueue = new ArrayDeque<>();
      Position currentPosition = new Position(0, 0, 0);
      positionQueue.add(currentPosition);

      while (!positionQueue.isEmpty()) {
        currentPosition = positionQueue.poll();
        this.cubes[currentPosition.x][currentPosition.y][currentPosition.z] = 1;
        for (Position ap : currentPosition.adjacentPositions()) {
          if (
            ap.x >= 0 && ap.y >= 0 && ap.z >= 0 && ap.x < cubes.length && ap.y < cubes[0].length && ap.z < cubes[0][0].length
              && this.cubes[ap.x][ap.y][ap.z] == 0 && !positionQueue.contains(ap)
          ) {
            positionQueue.add(ap);
          }
        }
      }

      // counting the surface of only the aPs with value == 1 which is the exterior
      for (int i = 0; i < cubes.length; i++) {
        for (int j = 0; j < cubes[i].length; j++) {
          for (int k = 0; k < cubes[i][j].length; k++) {
            // the droplet shape will have value -1
            if (cubes[i][j][k] == -1) {
              for (Position aP : new Position(i, j, k).adjacentPositions()) {
                // now we only care about the values that are 1
                if (cubes[aP.x][aP.y][aP.z] == 1) {
                  totalSurface++;
                }
              }
            }
          }
        }
      }

      return totalSurface;
    }

  }

  public record Position(int x, int y, int z) {
    public static Position parse(String s) {
      String[] s1 = s.split(",");
      return new Position(Integer.parseInt(s1[0].strip()), Integer.parseInt(s1[1].strip()), Integer.parseInt(s1[2].strip()));
    }

    private Position move(int xDelta, int yDelta, int zDelta) {
      return new Position(this.x + xDelta, this.y + yDelta, this.z + zDelta);
    }

    private List<Position> adjacentPositions() {
      List<Position> contiguousPositions = new ArrayList<>();
      contiguousPositions.add(this.move(1, 0, 0));
      contiguousPositions.add(this.move(-1, 0, 0));
      contiguousPositions.add(this.move(0, 1, 0));
      contiguousPositions.add(this.move(0, -1, 0));
      contiguousPositions.add(this.move(0, 0, 1));
      contiguousPositions.add(this.move(0, 0, -1));
      return contiguousPositions;
    }
  }
}
