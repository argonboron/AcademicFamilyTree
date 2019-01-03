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
  private int distanceFromSource;
  private int numberOfChildren;
  private int numberOfDescendants;

  /**
   * Getter.
   * @return Name of current person.
   */
  String getName() {
    return name;
  }

    /**
     * Getter.
     * @return Number of Descendants.
     */
  int getNumberOfDescendants() {
      return numberOfDescendants;
  }


  void addToDescendants(int newKids) {
      numberOfDescendants+= newKids;
  }

  void resetNumberOfDescendants() {
      numberOfDescendants = 0;
  }

  int getNumberOfChildren() {
      return numberOfChildren;
  }

  void setNumberOfChildren (int children) {
      numberOfChildren = children;
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
