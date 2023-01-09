package com.manu.day21;


import com.manu.Puzzle;

import java.util.HashMap;
import java.util.Map;

public class Day21 extends Puzzle {

  public Day21(String input) {
    super(input);
  }

  @Override
  public String part1() {
    var monkeyMap = buildMonkeyMap();
    var rootMonkey = monkeyMap.get("root");
    return String.format("%.0f", rootMonkey.operation.operate(monkeyMap));
  }


  @Override
  public String part2() {
    var monkeyMap = buildMonkeyMap();

    // the objective here is to get 0 from the processMonkeyPart2 method

    // adding the new root, now its operation is a subtract
    String rootMonkeyLine = getInput().lines().filter(s -> s.contains("root")).findFirst().orElse("null");
    Monkey rootMonkey = Monkey.parseRootForPart2(rootMonkeyLine);
    monkeyMap.put("root", rootMonkey);


    // first we need to see if it goes up or down when growing the number
    monkeyMap.put("humn", new Monkey("humn", valueOperation(0.0)));
    Double output1 = rootMonkey.operation.operate(monkeyMap);
    monkeyMap.put("humn", new Monkey("humn", valueOperation(100.0)));
    Double output2 = rootMonkey.operation.operate(monkeyMap);
    boolean itsGoingDown = output1 > output2;
    // we are going to use a binary search, searching every single long from min to max, discarding half on every iteration
    // the problem is that depending on the operations you will do, having a high max will mess with the double limits
    Double min = -100_000_000_000_000.0;
    Double max = 100_000_000_000_000.0;
    Double midPoint = min + (max - min) / 2;
    monkeyMap.put("humn", new Monkey("humn", valueOperation(midPoint)));

    Double guess = rootMonkey.operation.operate(monkeyMap);

    while (guess != 0) {
      // do binary search
      if (min.equals(midPoint) || max.equals(midPoint)) return "nope";

      if (guess > 0) {
        if (itsGoingDown) {
          min = midPoint + 1;
        } else {
          max = midPoint - 1;
        }
      } else {
        if (itsGoingDown) {
          max = midPoint - 1;
        } else {
          min = midPoint + 1;
        }
      }
      midPoint = min + (max - min) / 2;
      monkeyMap.put("humn", new Monkey("humn", valueOperation(midPoint)));

      guess = processMonkeyPart2(monkeyMap.get("root"), monkeyMap, midPoint);
    }

    return String.format("%.0f", midPoint);
  }

  private Operation valueOperation(Double d) {
    return map -> d;
  }

  private Double processMonkeyPart2(Monkey m, HashMap<String, Monkey> monkeyMap, Double humnNo) {
    if (m.name.equals("humn")) return humnNo;
    else return m.operation().operate(monkeyMap);
  }


  private HashMap<String, Monkey> buildMonkeyMap() {
    var monkeyMap = new HashMap<String, Monkey>();
    for (String s : getInputLines()) {
      Monkey m = Monkey.parse(s);
      monkeyMap.put(m.name, m);
    }
    return monkeyMap;
  }

  private record Monkey(String name, Operation operation) {

    public static Monkey parse(String s) {
      String[] words = s.split(" ");
      String name = words[0].replace(":", "");
      if (words.length == 2) {
        // value monkey
        return new Monkey(name, map -> Double.parseDouble(words[1]));
      } else {
        // operation monkey
        Operation op = switch (words[2]) {
          case "+" -> map -> map.get(words[1]).operation.operate(map) + map.get(words[3]).operation.operate(map);
          case "-" -> map -> map.get(words[1]).operation.operate(map) - map.get(words[3]).operation.operate(map);
          case "*" -> map -> map.get(words[1]).operation.operate(map) * map.get(words[3]).operation.operate(map);
          case "/" -> map -> map.get(words[1]).operation.operate(map) / map.get(words[3]).operation.operate(map);
          default -> throw new RuntimeException("unknown operation");
        };
        return new Monkey(name, op);
      }
    }

    public static Monkey parseRootForPart2(String s) {
      String[] words = s.split(" ");
      String name = words[0].replace(":", "");
      return new Monkey(name, map -> map.get(words[1]).operation.operate(map) - map.get(words[3]).operation.operate(map));

    }

  }

  private interface Operation {
    public Double operate(Map<String, Monkey> monkeyMap);
  }

}
