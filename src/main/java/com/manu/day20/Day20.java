package com.manu.day20;


import com.manu.Puzzle;

import java.util.*;
import java.util.stream.Collectors;

public class Day20 extends Puzzle {

  public Day20(String input) {
    super(input);
  }


  @Override
  public String part1() {
    // using data class as our input to store value and original Index
    LinkedList<Data> list = buildList();

    // keeping the numbers we need to sort in a deque
    Queue<Data> dataQueue = new ArrayDeque<>(list);

    // let's get things moving
    while (!dataQueue.isEmpty()) {
      Data data = dataQueue.poll();
      int startingIndex = list.indexOf(data);
      // move it n positions -> using a mod so n lies between 0 an aux.size
      int n = Math.floorMod(data.value, list.size() - 1);
      list = moveIndex(startingIndex, list, n);
    }

    return prepareAns(list);
  }

  @Override
  public String part2() {
    // using data class as our input to store value and original Index -> multiply value by 811589153
    LinkedList<Data> list = buildList().stream().map(d -> new Data(d.value * 811589153L, d.ogIndex)).collect(Collectors.toCollection(LinkedList::new));
    // here I need an auxList so I can keep list to refill the queue in between iterations
    LinkedList<Data> auxList = new LinkedList<>(list);
    // run it 10 times
    for (int i = 0; i < 10; i++) {
      // let's get things moving
      // keeping the numbers we need to sort in a deque
      Queue<Data> dataQueue = new ArrayDeque<>(list);
      while (!dataQueue.isEmpty()) {
        Data data = dataQueue.poll();
        int startingIndex = auxList.indexOf(data);
        // move it n positions -> using a mod so n lies between 0 an aux.size
        int n = Math.floorMod(data.value, auxList.size() - 1);
        auxList = moveIndex(startingIndex, auxList, n);
      }
    }

    return prepareAns(auxList);
  }

  private LinkedList<Data> buildList() {
    LinkedList<Data> list = new LinkedList<>();
    int i = 0;
    for (String s : getInputLines()) {
      list.add(new Data(Long.parseLong(s), i++));
    }
    return list;
  }


  private String prepareAns(LinkedList<Data> response) {
    // it should return the sum of the 1000th value, the 2000th value and the 3000th value after the 0 (unique)
    int size = response.size();
    Data index0Data = null;
    for (Data d : response) {
      if (d.value == 0) {
        index0Data = d;
      }
    }
    int index0 = response.indexOf(index0Data);
    return String.valueOf(response.get(Math.floorMod(index0 + 1000, size)).value + response.get(Math.floorMod(index0 + 2000, size)).value + response.get(Math.floorMod(index0 + 3000, size)).value);
  }

  private LinkedList<Data> moveIndex(int index, LinkedList<Data> list, int step) {
    if (step == 0) return list;
    LinkedList<Data> outputList = new LinkedList<>(list);

    int newIndex = Math.floorMod(index + step, list.size() - 1);
    Data element = outputList.get(index);
    outputList.remove(index);
    outputList.add(newIndex, element);
    return outputList;
  }


  //creating this data class, so we can hold the value and the originalIndex to know in which order we should sort the numbers
  private record Data(long value, int ogIndex) {
  }


}
