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
      totalQualityLevel += ++i * getMaxPossibleGeodes(blueprint, new Resources(), 24, new CollectionRobots(1, 0, 0, 0));
      System.out.println("blueprint done");
      cacheMap = new HashMap<>();
      otherCacheMap = new HashMap<>();
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

  @Override
  public String part2() {
    return null;
  }

  HashMap<CacheClass, Set<CollectionRobots>> cacheMap = new HashMap<>();
  HashMap<OtherCacheClass, Integer> otherCacheMap = new HashMap<>();

  private int getMaxPossibleGeodes(Blueprint blueprint, Resources resources, int remainingTime, CollectionRobots collectionRobots) {
    // recursive method, beginning of the turn
    if (remainingTime <= 0) return resources.geodeCount();

    int maxNumberOfOreRobotsNeeded = Math.max(Math.max(Math.max(
          blueprint.oreCollectionRobotCost.oreCost,
          blueprint.clayCollectionRobotCost.oreCost),
        blueprint.obsidianCollectionRobotCost.oreCost),
      blueprint.geodeCollectionRobotCost.oreCost);
    int maxNumberOfClayRobotsNeeded = blueprint.obsidianCollectionRobotCost.clayCost;
    int maxNumberOfObsidianRobotsNeeded = blueprint.geodeCollectionRobotCost.obsidianCost;

    OtherCacheClass cc = new OtherCacheClass(blueprint, resources, remainingTime, collectionRobots);
    if (otherCacheMap.containsKey(cc)) {
//      System.out.println("other cache hit");
      return otherCacheMap.get(cc);
    }

    int maxPossibleGeodes = 0;

    // then we need to calculate every possible combination of machines we could build and try every path
    // how to do that??
    for (CollectionRobots possibleCollectionRobots : getPossibleCollectionRobotsWeCouldBuild(blueprint, resources)) {
      if (possibleCollectionRobots.oreCollectionRobots == 1 && collectionRobots.oreCollectionRobots >= maxNumberOfOreRobotsNeeded ||
        possibleCollectionRobots.clayCollectionRobots == 1 && collectionRobots.clayCollectionRobots >= maxNumberOfClayRobotsNeeded ||
        possibleCollectionRobots.obsidianCollectionRobots == 1 && collectionRobots.obsidianCollectionRobots >= maxNumberOfObsidianRobotsNeeded
      ) {
        break;
      }

      maxPossibleGeodes = Math.max(maxPossibleGeodes, getMaxPossibleGeodes(
        blueprint,
        resources.addResources(collectionRobots),
        remainingTime - 1,
        collectionRobots.addCollectionRobots(possibleCollectionRobots)));
    }
    otherCacheMap.put(cc, maxPossibleGeodes);
    return maxPossibleGeodes;
  }

  private record CacheClass(Blueprint blueprint, Resources resources) {
  }

  private record OtherCacheClass(Blueprint blueprint, Resources resources, int remainingTime,
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

    // cache
    CacheClass cc = new CacheClass(blueprint, resources);
    if (cacheMap.containsKey(cc)) {
//      System.out.println("cache hit");
      return cacheMap.get(cc);
    }

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

    cacheMap.put(cc, collectionRobotsSet);
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

    private Resources addResources(int ore, int clay, int obsidian, int geode) {
      return new Resources(oreCount + ore, clayCount + clay,
        obsidianCount + obsidian, geodeCount + geode);
    }

    private Resources addResources(CollectionRobots collectionRobots) {
      return addResources(collectionRobots.oreCollectionRobots, collectionRobots.clayCollectionRobots,
        collectionRobots.obsidianCollectionRobots, collectionRobots.geodeCollectionRobots);
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
  }

  private record Cost(int oreCost, int clayCost, int obsidianCost) {
  }

}
