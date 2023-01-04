package com.manu.day16;


import com.manu.Puzzle;

import java.util.*;

public class Day16 extends Puzzle {

  public Day16(String input) {
    super(input);
  }


  @Override
  public String part1() {
    startingNode = buildNodes().get("AA");
    stateMap = new HashMap<>();
    return String.valueOf(getMaxPossibleScore(startingNode, new TreeSet<>(), 30, 0));
  }

  @Override
  public String part2() {
    startingNode = buildNodes().get("AA");
    stateMap = new HashMap<>();
    return String.valueOf(getMaxPossibleScore(startingNode, new TreeSet<>(), 26, 1));
  }

  private Map<Integer, Integer> stateMap = new HashMap<>();
  private Node startingNode = null;


  private int getMaxPossibleScore(Node current, TreeSet<String> openNodes, int remainingTime, int numberOfElephants) {
    if (remainingTime <= 0) {
      return (numberOfElephants > 0) ? getMaxPossibleScore(startingNode, openNodes, 26, numberOfElephants - 1) : 0;
    }
    int stateHash = new State(current.name, openNodes, remainingTime, numberOfElephants).hashCode();
    if (stateMap.containsKey(stateHash)) return stateMap.get(stateHash);

    int maxScore = 0;
    if (!openNodes.contains(current.name) && current.flowRate > 0) {
      // we open the current valve only if it has flowrate to provide
      current.open = true;
      TreeSet<String> openNodesIfWeOpenCurrent = new TreeSet<>(openNodes);
      openNodesIfWeOpenCurrent.add(current.name);
      maxScore += (remainingTime - 1) * current.flowRate;
      maxScore += getMaxPossibleScore(current, openNodesIfWeOpenCurrent, remainingTime - 1, numberOfElephants);
    }
    for (Node connectedNode : current.connectedTo) {
      maxScore = Math.max(maxScore, getMaxPossibleScore(connectedNode, openNodes, remainingTime - 1, numberOfElephants));
    }
    stateMap.put(stateHash, maxScore);
    return maxScore;
  }


  private record State(String current, TreeSet<String> openNodes, int remainingTime, int numberOfElephants) {
    private static final HashMap<String, Integer> nameToIntMap = new HashMap<>();
    private static final HashMap<String, Integer> openableNameToIntMap = new HashMap<>();
    private static int count = 0;
    private static int openableCount = 0;

    @Override
    public int hashCode() {
      // so we have 50 current nodes
      // 15 openable nodes -> 2^15 states
      // remaining time -> 31 states
      // number of elephants -> 2
      // 101_580_800 possible unique hashes
      // probably most of them are unreachable, as you can't open all the valves

      // turning name into an int
      int name;
      if (nameToIntMap.containsKey(this.current)) {
        name = nameToIntMap.get(this.current);
      } else {
        name = count;
        count++;
        nameToIntMap.put(this.current, name);
      }

      // turning the treeset of open nodes into a hash
      int totalOpenNodesHash = 0;
      for (String openableNameStr : this.openNodes) {
        int openableName;
        if (openableNameToIntMap.containsKey(openableNameStr)) {
          openableName = openableNameToIntMap.get(openableNameStr);
        } else {
          openableName = openableCount;
          openableCount++;
          openableNameToIntMap.put(openableNameStr, openableName);
        }
        totalOpenNodesHash += Math.pow(2, openableName);
      }
      return (int) name + remainingTime * 50 + numberOfElephants * 50 * 31 + totalOpenNodesHash * 50 * 31 * 2;
    }
  }

  private Map<String, Node> buildNodes() {
    Map<String, Node> nodes = new HashMap<>();
    for (String s : getInputLines()) {
      Node node = new Node(s);
      nodes.put(node.name, node);
    }
    for (Map.Entry<String, Node> entry : nodes.entrySet()) {
      for (String connectedTo : entry.getValue().connectedToStr) {
        entry.getValue().connectedTo.add(nodes.get(connectedTo));
      }
    }
    return nodes;
  }

  private class Node {
    String name;
    List<Node> connectedTo = new ArrayList<>();
    int flowRate;
    boolean open = false;
    List<String> connectedToStr = new ArrayList<>();

    public Node(String s) {
      s = s.replace("Valve ", "").replace(" has flow rate=", ",").replace("; tunnels lead to valves ", ",").replace("; tunnel leads to valve ", ",");
      String[] s1 = s.split(",");

      this.name = s1[0];
      this.flowRate = Integer.parseInt(s1[1]);
      for (int i = 2; i < s1.length; i++) {
        connectedToStr.add(s1[i].strip());
      }
    }

  }

}