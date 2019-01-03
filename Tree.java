import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>Tree Object</h1>
 * An object to represent the overall family tree. Consists of Family instances which link to people.
 * @author argonboron - Corrie McGregor
 * @version 1.0
 * @since 2018-12-23
 */
public class Tree {
  private HashMap<String, Person> people = new HashMap<>();
  private HashMap<Integer, Family> families = new HashMap<>();

  /**
   * Reads the csv file and extracts each family to create a Family object.
   * @param csvFile the raw data for the tree.
   */
  private void createTree(String csvFile) {
    BufferedReader reader = null;
    String line;
    String cvsSplitBy = ",";
    int familyID = 0;
    try {
        reader = new BufferedReader(new FileReader(csvFile));
        boolean onParents = true;
        while ((line = reader.readLine()) != null) {
          String[] lineSplit = line.split(cvsSplitBy);
          Family family = new Family(familyID);
          for(int i = 0; i < (lineSplit.length - 1); i++) {
            Person person;
            if (isNumeric(lineSplit[i])) {
              family.setYear(Integer.parseInt(lineSplit[i]));
              if (lineSplit[i+1].equals("+")) {
                family.setMarriage();
              }
              break;
            }
            if (lineSplit[i].equals("@")) {
              onParents = false;
            } else if (onParents) {
                person = addPerson(lineSplit[i], familyID);
                family.addParent(person);
            } else {
              person = addPerson(lineSplit[i], familyID);
              family.addChild(person);
            }
          }
          families.put(familyID, family);
          onParents = true;
          familyID++;
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

  }

  /**
   * Add person to unique list of people to ensure no duplicate people.
   * @param name Name of person.
   * @param familyID ID of family they are to be added to.
   * @return The Person Object for the person that was added.
   */
  private Person addPerson(String name, int familyID) {
    Person person;
    if (!people.isEmpty()) {
      if (people.containsKey(name)) {
        person = people.get(name);
        person.addFamilyID(familyID);
        people.put(name, person);
        return person;
      } else {
        person = new Person(name, familyID);
        people.put(name, person);
        return person;
      }
    } else {
      person = new Person(name, familyID);
      people.put(name, person);
      return person;
    }
  }

  /**
   * Checks if a string is numeric.
   * @param strNum String to be checked.
   * @return if the string is numeric or not.
   */
  boolean isNumeric(String strNum) {
    try {
        Integer.parseInt(strNum);
    } catch (NumberFormatException | NullPointerException nfe) {
        return false;
    }
    return true;
  }

  /**
   * Adds a family to a tree.
   * @param parents List of parent's names.
   * @param children List of children's names.
   * @param year Year of formation.
   * @param married Whether the parents are married.
   * @return The family which has been added.
   */
  Family addFamilyToTree(String[] parents, String[] children, String year, boolean married) {
    int famID = families.size()+1;
    Family family = new Family(famID);
    if (isNumeric(year)) {
      family.setYear(Integer.parseInt(year));
    } else {
      family.setYear(0);
    }
      for (String parent1 : parents) {
          Person parent = addPerson(parent1, famID);
          family.addParent(parent);
      }
    if (children != null) {
        for (String aChildren : children) {
            Person child = addPerson(aChildren, famID);
            family.addChild(child);
        }
    }
    if (married) {
      family.setMarriage();
    }
    families.put(famID, family);
    return family;
  }

  /**
   * Getter.
   * @return Map of id's to Family objects.
   */
  HashMap<Integer, Family> getFamilies() {
    return families;
  }

  /**
   * Get a family by its id.
   * @param id Family id.
   * @return Family object.
   */
  Family getFamily(int id) {
    return families.get(id);
  }

  /**
   * Getter.
   * @return Map of names to people objects.
   */
  HashMap<String, Person> getPeople() {
    return people;
  }

  /**
   * reset the distance of all people.
   */
  void clearDistances() {
    for (Map.Entry<String, Person> pair : people.entrySet()) {
        pair.getValue().setDistance(0);
    }
  }

  /**
   * Constructor.
   * @param csvPath file path of csv.
   */
  Tree(String csvPath) {
    createTree(csvPath);
  }
}
