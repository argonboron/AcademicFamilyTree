import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Tree {
  private HashMap<String, Person> people = new HashMap<>();
  private HashMap<Integer, Family> families = new HashMap<>();

  private void createTree(String csvFile) {
    BufferedReader reader = null;
    String line = "";
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
        //  family.printFamily();
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

  private Person addPerson(String name, int familyID) {
    Person person = null;
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

  private boolean isNumeric(String strNum) {
    try {
        int num = Integer.parseInt(strNum);
    } catch (NumberFormatException | NullPointerException nfe) {
        return false;
    }
    return true;
  }

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

  public HashMap<Integer, Family> getFamilies() {
    return families;
  }

  Family getFamily(int id) {
    return families.get(id);
  }

  HashMap<String, Person> getPeople() {
    return people;
  }

  void clearDistances() {
    for (Map.Entry<String, Person> pair : people.entrySet()) {
        pair.getValue().setDistance(0);
    }
  }

  Tree(String csvPath) {
    createTree(csvPath);
  }
}
