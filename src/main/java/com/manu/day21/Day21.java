package com.manu.day21;


import com.manu.Puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day21 extends Puzzle {

  public Day21(String input) {
    super(input);
  }

  @Override
  public String part1() {
    var monkeyMap = buildMonkeyMap();
    return String.format("%.0f", monkeyMap.get("root").operate(monkeyMap));
  }


  @Override
  public String part2() {
    var monkeyMap = buildMonkeyMap();

    // now root monkey operation is a == , so you need both sides of the operator to return the same value
    // they ask for the value that the monkey "humn" (you) needs to shout
    // let's start from root and evaluate all the way to humn doing reverse operations and taking order of operations
    // into account since the order of the values affect the result for - and /
    var rootMonkey = monkeyMap.get("root");
    return String.format("%.0f", recurseMonkeyTree(rootMonkey, null, monkeyMap));
  }

  private Double recurseMonkeyTree(Monkey currentMonkey, Double currentVal, Map<String, Monkey> monkeyMap) {
    if (currentMonkey.name.equals("humn")) return currentVal;
    Boolean isHumnOnTheLeft = isHumnUnderThisMonkey(monkeyMap.get(currentMonkey.operationMonkeys.get(0)), monkeyMap);
    if (currentMonkey.name.equals("root")) {
      if (isHumnOnTheLeft) {
        //get right side
        currentVal = monkeyMap.get(currentMonkey.operationMonkeys.get(1)).operate(monkeyMap);
        currentMonkey = monkeyMap.get(currentMonkey.operationMonkeys.get(0));
      } else {
        currentVal = monkeyMap.get(currentMonkey.operationMonkeys.get(0)).operate(monkeyMap);
        currentMonkey = monkeyMap.get(currentMonkey.operationMonkeys.get(1));
      }
      return recurseMonkeyTree(currentMonkey, currentVal, monkeyMap);
    }

    Double valWhereHumnIsNot;

    if (isHumnOnTheLeft) {
      //get value from the right side
      valWhereHumnIsNot = monkeyMap.get(currentMonkey.operationMonkeys.get(1)).operate(monkeyMap);
      Monkey.Operation currentMonkeyOp = currentMonkey.operation;

      // perform operation
      switch (currentMonkeyOp) {
        case SUM -> currentVal -= valWhereHumnIsNot;
        case SUB -> currentVal += valWhereHumnIsNot;
        case MULTIPLICATION -> currentVal /= valWhereHumnIsNot;
        case DIVISION -> currentVal *= valWhereHumnIsNot;
      }
      // move to next monkey
      currentMonkey = monkeyMap.get(currentMonkey.operationMonkeys.get(0));
    } else {
      //get value from the left side
      valWhereHumnIsNot = monkeyMap.get(currentMonkey.operationMonkeys.get(0)).operate(monkeyMap);
      Monkey.Operation currentMonkeyOp = currentMonkey.operation;

      // perform operation
      switch (currentMonkeyOp) {
        case SUM -> currentVal -= valWhereHumnIsNot;
        case SUB -> currentVal = valWhereHumnIsNot - currentVal;
        case MULTIPLICATION -> currentVal /= valWhereHumnIsNot;
        case DIVISION -> currentVal = valWhereHumnIsNot / currentVal;
      }
      // move to next monkey
      currentMonkey = monkeyMap.get(currentMonkey.operationMonkeys.get(1));
    }
    // recurse
    return recurseMonkeyTree(currentMonkey, currentVal, monkeyMap);
  }

  private Map<String, Monkey> buildMonkeyMap() {
    var monkeyMap = new HashMap<String, Monkey>();
    for (String s : getInputLines()) {
      Monkey m = Monkey.parse(s);
      monkeyMap.put(m.name, m);
    }
    return monkeyMap;
  }

  private Boolean isHumnUnderThisMonkey(Monkey m, Map<String, Monkey> monkeyMap) {
    if (m.name().equals("humn")) return true;
    if (m.operation() == Monkey.Operation.NOTHING) return false;

    return isHumnUnderThisMonkey(monkeyMap.get(m.operationMonkeys().get(0)), monkeyMap) || isHumnUnderThisMonkey(monkeyMap.get(m.operationMonkeys().get(1)), monkeyMap);
  }

  private record Monkey(List<String> operationMonkeys, Operation operation, Double value, String name) {

    private Double operate(Map<String, Monkey> monkeyMap) {
      return switch (operation) {
        case SUM ->
          monkeyMap.get(this.operationMonkeys.get(0)).operate(monkeyMap) + monkeyMap.get(this.operationMonkeys.get(1)).operate(monkeyMap);
        case SUB ->
          monkeyMap.get(this.operationMonkeys.get(0)).operate(monkeyMap) - monkeyMap.get(this.operationMonkeys.get(1)).operate(monkeyMap);
        case MULTIPLICATION ->
          monkeyMap.get(this.operationMonkeys.get(0)).operate(monkeyMap) * monkeyMap.get(this.operationMonkeys.get(1)).operate(monkeyMap);
        case DIVISION ->
          monkeyMap.get(this.operationMonkeys.get(0)).operate(monkeyMap) / monkeyMap.get(this.operationMonkeys.get(1)).operate(monkeyMap);
        case NOTHING -> this.value;
      };
    }

    public static Monkey parse(String s) {
      String[] words = s.split(" ");
      String name = words[0].replace(":", "");
      if (words.length == 2) {
        return new Monkey(new ArrayList<>(), Operation.NOTHING, Double.parseDouble(words[1]), name);
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
        return new Monkey(operationMonkeys, op, 0.0, name);
      }
    }

    private enum Operation {
      MULTIPLICATION, DIVISION, SUM, SUB, NOTHING
    }

  }
}
