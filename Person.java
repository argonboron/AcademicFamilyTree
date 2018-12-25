import java.util.ArrayList;

/**
 * <h1>Person Object</h1>
 * Object to store a representation of a person in the tree.
 * @author argonboron - Corrie McGregor
 * @version 1.0
 * @since 2018-12-23
 */
class Person {
  private String name;
  private ArrayList<Integer> familyIDs = new ArrayList<>();
  private ArrayList<Person> spouses = new ArrayList<>();
  private int distanceFromSource;

  /**
   * Getter.
   * @return Name of current person.
   */
  String getName() {
    return name;
  }

  /**
   * Getter.
   * @return distance from source.
   */
  int getDistance() {
    return distanceFromSource;
  }

  /**
   * Setter.
   * @param newDistance new distance.
   */
  void setDistance(int newDistance) {
    distanceFromSource = newDistance;
  }

  /**
   * Getter.
   * @return list of all family id's associated with current person.
   */
  ArrayList<Integer> getFamilyIDs() {
    return familyIDs;
  }

    /**
     * Add marriage relationship to current person.
     * @param spouse spouse.
     */
  void addMarriage(Person spouse) {
    spouses.add(spouse);
  }

  /**
   * Add new family id to persons list of families.
   * @param id new family id.
   */
  void addFamilyID(int id) {
    familyIDs.add(id);
  }

    /**
     * Constructor.
     * @param name person name.
     * @param familyID Person family id.
     */
  Person(String name, int familyID) {
    this.name = name;
    familyIDs.add(familyID);
    distanceFromSource = 0;
  }
}
