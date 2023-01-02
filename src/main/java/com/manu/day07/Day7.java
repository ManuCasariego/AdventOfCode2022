package com.manu.day07;

import com.manu.Puzzle;

import java.util.ArrayList;
import java.util.List;

public class Day7 extends Puzzle {

  public Day7(String input) {
    super(input);
  }


  @Override
  public String part1() {
    TreeNode root = buildTree();
    addSizesToDirectories(root);

    int sumOfDirectoriesWithAtMostNSize = sumOfDirectoriesWithAtMostNSize(100000, root);
    return Integer.toString(sumOfDirectoriesWithAtMostNSize);
  }

  @Override
  public String part2() {
    TreeNode root = buildTree();
    addSizesToDirectories(root);

    final int TOTAL_DISK_SPACE = 70000000;
    final int SPACE_NEEDED = 30000000;

    // delete the smallest folder that allows the system to install the update --> size of the dir you delete
    TreeNode smallestFolder = getSmallestDirWhichSizeIsBiggerThan(root.size + SPACE_NEEDED - TOTAL_DISK_SPACE, root);
    return Integer.toString(smallestFolder == null ? 0 : smallestFolder.size);
  }

  private TreeNode getSmallestDirWhichSizeIsBiggerThan(int size, TreeNode node) {
    if (node.isFile) {
      return null;
    }
    TreeNode minNode = null;
    int currentMin = Integer.MAX_VALUE;
    if (node.size > size) {
      minNode = node;
      currentMin = node.size;
    }
    for (TreeNode tn : node.nodes) {
      TreeNode aux = getSmallestDirWhichSizeIsBiggerThan(size, tn);
      if (aux != null && aux.size > size && aux.size < currentMin) {
        minNode = aux;
        currentMin = aux.size;
      }
    }
    return minNode;
  }

  private TreeNode buildTree() {
    TreeNode root = new TreeNode().setFile(false).setName("/");

    TreeNode currentNode = root;
    for (String s : getInputLines()) {
      if (s.contains("$")) {
        if (s.contains("cd")) {
          if (s.equals("$ cd /")) {
            // root
            currentNode = root;
          } else if (s.equals("$ cd ..")) {
            // going back one level
            currentNode = currentNode.parent;
          } else {
            // moving to another directory
            currentNode = currentNode.childrenWithName(s.split(" ")[2]);
          }
        }
      } else {
        // response from previous command
        if (s.contains("dir")) {
          // we have a directory in the current node
          TreeNode dir = new TreeNode().setFile(false).setName(s.split(" ")[1]).setParent(currentNode);
          currentNode.nodes.add(dir);
        } else {
          // we have a file
          String[] s1 = s.split(" ");
          TreeNode file = new TreeNode().setName(s1[1]).setFile(true).setParent(currentNode).setSize(Integer.parseInt(s1[0]));
          currentNode.nodes.add(file);
        }
      }
    }
    return root;
  }


  private int sumOfDirectoriesWithAtMostNSize(int limit, TreeNode node) {
    if (node.isFile) {
      return 0;
    }
    int sum = 0;
    for (TreeNode tn : node.nodes) {
      sum += sumOfDirectoriesWithAtMostNSize(limit, tn);
    }
    if (node.size <= limit) {
      return node.size + sum;
    } else return sum;
  }


  private int addSizesToDirectories(TreeNode node) {
    // iterate the tree and add sizes to directories

    if (node.isFile) {
      return node.size;
    }
    int size = 0;
    for (TreeNode tn : node.nodes) {
      size += addSizesToDirectories(tn);
    }
    node.size = size;
    return size;
  }


  public class TreeNode {
    public boolean isFile;
    public int size = 0;
    public List<TreeNode> nodes = new ArrayList<>();
    public String name;
    public TreeNode parent = null;

    public TreeNode setFile(boolean file) {
      isFile = file;
      return this;
    }

    public TreeNode setSize(int size) {
      this.size = size;
      return this;
    }

    public TreeNode setNodes(List<TreeNode> nodes) {
      this.nodes = nodes;
      return this;
    }

    public TreeNode setName(String name) {
      this.name = name;
      return this;
    }

    public TreeNode setParent(TreeNode parent) {
      this.parent = parent;
      return this;
    }

    public TreeNode childrenWithName(String s) {
      for (TreeNode child : this.nodes) {
        if (child.name.equals(s)) {
          return child;
        }
      }
      // not found
      return null;
    }
  }

}
