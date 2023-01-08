package com.manu.day19;


import com.manu.Puzzle;

import java.util.*;

public class Day19 extends Puzzle {

  public Day19(String input) {
    super(input);
  }


  @Override
  public String part1() {
    // objective -> to have as many geodes as possible
    // you start with 1 ore collection robot
    // from ore you can get either more ore collection robots or clay collection robots
    // from same ores and a lot of clay you can get obsidian collection robots
    // from ores and obsidian you can build the geode collection robots that you need

    // so it makes sense that you almost always prioritize clay collection, but let's try to make an algorithm that
    // calculates every possible path, and then we'll choose the path that got the most geodes
    List<Blueprint> blueprintList = buildBlueprintList();

    int i = 0;
    int totalQualityLevel = 0;
    for (Blueprint blueprint : blueprintList) {
//      totalQualityLevel += ++i * getMaxPossibleGeodes(blueprint, new Resources(), 24, new CollectionRobots(1, 0, 0, 0));
      System.out.println("blueprint done");
      cacheMap = new HashMap<>();
    }
    return String.valueOf(totalQualityLevel);
  }


  @Override
  public String part2() {
    List<Blueprint> blueprintList = buildBlueprintList().subList(0,3);

    int i = 0;
    int totalQualityLevel = 1;
    for (Blueprint blueprint : blueprintList) {
      totalQualityLevel *=  getMaxPossibleGeodes(blueprint, new Resources(), 32, new CollectionRobots(1, 0, 0, 0));
      System.out.println("blueprint done");
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
  HashMap<CacheClass, Integer> cacheMap = new HashMap<>();

  private int getMaxPossibleGeodes(Blueprint blueprint, Resources resources, int remainingTime, CollectionRobots collectionRobots) {
    // recursive method, beginning of the turn
    if (remainingTime <= 0) return resources.geodeCount();

    CacheClass cc = new CacheClass(blueprint, resources, remainingTime, collectionRobots);
    if (cacheMap.containsKey(cc)) {
//      System.out.println("cache hit");
      return cacheMap.get(cc);
    }

    int maxPossibleGeodes = 0;

    // then we need to calculate every possible combination of machines we could build and try every path
    // how to do that??
    for (CollectionRobots possibleCollectionRobots : getPossibleCollectionRobotsWeCouldBuild(blueprint, resources)) {
      if (possibleCollectionRobots.oreCollectionRobots == 1 && collectionRobots.oreCollectionRobots == blueprint.getMaxNumberOfOreRobotsNeeded() ||
        possibleCollectionRobots.clayCollectionRobots == 1 && collectionRobots.clayCollectionRobots == blueprint.getMaxNumberOfClayRobotsNeeded() ||
        possibleCollectionRobots.obsidianCollectionRobots == 1 && collectionRobots.obsidianCollectionRobots == blueprint.getMaxNumberOfObsidianRobotsNeeded()
      ) {
        continue;
      }
      maxPossibleGeodes = Math.max(maxPossibleGeodes, getMaxPossibleGeodes(
        blueprint,
        resources.addCycleOfResources(collectionRobots, 1).removeCostResources(possibleCollectionRobots, blueprint),
        remainingTime - 1,
        collectionRobots.addCollectionRobots(possibleCollectionRobots)));
    }
    cacheMap.put(cc, maxPossibleGeodes);
    return maxPossibleGeodes;
  }


  private record CacheClass(Blueprint blueprint, Resources resources, int remainingTime,
                            CollectionRobots collectionRobots) {
  }

  /**
   * you can only build one robot at a time
   *
   * @param blueprint
   * @param resources
   * @return
   */
  private Set<CollectionRobots> getPossibleCollectionRobotsWeCouldBuild(Blueprint blueprint, Resources resources) {

    Set<CollectionRobots> collectionRobotsSet = new HashSet<>();
    // create nothing
    collectionRobotsSet.add(new CollectionRobots(0, 0, 0, 0));


    // can we create an ore collection robot?
    if (resources.canWeBuildAnOreCollectionRobot(blueprint)) {
      collectionRobotsSet.add(new CollectionRobots(1, 0, 0, 0));
    }

    // can we create a clay collection robot?
    if (resources.canWeBuildAClayCollectionRobot(blueprint)) {
      collectionRobotsSet.add(new CollectionRobots(0, 1, 0, 0));
    }

    // can we create an obsidian collection robot?
    if (resources.canWeBuildAnObsidianCollectionRobot(blueprint)) {
      collectionRobotsSet.add(new CollectionRobots(0, 0, 1, 0));
    }

    // can we create a geode collection robot?
    if (resources.canWeBuildAGeodeCollectionRobot(blueprint)) {
      // if we can create a geode collection robot then we do it - it's always worth it
      collectionRobotsSet = new HashSet<>();
      collectionRobotsSet.add(new CollectionRobots(0, 0, 0, 1));
    }

    return collectionRobotsSet;
  }

  private record CollectionRobots(int oreCollectionRobots,
                                  int clayCollectionRobots,
                                  int obsidianCollectionRobots,
                                  int geodeCollectionRobots) {


    private CollectionRobots addCollectionRobots(int ore, int clay, int obsidian, int geode) {
      return new CollectionRobots(oreCollectionRobots + ore, clayCollectionRobots + clay,
        obsidianCollectionRobots + obsidian, geodeCollectionRobots + geode);
    }

    private CollectionRobots addCollectionRobots(CollectionRobots collectionRobots) {
      return addCollectionRobots(collectionRobots.oreCollectionRobots, collectionRobots.clayCollectionRobots,
        collectionRobots.obsidianCollectionRobots, collectionRobots.geodeCollectionRobots);
    }
  }

  private record Resources(int oreCount, int clayCount, int obsidianCount, int geodeCount) {

    public Resources() {
      this(0, 0, 0, 0);
    }

    private Resources addCycleOfResources(int ore, int clay, int obsidian, int geode, int cycles) {
      return new Resources(oreCount + ore * cycles, clayCount + clay * cycles,
        obsidianCount + obsidian* cycles, geodeCount + geode * cycles);
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

    public Resources removeCostResources(CollectionRobots possibleCollectionRobots, Blueprint blueprint) {
      if (possibleCollectionRobots.oreCollectionRobots == 1) {
        return this.addCycleOfResources(-blueprint.oreCollectionRobotCost.oreCost, 0, 0, 0, 1);
      } else if (possibleCollectionRobots.clayCollectionRobots == 1) {
        return this.addCycleOfResources(-blueprint.clayCollectionRobotCost.oreCost, 0, 0, 0, 1);
      } else if (possibleCollectionRobots.obsidianCollectionRobots == 1) {
        return this.addCycleOfResources(-blueprint.obsidianCollectionRobotCost.oreCost, -blueprint.obsidianCollectionRobotCost.clayCost, 0, 0, 1);
      } else if (possibleCollectionRobots.geodeCollectionRobots == 1) {
        return this.addCycleOfResources(-blueprint.geodeCollectionRobotCost.oreCost, 0, -blueprint.geodeCollectionRobotCost.obsidianCost, 0, 1);
      }
      return this;
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

}
