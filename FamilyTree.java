import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
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
          System.out.println("Distance: " + people.get(nameTwo).getDistance());
          for (int i = 0; i < people.get(nameTwo).getDistance()+1; i++) {
            System.out.println(path.get(i).getName());
            if(i < people.get(nameTwo).getDistance()) {
              // if (i>=1) {
              //   System.out.print(" who");
              // }
              System.out.print(getRelationship(path.get(i), path.get(i+1)));
            }
          }
          System.out.println();
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
        Family family = tree.getFamily((int) id);
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
   * Main program.
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
                    if (inputArray.length == 3) {
                        findShortestPath(inputArray[1], inputArray[2]);
                        break;
                    } else {
                        System.out.println("usage: path, <name>, <name>");
                        break;
                    }
                case "quit":
                    exitProgram = true;
                    scanner.close();
                    break;
                case "addFamily":
                    writeToCSV(addFamily(scanner), filePath);
                    break;
                case "isMember":
                    if (inputArray.length == 2) {
                        if (tree.getPeople().containsKey(inputArray[1])) {
                            System.out.println("Yes.");
                        } else {
                            System.out.println("No.");
                        }
                        break;
                    } else {
                        System.out.println("usage: isMember, <name>");
                        break;
                    }
                case "help":
                    System.out.println("Commands:\n path, <person1>, <person2> - displays connection between two people\n addFamily - add a new family instance\n isMember, <person1> - check if a person is in the tree\n quit - exit program");
                    break;
                default:
                    System.out.println("incorrect command: type 'help' for a list of commands");
                    break;
            }
        }
    }

}
