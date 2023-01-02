package com.manu.day11;

import com.manu.Puzzle;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Day11 extends Puzzle {

  public Day11(String input) {
    super(input);
  }


  @Override
  public String part1() {
    List<Monkey> monkeys = buildMonkeys();
    return executeRounds(monkeys, 20, true);
  }

  @Override
  public String part2() {
    List<Monkey> monkeys = buildMonkeys();
    return executeRounds(monkeys, 10000, false);
  }

  private int multiply(List<Integer> numbers) {
    int multiply = 1;
    for (int number : numbers) {
      multiply = multiply * number;
    }
    return multiply;
  }

  private List<Integer> getDenominators(List<Monkey> monkeys) {
    Set<Integer> denominators = new HashSet<>();
    for (Monkey monkey : monkeys) {
      denominators.add(monkey.test.divisibleBy);
    }
    return denominators.stream().toList();
  }

  private String executeRounds(List<Monkey> monkeys, int rounds, boolean divideByThree) {
    List<Integer> denominators = getDenominators(monkeys);
    int leastCommonMultiple = multiply(denominators);
    long[] inspections = new long[monkeys.size()];
    for (int i = 0; i < rounds; i++) {
      for (int j = 0; j < monkeys.size(); j++) {
        Monkey monkey = monkeys.get(j);
        for (Long item : monkey.items) {
          inspections[j]++;
          long itemToGive = monkey.operation.performOperation(item);
          if (divideByThree) itemToGive = Math.floorDiv(itemToGive, 3);
          itemToGive = itemToGive % leastCommonMultiple;
          int monkeyToGiveTheItem = monkey.test.performOperationMonkey(itemToGive);
          monkeys.get(monkeyToGiveTheItem).items.add(itemToGive);
        }
        // empty the monkeys' items array
        monkey.items = new ArrayList<>();
      }
    }
    // get the two most active monkeys and multiply their no of inspections
    inspections = Arrays.stream(inspections).sorted().toArray();
    return String.valueOf(inspections[inspections.length - 1] * inspections[inspections.length - 2]);
  }

  private List<Monkey> buildMonkeys() {
    List<Monkey> monkeys = new ArrayList<>();
    for (String monkeyString : getInput().split("\n\n")) {
      monkeys.add(Monkey.parse(monkeyString));
    }
    return monkeys;
  }

  public static class Monkey {

    List<Long> items;
    Operation operation;
    TestOperation test;

    public Monkey(List<Long> items, Operation operation, TestOperation test) {
      this.items = items;
      this.operation = operation;
      this.test = test;
    }

    public static Monkey parse(String monkeyString) {
      List<Long> items = new ArrayList<>();
      var splitMonkeyString = monkeyString.split("\n");
      // items
      Pattern p = Pattern.compile("\\d+");
      Matcher m1 = p.matcher(splitMonkeyString[1]);
      while (m1.find()) {
        items.add(Long.parseLong(m1.group()));
      }
      // operation

      Matcher m2 = p.matcher(splitMonkeyString[2]);
      int operationValue = (m2.find()) ? Integer.parseInt(m2.group()) : 0;
      Operation.MathOperation mathOperation =
        (splitMonkeyString[2].contains("+")) ? Operation.MathOperation.ADDITION : (operationValue == 0) ?
          Operation.MathOperation.SQUARE : Operation.MathOperation.MULTIPLICATION;
      Operation operation = new Operation(mathOperation, operationValue);

      Matcher m3 = p.matcher(splitMonkeyString[3]);
      Matcher m4 = p.matcher(splitMonkeyString[4]);
      Matcher m5 = p.matcher(splitMonkeyString[5]);

      int divisibleBy = (m3.find()) ? Integer.parseInt(m3.group()) : 0;
      int throwToMonkeyIfTrue = (m4.find()) ? Integer.parseInt(m4.group()) : 0;
      int throwToMonkeyIfFalse = (m5.find()) ? Integer.parseInt(m5.group()) : 0;

      TestOperation test = new TestOperation(divisibleBy, throwToMonkeyIfTrue, throwToMonkeyIfFalse);

      return new Monkey(items, operation, test);
    }
  }

  public record Operation(MathOperation mathOperation, int value) {

    public long performOperation(long x) {
      return switch (mathOperation) {
        case ADDITION -> x + value;
        case MULTIPLICATION -> x * value;
        case SQUARE -> x * x;
        default -> x;
      };
    }

    enum MathOperation {ADDITION, MULTIPLICATION, SQUARE;}
  }

  public record TestOperation(int divisibleBy, int throwToMonkeyIfTrue, int throwToMonkeyIfFalse) {

    public int performOperationMonkey(long x) {
      // returns the number of the monkey it'll throw the object to
      if (x % divisibleBy == 0) return throwToMonkeyIfTrue;
      else return throwToMonkeyIfFalse;
    }
  }

}
