package com.manu;

import com.manu.day01.Day1;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDay01 {

  @Test
  public void shouldAnswerWithTrue() {
    Day1 day1 = new Day1("""
      1000
      2000
      3000
            
      4000
            
      5000
      6000
            
      7000
      8000
      9000
            
      10000
      """);

    assertTrue(true);
    assertEquals("24000", day1.part1());
    assertEquals("45000", day1.part2());
  }
}
