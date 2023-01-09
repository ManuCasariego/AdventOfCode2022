package com.manu;

import com.manu.day01.Day1;
import com.manu.day02.Day2;
import com.manu.day03.Day3;
import com.manu.day04.Day4;
import com.manu.day05.Day5;
import com.manu.day06.Day6;
import com.manu.day07.Day7;
import com.manu.day08.Day8;
import com.manu.day09.Day9;
import com.manu.day10.Day10;
import com.manu.day11.Day11;
import com.manu.day12.Day12;
import com.manu.day13.Day13;
import com.manu.day14.Day14;
import com.manu.day15.Day15;
import com.manu.day16.Day16;
import com.manu.day17.Day17;
import com.manu.day18.Day18;
import com.manu.day19.Day19;
import com.manu.day20.Day20;
import com.manu.day21.Day21;
import com.manu.day22.Day22;
import com.manu.day23.Day23;
import com.manu.day24.Day24;
import com.manu.day25.Day25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RunCode {


  public static void main(String[] args) throws IOException {
    run(20);
  }


  public static void run(int day) throws IOException {
    String input = loadInput(day);
    Puzzle puzzle = getPuzzle(day, input);
    puzzle.printSolutions();
  }

  public static Puzzle getPuzzle(int day, String input) {
    return switch (day) {
      case 1 -> new Day1(input);
      case 2 -> new Day2(input);
      case 3 -> new Day3(input);
      case 4 -> new Day4(input);
      case 5 -> new Day5(input);
      case 6 -> new Day6(input);
      case 7 -> new Day7(input);
      case 8 -> new Day8(input);
      case 9 -> new Day9(input);
      case 10 -> new Day10(input);
      case 11 -> new Day11(input);
      case 12 -> new Day12(input);
      case 13 -> new Day13(input);
      case 14 -> new Day14(input);
      case 15 -> new Day15(input);
      case 16 -> new Day16(input);
      case 17 -> new Day17(input);
      case 18 -> new Day18(input);
      case 19 -> new Day19(input);
      case 20 -> new Day20(input);
      case 21 -> new Day21(input);
      case 22 -> new Day22(input);
      case 23 -> new Day23(input);
      case 24 -> new Day24(input);
      case 25 -> new Day25(input);
      default -> null;
    };

  }


  public static String loadInput(String day) throws IOException {
    String separator = System.getProperty("file.separator");
    StringBuffer sb =
      new StringBuffer().append(System.getProperty("user.dir")).append(separator).append("src").append(separator)
        .append("main").append(separator).append("java").append(separator).append("com").append(separator).append("manu")
        .append(separator).append(day).append(separator).append("input");
    return Files.readString(Path.of(sb.toString()));
  }

  public static String loadInput(int day) throws IOException {
    return loadInput("day" + String.format("%02d", day));
  }
}
