import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;


public class FamilyTree {
  private static Tree tree;
  private static ArrayList<Person> path = new ArrayList<>();

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

  private static void findShortestPath(String nameOne, String nameTwo) {
    try {
      HashMap<String, Person> people = tree.getPeople();
      if (people.containsKey(nameOne) && people.containsKey(nameTwo)) {
          findPath(people.get(nameOne), people.get(nameTwo));
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

  private static void findPath(Person sourcePerson, Person searchPerson) throws InterruptedException {
    ArrayList<Person> visited = new ArrayList<>();
    HashMap<Person, Person> predecessorMap = new HashMap<Person, Person>();
    ArrayBlockingQueue<Person> queue = new ArrayBlockingQueue<Person>(tree.getPeople().size()*500);
    ArrayList<Integer> famIDs = sourcePerson.getFamilyIDs();
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
