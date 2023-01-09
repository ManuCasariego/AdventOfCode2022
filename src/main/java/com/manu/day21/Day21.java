package com.manu.day21;


import com.manu.Puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day21 extends Puzzle {

  public Day21(String input) {
    super(input);
  }


  @Override
  public String part1() {
    var monkeyMap = buildMonkeyMap();
    return String.valueOf(processMonkeyPart1(monkeyMap.get("root"), monkeyMap));
  }


  @Override
  public String part2() {
    var monkeyMap = buildMonkeyMap();

    // the objective here is to get 0 from the processMonkeyPart2 method

    // we are going to use a binary search, searching every single long from min to max, discarding half on every iteration

    Long min = -1000000000000L;
    Long max = 10000000000000L;
    Long midPoint = min + (max - min) / 2;

    Long guess = processMonkeyPart2(monkeyMap.get("root"), monkeyMap, midPoint);
    while (guess != 0) {
      // do binary search
      if (guess > 0) {
        // go up
        if (min.equals(midPoint)) return "nope";
        min = midPoint + 1;
      } else {
        // go down
        if (max.equals(midPoint)) return "nope";
        max = midPoint - 1;
      }
      midPoint = min + (max - min) / 2;
      guess = processMonkeyPart2(monkeyMap.get("root"), monkeyMap, midPoint);
    }

    return String.valueOf(midPoint);
  }


  private Long processMonkeyPart2(Monkey m, HashMap<String, Monkey> monkeyMap, Long humnNo) {
    if (m.name.equals("root")) {
      Long first = processMonkeyPart2(monkeyMap.get(m.operationMonkeys.get(0)), monkeyMap, humnNo);
      Long second = processMonkeyPart2(monkeyMap.get(m.operationMonkeys.get(1)), monkeyMap, humnNo);
      Long diff = first - second;
      return diff;
    } else if (m.name.equals("humn")) {
      return humnNo;
    }
    switch (m.operation) {
      case NOTHING -> {
        return m.value;
      }
      case SUB -> {
        return processMonkeyPart2(monkeyMap.get(m.operationMonkeys.get(0)), monkeyMap, humnNo) - processMonkeyPart2(monkeyMap.get(m.operationMonkeys.get(1)), monkeyMap, humnNo);
      }
      case MULTIPLICATION -> {
        return processMonkeyPart2(monkeyMap.get(m.operationMonkeys.get(0)), monkeyMap, humnNo) * processMonkeyPart2(monkeyMap.get(m.operationMonkeys.get(1)), monkeyMap, humnNo);
      }
      case DIVISION -> {
        return processMonkeyPart2(monkeyMap.get(m.operationMonkeys.get(0)), monkeyMap, humnNo) / processMonkeyPart2(monkeyMap.get(m.operationMonkeys.get(1)), monkeyMap, humnNo);
      }
      case SUM -> {
        return processMonkeyPart2(monkeyMap.get(m.operationMonkeys.get(0)), monkeyMap, humnNo) + processMonkeyPart2(monkeyMap.get(m.operationMonkeys.get(1)), monkeyMap, humnNo);
      }
      default -> {
        throw new IllegalStateException("Unexpected value: " + m.operation);
      }
    }
  }


  private Long processMonkeyPart1(Monkey m, HashMap<String, Monkey> monkeyMap) {
    switch (m.operation) {
      case NOTHING -> {
        return m.value;
      }
      case SUB -> {
        return processMonkeyPart1(monkeyMap.get(m.operationMonkeys.get(0)), monkeyMap) - processMonkeyPart1(monkeyMap.get(m.operationMonkeys.get(1)), monkeyMap);
      }
      case MULTIPLICATION -> {
        return processMonkeyPart1(monkeyMap.get(m.operationMonkeys.get(0)), monkeyMap) * processMonkeyPart1(monkeyMap.get(m.operationMonkeys.get(1)), monkeyMap);
      }
      case DIVISION -> {
        return processMonkeyPart1(monkeyMap.get(m.operationMonkeys.get(0)), monkeyMap) / processMonkeyPart1(monkeyMap.get(m.operationMonkeys.get(1)), monkeyMap);
      }
      case SUM -> {
        return processMonkeyPart1(monkeyMap.get(m.operationMonkeys.get(0)), monkeyMap) + processMonkeyPart1(monkeyMap.get(m.operationMonkeys.get(1)), monkeyMap);
      }
      default -> {
        throw new IllegalStateException("Unexpected value: " + m.operation);
      }
    }
  }

  private HashMap<String, Monkey> buildMonkeyMap() {
    var monkeyMap = new HashMap<String, Monkey>();
    for (String s : getInputLines()) {
      Monkey m = Monkey.parse(s);
      monkeyMap.put(m.name, m);
    }
    return monkeyMap;
  }

  private record Monkey(List<String> operationMonkeys, Operation operation, long value, String name) {

    public static Monkey parse(String s) {
      String[] words = s.split(" ");
      String name = words[0].replace(":", "");
      if (words.length == 2) {
        return new Monkey(new ArrayList<>(), Operation.NOTHING, Integer.parseInt(words[1]), name);
      } else {
        var operationMonkeys = new ArrayList<String>();
        operationMonkeys.add(words[1]);
        operationMonkeys.add(words[3]);
        Operation op = switch (words[2]) {
          case "+" -> Operation.SUM;
          case "-" -> Operation.SUB;
          case "*" -> Operation.MULTIPLICATION;
          case "/" -> Operation.DIVISION;
          default -> Operation.NOTHING;
        };
        return new Monkey(operationMonkeys, op, 0, name);
      }
    }
  }

  private enum Operation {
    MULTIPLICATION, DIVISION, SUM, SUB, NOTHING
  }

}
