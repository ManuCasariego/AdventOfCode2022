package com.manu.day15;


import com.manu.Puzzle;

import java.util.*;
import java.util.stream.Collectors;

public class Day15 extends Puzzle {

  public Day15(String input) {
    super(input);
  }

  @Override
  public String part1() {
    Board b = buildBoard();
    return String.valueOf(b.noBeaconsAtY(2000000));
  }

  @Override
  public String part2() {
    Board b = buildBoard();
    // find the only point where we could have an undetected beacon
    // between 0,0 and 4_000_000, 4_000_000 (20,20 for ex)
    int max = 4_000_000;
    for (Map.Entry<Position, Integer> radiusEntry : b.radius.entrySet()) {
      for (Position p : buildPerimeter(radiusEntry.getKey(), radiusEntry.getValue() + 1, 0, max)) {
        if (b.undetectedBeacon(p)) {
          return String.valueOf((long) p.x * 4_000_000 + (long) p.y);
        }
      }
    }
    return null;
  }

  private Set<Position> buildPerimeter(Position centre, int radius, int min, int max) {
    Set<Position> perimeter = new HashSet<>();
    var a = new Position(centre.x + radius, centre.y);
    var b = new Position(centre.x, centre.y + radius);
    var c = new Position(centre.x - radius, centre.y);
    var d = new Position(centre.x, centre.y - radius);

    perimeter.addAll(buildLine(a, b));
    perimeter.addAll(buildLine(b, c));
    perimeter.addAll(buildLine(c, d));
    perimeter.addAll(buildLine(d, a));
    return perimeter.stream().filter(p -> p.x >= min && max >= p.x && p.y >= min && max >= p.y).collect(Collectors.toSet());
  }

  private Set<Position> buildLine(Position a, Position b) {
    Set<Position> line = new HashSet<>();

    var deltaX = (a.x < b.x) ? 1 : -1;
    var deltaY = (a.y < b.y) ? 1 : -1;

    var currentPosition = a;
    while (!currentPosition.equals(b)) {
      line.add(currentPosition);
      currentPosition = currentPosition.move(deltaX, deltaY);
    }
    line.add(currentPosition);
    return line;
  }

  public Board buildBoard() {
    Board b = new Board(new HashMap<>(), new HashMap<>());
    for (String line : getInputLines()) {
      line = line.replace("Sensor at ", "").replace(" closest beacon is at ", "");
      String[] s1 = line.split(":");
      Position sensorPosition = Position.parse(s1[0]);
      b.map.put(sensorPosition, Type.SENSOR);
      Position beaconPosition = Position.parse(s1[1]);
      b.map.put(beaconPosition, Type.BEACON);
      b.radius.put(sensorPosition, sensorPosition.getRadius(beaconPosition));
    }
    return b;
  }

  private record Board(Map<Position, Type> map, Map<Position, Integer> radius) {

    private int noBeaconsAtY(int y) {
      // create the line starting from minX - maxRadius to maxX + maxRadius
      // for each point in line check insideAnyRadius
      int count = 0;
      int minX = this.radius.keySet().stream().map(Position::x).min(Comparator.naturalOrder()).orElse(0);
      int maxX = this.radius.keySet().stream().map(Position::x).max(Comparator.naturalOrder()).orElse(0);
      int maxRadius = this.radius.values().stream().max(Comparator.naturalOrder()).orElse(0);
      for (int x = minX - maxRadius; x < maxX + maxRadius; x++) {
        if (insideAnyRadius(new Position(x, y)) && !this.map.containsKey(new Position(x, y))) {
          count++;
        }
      }
      return count;
    }

    private boolean insideAnyRadius(Position p) {
      for (Map.Entry<Position, Integer> entry : this.radius.entrySet()) {
        if (p.getRadius(entry.getKey()) <= entry.getValue()) {
          return true;
        }
      }
      return false;
    }

    public boolean undetectedBeacon(Position p) {
      if (this.map.containsKey(p)) {
        return false;
      } else return !insideAnyRadius(p);
    }

    public void draw(int min, int max) {
      StringBuffer sb = new StringBuffer();
      for (int y = min; y <= max; y++) {
        for (int x = min; x <= max; x++) {
          if (insideAnyRadius(new Position(x, y))) {
            sb.append("███");
          } else sb.append("...");
        }
        sb.append("\n");
      }
      System.out.println(sb);
    }
  }


  private record Position(int x, int y) {
    private static Position parse(String s) {
      String[] s1 = s.replace("x=", "").replace(" y=", "").split(",");
      return new Position(Integer.parseInt(s1[0]), Integer.parseInt(s1[1]));
    }

    private int getRadius(Position radius) {
      // distance between two positions
      return Math.abs(this.x - radius.x) + Math.abs(this.y - radius.y);
    }

    private Position move(int deltaX, int deltaY) {
      return new Position(x + deltaX, y + deltaY);
    }
  }

  private enum Type {SENSOR, BEACON}
}
