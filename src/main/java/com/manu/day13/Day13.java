package com.manu.day13;

import com.manu.Puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13 extends Puzzle {

  public Day13(String input) {
    super(input);
  }

  @Override
  public String part1() {
    List<Pair<Packet>> packetPairsList = buildPacketPairsList();
    List<ImprovedBool> results = packetPairsList.stream().map(this::rightOrder).toList();
    int count = countTrues(results);
    return String.valueOf(count);
  }

  @Override
  public String part2() {
    List<Packet> packetList = buildPacketPairsList().stream().flatMap(pair -> Stream.of(pair.left, pair.right)).collect(Collectors.toCollection(ArrayList::new));
    ListPacket dividerPacket1 = ListPacket.parse("[[2]]");
    ListPacket dividerPacket2 = ListPacket.parse("[[6]]");
    packetList.add(dividerPacket1);
    packetList.add(dividerPacket2);
    List<Packet> sortedPacketList = packetList.stream().sorted((o1, o2) -> {
      ImprovedBool rightOrder = rightOrder(o1, o2);
      if (rightOrder == ImprovedBool.TRUE) return -1;
      else if (rightOrder == ImprovedBool.FALSE) return 1;
      else return 0;
    }).toList();
    int product = (sortedPacketList.indexOf(dividerPacket1) + 1) * (sortedPacketList.indexOf(dividerPacket2) + 1);
    return String.valueOf(product);
  }

  private int countTrues(List<ImprovedBool> list) {
    int count = 0;
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i) == ImprovedBool.TRUE) count += i + 1;
    }
    return count;
  }

  private ImprovedBool rightOrder(Pair<Packet> pair) {
    Packet a = pair.left;
    Packet b = pair.right;
    return rightOrder(a, b);
  }

  private ImprovedBool rightOrder(Packet a, Packet b) {
    if (a instanceof ListPacket listA && b instanceof ListPacket listB) {
      return rightOrder(listA, listB);
    } else if (a instanceof ValuePacket valueA && b instanceof ValuePacket valueB) {
      return rightOrder(valueA, valueB);
    } else if (a instanceof ValuePacket valueA && b instanceof ListPacket listB) {
      return rightOrder(new ListPacket(List.of(valueA)), listB);
    } else if (a instanceof ListPacket listA && b instanceof ValuePacket valueB) {
      return rightOrder(listA, new ListPacket(List.of(valueB)));
    }

    return ImprovedBool.CONTINUE;
  }

  private ImprovedBool rightOrder(ValuePacket a, ValuePacket b) {
    if (a.value < b.value) return ImprovedBool.TRUE;
    else if (a.value > b.value) return ImprovedBool.FALSE;
    else return ImprovedBool.CONTINUE;
  }

  private ImprovedBool rightOrder(ListPacket a, ListPacket b) {
    for (int i = 0; i < Math.max(a.packetList.size(), b.packetList.size()); i++) {
      if (i >= a.packetList.size()) {
        // a is smaller
        return ImprovedBool.TRUE;
      } else if (i >= b.packetList.size()) {
        // b is smaller
        return ImprovedBool.FALSE;
      }
      Packet packetA = a.packetList.get(i);
      Packet packetB = b.packetList.get(i);
      ImprovedBool improvedBool = rightOrder(packetA, packetB);
      if (improvedBool == ImprovedBool.TRUE || improvedBool == ImprovedBool.FALSE) return improvedBool;
    }
    return ImprovedBool.CONTINUE;
  }

  private List<Pair<Packet>> buildPacketPairsList() {
    List<Pair<Packet>> packetPairsList = new ArrayList<>();

    for (String pairString : getInput().split("\n\n")) {
      String[] s = pairString.split("\n");
      ListPacket left = ListPacket.parse(s[0]);
      ListPacket right = ListPacket.parse(s[1]);

      packetPairsList.add(new Pair<>(left, right));
    }
    return packetPairsList;
  }

  private interface Packet {
  }

  private record ListPacket(List<Packet> packetList) implements Packet {
    private static ListPacket parse(String s) {
      Stack<Packet> packetStack = new Stack<>();
      ListPacket packetToReturn = null;
      int digit = -1;
      for (Character c : s.toCharArray()) {
        if (c.equals('[')) {
          // create a new packet
          Packet packet = new ListPacket(new ArrayList<>());
          packetStack.push(packet);
        } else if (c.equals(']')) {
          // end of a packet
          Packet packet = packetStack.pop();

          if (digit != -1) {
            ((ListPacket) packet).packetList.add(new ValuePacket(digit));
            digit = -1;
          }
          if (packetStack.isEmpty()) {
            // it was the last packet
            return new ListPacket(((ListPacket) packet).packetList);
          } else {
            ((ListPacket) packetStack.get(packetStack.size() - 1)).packetList.add(packet);
          }
        } else if (Character.isDigit(c)) {
          // continue or start digit reading
          if (digit == -1) {
            digit = 0;
          }
          digit = digit * 10 + Character.getNumericValue(c);
        } else if (c.equals(',')) {
          // if there was a digit reading close it, otherwise do nothing
          if (digit != -1) {
            ((ListPacket) packetStack.get(packetStack.size() - 1)).packetList.add(new ValuePacket(digit));
            digit = -1;
          }

        }

      }
      throw new IllegalArgumentException("the input is wrong");
    }
  }

  private record ValuePacket(int value) implements Packet {
  }


  private record Pair<T>(T left, T right) {
  }

  private enum ImprovedBool {TRUE, FALSE, CONTINUE}
}
