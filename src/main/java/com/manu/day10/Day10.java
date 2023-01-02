package com.manu.day10;

import com.manu.Puzzle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day10 extends Puzzle {

  public Day10(String input) {
    super(input);
  }

  @Override
  public String part1() {
    List<Instruction> instructions = getInput().lines().map(Instruction::parse).toList();
    ClockCircuit clockCircuit = new ClockCircuit();
    Map<Integer, Integer> cycles = clockCircuit.executeInstructions(instructions);
    int solution = cycles.get(20) * 20 + cycles.get(60) * 60 + cycles.get(100) * 100 + cycles.get(140) * 140 + cycles.get(180) * 180 + cycles.get(220) * 220;
    return String.valueOf(solution);
  }

  @Override
  public String part2() {
    List<Instruction> instructions = getInput().lines().map(Instruction::parse).toList();
    ClockCircuit clockCircuit = new ClockCircuit();
    Map<Integer, Integer> cycles = clockCircuit.executeInstructions(instructions);
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\n");

    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 40; j++) {
        int pixel = j;
        int cycle = pixel + i * 40 + 1;
        int register = cycles.get(cycle);
        if (Math.abs(pixel - register) <= 1) {
          stringBuffer.append("███");
        } else {
          stringBuffer.append("   ");
        }
      }
      stringBuffer.append("\n");
    }
    return stringBuffer.toString();

  }

  public class ClockCircuit {
    int register;
    int cycle;
    Map<Integer, Integer> cycles;

    public ClockCircuit() {
      this.register = 1;
      this.cycle = 1;
      this.cycles = new HashMap<>();
    }

    public Map<Integer, Integer> executeInstructions(List<Instruction> instructions) {
      // todo
      // 20, 60, 100, 140 , 180, 220 cycles
      cycles.put(this.cycle, this.register);
      for (Instruction instruction : instructions) {
        switch (instruction.instruction()) {
          case "noop" -> {
            this.cycle++;
            cycles.put(this.cycle, this.register);
          }
          case "addx" -> {
            this.cycle++;
            cycles.put(this.cycle, this.register);
            this.cycle++;
            this.register += instruction.value;
            cycles.put(this.cycle, this.register);
          }
        }
        ;
      }

      return cycles;
    }


  }

  public record Instruction(String instruction, int value) {
    public int cycles() {
      return switch (this.instruction) {
        case "noop" -> 1;
        case "addx" -> 2;
        default -> 0;
      };
    }

    static Instruction parse(String s) {
      String[] s1 = s.split(" ");
      return (s1.length > 1) ? new Instruction(s1[0], Integer.parseInt(s1[1])) : new Instruction(s1[0], 0);
    }
  }
}
