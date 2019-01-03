import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * <h1>Family Tree Application</h1>
 * This program creates a family tree object, and allows the user to perform operations on it such as finding the shortest path between people, adding new families, and checking membership.
 * @author argonboron - Corrie McGregor
 * @version 1.0
 * @since 2018-12-23
 */
public class FamilyTree {
  private static Tree tree;
  private static ArrayList<Person> path = new ArrayList<>();
  private static String via;

  /**
   * Writes new family to the raw data csv file.
   * @param family The family to be written.
   * @param filePath Filepath of the csv.
   */
  private static void writeToCSV(Family family, String filePath){
    try {
      FileWriter writer = new FileWriter(filePath, true);
      StringBuilder writeString = new StringBuilder("\n");
      for (Person person: family.getParents()) {
        writeString.append(person.getName()).append(",");
      }
      writeString.append("@,");
      for (Person person: family.getChildren()) {
        writeString.append(person.getName()).append(",");
      }
      writeString.append(Integer.toString(family.getYear())).append(",");
      if (family.hasMarriage()) {
        writeString.append("+,");
      }
      writeString.append(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,");
      writer.write(writeString.toString());
      writer.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the details of the family from the user and passes it to the tree.
   * @param scanner the System.in scanner.
   * @return The family that was added.
   */
  private static Family addFamily(Scanner scanner) {
    boolean married = false;
    System.out.println("Enter Parents name(s)");
    String[] parentNames = scanner.nextLine().split(", ");
    System.out.println("Enter Childrens name(s) if any");
    String[] childrenNames= scanner.nextLine().split(", ");
    if (childrenNames[0].equals("")) {
      childrenNames = null;
    }
    System.out.println("What year was this family formed?");
    String year = scanner.nextLine();
    if (parentNames.length > 1) {
      System.out.println("Are the parents married? (yes/no()");
      married = scanner.nextLine().equals("yes");
    }
    return tree.addFamilyToTree(parentNames, childrenNames, year, married);
  }

  /**
   * Calls the shortest path algorithm and prints the path between two nodes.
   * @param nameOne Name of source person.
   * @param nameTwo Name of person being searched for.
   */
  private static void findShortestPath(String nameOne, String nameTwo) {
    try {
      HashMap<String, Person> people = tree.getPeople();
      if (people.containsKey(nameOne) && people.containsKey(nameTwo)) {
          getPath(people.get(nameOne), people.get(nameTwo));
          System.out.println("\n" + nameOne + " --> " + nameTwo);
          System.out.println("Distance: " + people.get(nameTwo).getDistance() + "\n");
          if (people.get(nameTwo).getDistance() > 0) {
              for (int i = 0; i < people.get(nameTwo).getDistance() + 1; i++) {
                  if (via != null) {
                      System.out.print(path.get(i).getName());
                      System.out.println(via);
                      via = null;
                  } else {
                      if (i == 0) {
                          System.out.print(" - ");
                      }
                      System.out.print(path.get(i).getName());
                      if (i != 0) {
                          System.out.println();
                      }
                  }
                  if (i < people.get(nameTwo).getDistance()) {
                      if (i > 0) {
                          System.out.print(" - " + path.get(i).getName());
                      }
                      System.out.print(getRelationship(path.get(i), path.get(i + 1)));
                  }
              }
              System.out.println();
          }
          path.clear();
          tree.clearDistances();
      } else {
        System.out.println("bad input");
      }
    } catch (InterruptedException e ) {
      e.printStackTrace();
    }
  }

  /**
   * Uses a modified breadth first search to find the shortest path between nodes and store the path between them. Djickstra's Algorithm.
   * @param sourcePerson Source person.
   * @param searchPerson Person being searched for.
   * @throws InterruptedException Interrupted Exception
   */
  private static void getPath(Person sourcePerson, Person searchPerson) throws InterruptedException {
    ArrayList<Person> visited = new ArrayList<>();
    HashMap<Person, Person> predecessorMap = new HashMap<>();
    ArrayBlockingQueue<Person> queue = new ArrayBlockingQueue<>(tree.getPeople().size() * 500);
    ArrayList<Integer> famIDs;
    boolean found = false;

    visited.add(sourcePerson);
    queue.add(sourcePerson);
    sourcePerson.setDistance(0);

    while(!queue.isEmpty()) {
      Person current = queue.remove();
      famIDs = current.getFamilyIDs();

      for(Integer id: famIDs) {
        Family family = tree.getFamily(id);
        for(Person person : family.getAllMembers()) {
          if (!visited.contains(person)) {
            if(!person.getName().equals(current.getName())){
              visited.add(person);
              person.setDistance(current.getDistance()+1);
              predecessorMap.put(person, current);
              queue.add(person);
              if (person.getName().equals(searchPerson.getName())) {
                found = true;
                break;
              }
            }
          }
        }
      }
    }
    if (found) {
      path.add(searchPerson);
      Person currentPath = searchPerson;

      for(int i = 0; i < searchPerson.getDistance(); i++) {
        path.add(predecessorMap.get(currentPath));
        currentPath = predecessorMap.get(currentPath);
      }
      Collections.reverse(path);
    } else {
      System.out.println("No path between these people");
    }
  }

  /**
   * Gets the relationship between two people and returns it.
   * @param personOne person one.
   * @param personTwo person two.
   * @return relationship between the people.
   */
  private static String getRelationship(Person personOne, Person personTwo) {
    ArrayList<Integer> personOneFamilies = personOne.getFamilyIDs();
    ArrayList<Integer> personTwoFamilies = personTwo.getFamilyIDs();
    String personOneRole;
    String personTwoRole;
    int commonFam = -1;
    for (Integer id: personOneFamilies) {
      for (Integer personTwoFamily : personTwoFamilies) {
        if (Objects.equals(id, personTwoFamily)) {
          commonFam = id;
          break;
        }
      }
      if (commonFam >= 0) {
        break;
      }
    }
    Family family = tree.getFamily(commonFam);
    personOneRole = (family.getChildren().contains(personOne)) ? "child" : "parent";
    personTwoRole = (family.getChildren().contains(personTwo)) ? "child" : "parent";
    if (personOneRole.equals("child")) {
      switch (personTwoRole) {
        case "child":
            StringBuilder parentNames = new StringBuilder();
            ArrayList<Person> parents = family.getParents();
            for (int i = 0; i < parents.size(); i++) {
                parentNames.append(parents.get(i).getName());
                if (parents.size() > 0 && i < parents.size()-1) {
                    parentNames.append(", ");
                }
            }
          via = " (via " + parentNames + ")";
          return " is siblings with ";
        case "parent":
          return " is a child of ";
      }
    } else {
      switch (personTwoRole) {
        case "child":
          return " is a parent of ";
        case "parent":
        if (family.hasMarriage()) {
          return " is married to ";
        } else {
          return " adopted with ";
        }
      }
    }
    return null;
  }

  /**
   * Lists all the children of the person given if any;
   * @param parentName The name of the parent.
   */
  private static void listChildren(String parentName) {
    for(Integer id: tree.getPeople().get(parentName).getFamilyIDs()) {
      Family family = tree.getFamilies().get(id);
      if (family.getParents().contains(tree.getPeople().get(parentName)) && family.getChildren().size() > 0) {
        if (family.getParents().size() > 1) {
        System.out.print("\nChildren:\nwith ");
          for (Person parent: family.getParents()) {
            if (!parent.getName().equals(parentName)) {
              System.out.print(parent.getName() + " ");
            }
          }
        } else {
          System.out.print("\nAs a single parent");
        }
        System.out.println(":");
        for (Person child: family.getChildren()) {
          System.out.println(child.getName());
        }
      }
    }
    System.out.println();
  }

  /**
   * Finds the number of descendants of a given person
   * @param families the list of families that person is a parent in.
   * @param generation how many generations deep the recursion has run.
   */
  private static void getGenerations(Person person, ArrayList<Family> families, int generation, boolean print) {
    int children = 0;
    StringBuilder generationLabel = new StringBuilder("Number of grandchildren: ");
    ArrayList<Family> newFamilies = new ArrayList<>();
    for(Family family: families) {
      for(Person child: family.getChildren()) {
        if (isParent(child)) {
          for(Integer id: child.getFamilyIDs()) {
            if (isFamilyParent(child, tree.getFamilies().get(id)) && tree.getFamilies().get(id).getChildren().size() > 0) {
              children += tree.getFamilies().get(id).getChildren().size();
              newFamilies.add(tree.getFamilies().get(id));
            }
          }
        }
      }
    }
    if (children > 0) {
      switch(generation) {
        case 0:
          if (print) {
              System.out.println(generationLabel.toString() + children);
          }
          if (newFamilies.size() > 0) {
            generation++;
            person.addToDescendants(children);
            getGenerations(person, newFamilies, generation, print);
          }
          break;
        case 1:
          generationLabel = new StringBuilder("great grandchildren: ");
          if (print) {
              System.out.println("Number of " + generationLabel + children);
          }
          if (newFamilies.size() > 0) {
            generation++;
            person.addToDescendants(children);
            getGenerations(person, newFamilies, generation, print);
          }
          break;
        default:
          if (generation > 1) {
            generationLabel = new StringBuilder("great grandchildren: ");
            for (int i = 0; i < generation-1; i ++) {
              generationLabel.insert(0, "great-");
            }
            if (print) {
                System.out.println("Number of " + generationLabel + children);
            }
            if (newFamilies.size() > 0) {
              generation++;
              person.addToDescendants(children);
              getGenerations(person, newFamilies, generation, print);
            }
          } else {
            System.out.println("error");
          }
          break;
      }
    }
  }

  /**
   * Returns whether a given person is a parent of the given family.
   * @param person to be checked.
   * @param family to check in.
   * @return true if person is a parent.
   */
  private static boolean isFamilyParent(Person person, Family family) {
    if (person.getFamilyIDs().contains(family.getID())){
      if (family.getParents().contains(person)) {
          return true;
      }
    }
    return false;
  }

  /**
   * Returns whether a given person is a parent of any family.
   * @param person to be checked.
   * @return true if person is a parent.
   */
  private static boolean isParent(Person person) {
    for (Integer id: person.getFamilyIDs()){
      Family family = tree.getFamilies().get(id);
      if (family.getParents().contains(person)) {
          return true;
      }
    }
    return false;
  }

  /**
   * Prints all the parents associated with the given person.
   * @param person to look for the parents for.
   */
  private static void printParents(Person person) {
      ArrayList<Person> parents = new ArrayList<>();
      boolean printed = false;
      System.out.println("\nParents:");
      for(Integer id: person.getFamilyIDs()) {
          if (tree.getFamilies().get(id).getChildren().contains(person)) {
              System.out.println("--");
              for(Person parent: tree.getFamilies().get(id).getParents()) {
                  if(!parents.contains(parent)) {
                      parents.add(parent);
                      printed = true;
                      System.out.println(parent.getName());
                  }
              }
              System.out.println();
          }
      }
      if (!printed) {
          System.out.println("??");
      }
      parents.clear();
  }

  private static ArrayList<Integer> getMostKids() {
      int kidCount = 0;
      ArrayList<Integer> kidNums = new ArrayList<>();
      for (Entry<String, Person> pair : tree.getPeople().entrySet()) {
          Person person = pair.getValue();
          for (Integer id : person.getFamilyIDs()) {
              if (tree.getFamilies().get(id).getParents().contains(person)) {
                  kidCount += tree.getFamilies().get(id).getChildren().size();
              }
          }
          pair.getValue().setNumberOfChildren(kidCount);
          kidNums.add(kidCount);
          kidCount = 0;
      }
      return kidNums;
  }

  /**
   * Prints all the siblings of the given person.
   * @param person to check the siblings of.
   */
  private static void printSiblings(Person person) {
      for(Integer id: person.getFamilyIDs()) {
          if (tree.getFamilies().get(id).getChildren().contains(person) && tree.getFamilies().get(id).getChildren().size() > 1) {
              System.out.print("\nSiblings:\n(with parents: ");
              for(Person eachParent: tree.getFamilies().get(id).getParents()) {
                  System.out.print(eachParent.getName() + " ");
              }
              System.out.println(")");
              for(Person sibling: tree.getFamilies().get(id).getChildren()) {
                  if (!sibling.getName().equals(person.getName())) {
                      System.out.println(sibling.getName());
                  }
              }
          }
      }
  }

  private static void printRankings(ArrayList<Integer> rankData, int length, boolean descendants, String name) {
      ArrayList<Person> topPeople = new ArrayList<>();
      Collections.sort(rankData);
      Collections.reverse(rankData);
      for (int i = 0; i < length; i++) {
          int currentNumber = rankData.get(i);
          for (Entry<String, Person> pair : tree.getPeople().entrySet()) {
              int comparator = descendants ? pair.getValue().getNumberOfDescendants() : pair.getValue().getNumberOfChildren();
              String printString = descendants ? " descendants" : " kids";
              if (comparator == currentNumber && (!topPeople.contains(pair.getValue()))) {
                  topPeople.add(pair.getValue());
                  if (name == null || pair.getValue().getName().equals(name)) {
                      System.out.println(i+1 + ". " + pair.getValue().getName() + "  " + rankData.get(i) + printString);
                      if (name != null) {
                          System.out.println();
                        return;
                      }
                  }
                  System.out.println();
                  break;
              }
          }
      }
  }

  private static boolean isValid(String[] inputArray, int parameters, boolean numeric) {
      return inputArray.length == parameters && (!numeric || tree.isNumeric(inputArray[1]));
  }

  private static void getRankings(String name) {
      if (tree.getPeople().containsKey(name)) {
          printRankings(getMostKids(), tree.getPeople().size(), false, name);
          printRankings(getDescendants(), tree.getPeople().size(), true, name);
      } else {
          System.out.println("invalid name");
      }
  }

  private static void getDetails(String name) {
      if (tree.getPeople().containsKey(name)) {
          Person person = tree.getPeople().get(name);
          System.out.println(person.getName());
          printParents(person);
          printSiblings(person);
          listChildren(person.getName());
      } else {
          System.out.println("name not found");
      }
  }

  /**
   * Counts up all the descendants of every person in the tree.
   * @return descNums - list of descendant counts for every person in tree with descendants.
   */
  private static ArrayList<Integer> getDescendants() {
      ArrayList<Family> parentalFamilies = new ArrayList<>();
      ArrayList<Integer> descNums = new ArrayList<>();
      for(Entry<String, Person> pair : tree.getPeople().entrySet()) {
          int kidCount = 0;
          for(Integer id: pair.getValue().getFamilyIDs()) {
              if (tree.getFamilies().get(id).getParents().contains(pair.getValue())) {
                  kidCount += tree.getFamilies().get(id).getChildren().size();
                  parentalFamilies.add(tree.getFamilies().get(id));
              }
          }
          if (kidCount > 0) {
              getGenerations(pair.getValue(), parentalFamilies, 0, false);
              pair.getValue().addToDescendants(kidCount);
              int descendants = pair.getValue().getNumberOfDescendants();
              descNums.add(descendants);
          } else {
              pair.getValue().resetNumberOfDescendants();
          }
          parentalFamilies.clear();
      }
      return descNums;
  }

  /**
   *
   */
  private static void countChildren(String name) {
      int kidCount = 0;
      ArrayList<Family> parentalFamilies = new ArrayList<>();
      if (tree.getPeople().containsKey(name)) {
          for(Integer id: tree.getPeople().get(name).getFamilyIDs()) {
              if (tree.getFamilies().get(id).getParents().contains(tree.getPeople().get(name))) {
                  kidCount += tree.getFamilies().get(id).getChildren().size();
                  parentalFamilies.add(tree.getFamilies().get(id));
              }
          }
          System.out.println("\nNumber of children: " + kidCount);
          getGenerations(tree.getPeople().get(name), parentalFamilies, 0, true);
          parentalFamilies.clear();
          if (kidCount > 0) {
              listChildren(name);
          }
      } else {
          System.out.println("name not found");
      }
  }

  /**
   * User rankings menu, where they can choose which rankings they would like to generate.
   * @param scanner the system.in scanner for user input.
   */
  private static void rankingsMenu(Scanner scanner) {
        System.out.println("Rankings");
        System.out.println("type 'help' for list of ranking options");
        boolean back = false;
        while (!back) {
            System.out.print(":");
            String input = scanner.nextLine();
            String[] inputArray = input.split(", ");
            switch (inputArray[0]) {
                case "quit":
                    System.out.println("Usage: <command>, <parameter>, <parameter>\ntype 'help' for a list of commands");
                    back = true;
                    break;
                case "mostKids":
                    if (isValid(inputArray, 2, true)) {
                        printRankings(getMostKids(), Integer.parseInt(inputArray[1]), false, null);
                    } else {
                        System.out.println("usage: mostKids, <number>");
                    }
                    break;
                case "mostDescendants":
                    if (isValid(inputArray, 2, true)) {
                        printRankings(getDescendants(), Integer.parseInt(inputArray[1]), true, null);
                    } else {
                        System.out.println("usage: mostDescendants, <number");
                    }
                    break;
                case "ranking":
                    if (isValid(inputArray, 2, false)) {
                        getRankings(inputArray[1]);
                    } else {
                        System.out.println("usage: ranking, <name>");
                    }
                    break;
                case "help":
                    System.out.println("options:\n mostKids, <number>\n mostDescendants, <number>\n ranking, <name>");
                    break;
            }
        }
    }

  /**
   * Main program menu.
   * @param args main method arguments.
   */
  public static void main(String[] args) {
        boolean exitProgram = false;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Usage: <command>, <parameter>, <parameter>\ntype 'help' for a list of commands");
        String filePath = "./RawData.csv";
        tree = new Tree(filePath);
        while (!exitProgram) {
            System.out.print(":");
            String input = scanner.nextLine();
            String[] inputArray = input.split(", ");
            switch (inputArray[0]) {
                case "path" :
                    if (isValid(inputArray, 3, false)) {
                        findShortestPath(inputArray[1], inputArray[2]);
                    } else {
                        System.out.println("usage: path, <name>, <name>");
                    }
                    break;
                case "quit":
                    exitProgram = true;
                    scanner.close();
                    break;
                case "addFamily":
                    writeToCSV(addFamily(scanner), filePath);
                    break;
                case "isMember":
                    if (isValid(inputArray, 2, false)) {
                        if (tree.getPeople().containsKey(inputArray[1])) {
                            System.out.println("Yes.");
                        } else {
                            System.out.println("No.");
                        }
                    } else {
                        System.out.println("usage: isMember, <name>");
                    }
                    break;
                case "help":
                    System.out.println("Commands:\n path, <person1>, <person2> - displays connection between two people\n addFamily - add a new family instance\n isMember, <person1> - check if a person is in the tree\n listChildren, <person1> - list all the children of this person if any.\n getDetails, <person> - list this persons immediate family\n rankings - go to rankings menu\n stats - get current tree stats\n quit - exit program");
                    break;
                case "listChildren":
                    if (isValid(inputArray, 2, false)) {
                        countChildren(inputArray[1]);
                    } else {
                        System.out.println("usage: listChildren, <name>");
                    }
                    break;
                case "stats":
                  System.out.println("People: " + tree.getPeople().size());
                  System.out.println("Families: " + tree.getFamilies().size());
                  break;
                case "getDetails":
                    if (isValid(inputArray, 2, false)) {
                        getDetails(inputArray[1]);
                    }else {
                        System.out.println("usage: getDetails, <name>");
                    }
                    break;
                case "rankings":
                    rankingsMenu(scanner);
                    break;
                default:
                    System.out.println("incorrect command: type 'help' for a list of commands");
                    break;
            }
        }
    }

}
