package com.manu.day19;


import com.manu.Puzzle;

import java.util.*;

public class Day19 extends Puzzle {

  public Day19(String input) {
    super(input);
  }

  HashMap<CacheClass, Integer> cacheMap = new HashMap<>();

  @Override
  public String part1() {
    // objective -> to have as many geodes as possible
    // you start with 1 ore collection robot
    // from ore you can get either more ore collection robots or clay collection robots
    // from some ores and a lot of clay you can get obsidian collection robots
    // from ores and obsidian you can build the geode collection robots that you need
    // you can only build one robot at a time
    List<Blueprint> blueprintList = buildBlueprintList();

    int i = 0;
    int totalQualityLevel = 0;
    for (Blueprint blueprint : blueprintList) {
      totalQualityLevel += ++i * getMaxPossibleGeodes(blueprint, new Resources(), 24, new CollectionRobots(1, 0, 0, 0));
      cacheMap = new HashMap<>();
    }
    return String.valueOf(totalQualityLevel);
  }


  @Override
  public String part2() {
    List<Blueprint> blueprintList = buildBlueprintList().subList(0, 3);

    int totalQualityLevel = 1;
    for (Blueprint blueprint : blueprintList) {
      totalQualityLevel *= getMaxPossibleGeodes(blueprint, new Resources(), 32, new CollectionRobots(1, 0, 0, 0));
      cacheMap = new HashMap<>();
    }
    return String.valueOf(totalQualityLevel);
  }


  private List<Blueprint> buildBlueprintList() {
    var blueprintList = new ArrayList<Blueprint>();

    for (String s : this.getInputLines()) {
      blueprintList.add(Blueprint.parse(s));
    }
    return blueprintList;
  }


  private int getMaxPossibleGeodes(Blueprint blueprint, Resources resources, int remainingTime, CollectionRobots collectionRobots) {
    // recursive method, beginning of the turn
    if (remainingTime <= 0) return resources.geodeCount();

    CacheClass cc = new CacheClass(blueprint, resources, remainingTime, collectionRobots);
    if (cacheMap.containsKey(cc)) {
      return cacheMap.get(cc);
    }

    int maxPossibleGeodes = 0;

    Set<WhatToBuild> whatToBuildOptions = whatToBuild(blueprint, resources, collectionRobots);
    // adding the option to wait for the time to finish
    whatToBuildOptions.add(new WhatToBuild(Build.NONE, remainingTime));

    // iterating for every possible path we could take, and we will only keep the path that returns the most geodes
    for (WhatToBuild whatToBuild : whatToBuildOptions) {
      if (whatToBuild.machine == Build.ORE_MACHINE && collectionRobots.oreCollectionRobots == blueprint.getMaxNumberOfOreRobotsNeeded() ||
        whatToBuild.machine == Build.CLAY_MACHINE && collectionRobots.clayCollectionRobots == blueprint.getMaxNumberOfClayRobotsNeeded() ||
        whatToBuild.machine == Build.OBSIDIAN_MACHINE && collectionRobots.obsidianCollectionRobots == blueprint.getMaxNumberOfObsidianRobotsNeeded()
      ) {
        continue;
      }
      if (whatToBuild.minutesToWait > remainingTime) {
        continue;
      }
      if (whatToBuild.minutesToWait == remainingTime && collectionRobots.geodeCollectionRobots == 0) {
        // if you are spending all your time and still have 0 geode machines then there's no point on exploring that path
        // the result will be 0 anyway
        continue;
      }
      maxPossibleGeodes = Math.max(maxPossibleGeodes, getMaxPossibleGeodes(
        blueprint,
        resources.addCycleOfResources(collectionRobots, whatToBuild.minutesToWait).removeCostResources(whatToBuild.machine, blueprint),
        remainingTime - whatToBuild.minutesToWait,
        collectionRobots.addCollectionRobots(whatToBuild.machine)));
    }

    // populating the cache
    cacheMap.put(cc, maxPossibleGeodes);
    return maxPossibleGeodes;
  }


  private Set<WhatToBuild> whatToBuild(Blueprint blueprint, Resources resources, CollectionRobots collectionRobots) {
    // this method is gonna return all possible machines we could build, including the time it would take us to wait for the
    // needed resources and build the machine

    Set<WhatToBuild> whatToBuild = new HashSet<>();

    // can we build a ore collection machine??
    // we need to check if we have the blueprints OR the collection robots and wait for the resources
    if (resources.canWeBuildAnOreCollectionRobot(blueprint)) {
      whatToBuild.add(new WhatToBuild(Build.ORE_MACHINE, 1));
    } else if (collectionRobots.oreCollectionRobots > 0) {
      // we could eventually build it
      int minutesToWait = Math.ceilDiv((blueprint.oreCollectionRobotCost.oreCost - resources.oreCount), collectionRobots.oreCollectionRobots);
      // adding the extra minute to build the machine
      minutesToWait++;
      whatToBuild.add(new WhatToBuild(Build.ORE_MACHINE, minutesToWait));
    }

    if (resources.canWeBuildAClayCollectionRobot(blueprint)) {
      whatToBuild.add(new WhatToBuild(Build.CLAY_MACHINE, 1));
    } else if (collectionRobots.oreCollectionRobots > 0) {
      // we could eventually build it
      int minutesToWait = Math.ceilDiv((blueprint.clayCollectionRobotCost.oreCost - resources.oreCount), collectionRobots.oreCollectionRobots);
      // adding the extra minute to build the machine
      minutesToWait++;
      whatToBuild.add(new WhatToBuild(Build.CLAY_MACHINE, minutesToWait));
    }


    if (resources.canWeBuildAnObsidianCollectionRobot(blueprint)) {
      whatToBuild.add(new WhatToBuild(Build.OBSIDIAN_MACHINE, 1));
    } else if (collectionRobots.oreCollectionRobots > 0 && collectionRobots.clayCollectionRobots > 1) {
      // we could eventually build it
      int minutesToWaitForOre = Math.ceilDiv((blueprint.obsidianCollectionRobotCost.oreCost - resources.oreCount), collectionRobots.oreCollectionRobots);
      int minutesToWaitForClay = Math.ceilDiv((blueprint.obsidianCollectionRobotCost.clayCost - resources.clayCount), collectionRobots.clayCollectionRobots);
      int minutesToWait = Math.max(minutesToWaitForOre, minutesToWaitForClay);

      // adding the extra minute to build the machine
      minutesToWait++;
      whatToBuild.add(new WhatToBuild(Build.OBSIDIAN_MACHINE, minutesToWait));
    }


    if (resources.canWeBuildAGeodeCollectionRobot(blueprint)) {
      whatToBuild.add(new WhatToBuild(Build.GEODE_MACHINE, 1));
    } else if (collectionRobots.oreCollectionRobots > 0 && collectionRobots.obsidianCollectionRobots > 1) {
      // we could eventually build it
      int minutesToWaitForOre = Math.ceilDiv((blueprint.geodeCollectionRobotCost.oreCost - resources.oreCount), collectionRobots.oreCollectionRobots);
      int minutesToWaitForObsidian = Math.ceilDiv((blueprint.geodeCollectionRobotCost.obsidianCost - resources.obsidianCount), collectionRobots.obsidianCollectionRobots);
      int minutesToWait = Math.max(minutesToWaitForOre, minutesToWaitForObsidian);

      // adding the extra minute to build the machine
      minutesToWait++;
      whatToBuild.add(new WhatToBuild(Build.GEODE_MACHINE, minutesToWait));
    }

    return whatToBuild;
  }

  private record CollectionRobots(int oreCollectionRobots,
                                  int clayCollectionRobots,
                                  int obsidianCollectionRobots,
                                  int geodeCollectionRobots) {


    private CollectionRobots addCollectionRobots(int ore, int clay, int obsidian, int geode) {
      return new CollectionRobots(oreCollectionRobots + ore, clayCollectionRobots + clay,
        obsidianCollectionRobots + obsidian, geodeCollectionRobots + geode);
    }

    private CollectionRobots addCollectionRobots(Build machine) {
      return addCollectionRobots((machine == Build.ORE_MACHINE) ? 1 : 0, (machine == Build.CLAY_MACHINE) ? 1 : 0,
        (machine == Build.OBSIDIAN_MACHINE) ? 1 : 0, (machine == Build.GEODE_MACHINE) ? 1 : 0);
    }
  }

  private record Resources(int oreCount, int clayCount, int obsidianCount, int geodeCount) {

    public Resources() {
      this(0, 0, 0, 0);
    }

    private Resources addCycleOfResources(int ore, int clay, int obsidian, int geode, int cycles) {
      return new Resources(oreCount + ore * cycles, clayCount + clay * cycles,
        obsidianCount + obsidian * cycles, geodeCount + geode * cycles);
    }

    private Resources addCycleOfResources(CollectionRobots collectionRobots, int cycles) {
      return addCycleOfResources(collectionRobots.oreCollectionRobots, collectionRobots.clayCollectionRobots,
        collectionRobots.obsidianCollectionRobots, collectionRobots.geodeCollectionRobots, cycles);
    }

    public boolean canWeBuildAnOreCollectionRobot(Blueprint blueprint) {
      return blueprint.oreCollectionRobotCost.oreCost <= this.oreCount;
    }

    public boolean canWeBuildAClayCollectionRobot(Blueprint blueprint) {
      return blueprint.clayCollectionRobotCost.oreCost <= this.oreCount;
    }

    public boolean canWeBuildAnObsidianCollectionRobot(Blueprint blueprint) {
      return blueprint.obsidianCollectionRobotCost.oreCost <= this.oreCount && blueprint.obsidianCollectionRobotCost.clayCost <= this.clayCount;
    }

    public boolean canWeBuildAGeodeCollectionRobot(Blueprint blueprint) {
      return blueprint.geodeCollectionRobotCost.oreCost <= this.oreCount && blueprint.geodeCollectionRobotCost.obsidianCost <= this.obsidianCount;
    }

    public Resources removeCostResources(Build machine, Blueprint blueprint) {
      return switch (machine) {
        case ORE_MACHINE -> this.addCycleOfResources(-blueprint.oreCollectionRobotCost.oreCost, 0, 0, 0, 1);
        case CLAY_MACHINE -> this.addCycleOfResources(-blueprint.clayCollectionRobotCost.oreCost, 0, 0, 0, 1);
        case OBSIDIAN_MACHINE ->
          this.addCycleOfResources(-blueprint.obsidianCollectionRobotCost.oreCost, -blueprint.obsidianCollectionRobotCost.clayCost, 0, 0, 1);
        case GEODE_MACHINE ->
          this.addCycleOfResources(-blueprint.geodeCollectionRobotCost.oreCost, 0, -blueprint.geodeCollectionRobotCost.obsidianCost, 0, 1);
        default -> this;
      };
    }
  }

  private record Blueprint(Cost oreCollectionRobotCost, Cost clayCollectionRobotCost, Cost obsidianCollectionRobotCost,
                           Cost geodeCollectionRobotCost) {

    public static Blueprint parse(String s) {
      String[] words = s.split(" ");

      int oreRobotOreCost = Integer.parseInt(words[6]);

      int clayRobotOreCost = Integer.parseInt(words[12]);

      int obsidianRobotOreCost = Integer.parseInt(words[18]);
      int obsidianRobotClayCost = Integer.parseInt(words[21]);

      int geodeRobotOreCost = Integer.parseInt(words[27]);
      int geodeRobotObsidianCost = Integer.parseInt(words[30]);

      return new Blueprint(
        // ore robot
        new Cost(oreRobotOreCost, 0, 0),
        // clay robot
        new Cost(clayRobotOreCost, 0, 0),
        // obsidian robot
        new Cost(obsidianRobotOreCost, obsidianRobotClayCost, 0),
        // geode robot
        new Cost(geodeRobotOreCost, 0, geodeRobotObsidianCost)
      );
    }

    public int getMaxNumberOfOreRobotsNeeded() {

      return Math.max(Math.max(Math.max(
            oreCollectionRobotCost.oreCost,
            clayCollectionRobotCost.oreCost),
          obsidianCollectionRobotCost.oreCost),
        geodeCollectionRobotCost.oreCost);
    }

    public int getMaxNumberOfClayRobotsNeeded() {
      return obsidianCollectionRobotCost.clayCost;
    }

    public int getMaxNumberOfObsidianRobotsNeeded() {
      return geodeCollectionRobotCost.obsidianCost;
    }
  }

  private record Cost(int oreCost, int clayCost, int obsidianCost) {
  }

  private enum Build {ORE_MACHINE, CLAY_MACHINE, OBSIDIAN_MACHINE, GEODE_MACHINE, NONE}

  private record WhatToBuild(Build machine, int minutesToWait) {
  }

  private record CacheClass(Blueprint blueprint, Resources resources, int remainingTime,
                            CollectionRobots collectionRobots) {
  }
}
