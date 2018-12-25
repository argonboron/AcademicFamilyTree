import java.util.ArrayList;

public class Person {
  private String name;
  private ArrayList<Integer> familyIDs = new ArrayList<>();
  private ArrayList<Person> spouses = new ArrayList<>();
  private int distanceFromSource;


  String getName() {
    return name;
  }

  int getDistance() {
    return distanceFromSource;
  }

  public void addDistance() {
    distanceFromSource++;
  }

  void setDistance(int newDistance) {
    distanceFromSource = newDistance;
  }

  ArrayList<Integer> getFamilyIDs() {
    return familyIDs;
  }

  void addMarriage(Person spouse) {
    spouses.add(spouse);
  }

  void addFamilyID(int id) {
    familyIDs.add(id);
  }

  Person(String name, int familyID) {
    this.name = name;
    familyIDs.add(familyID);
    distanceFromSource = 0;
  }
}
