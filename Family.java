import java.util.ArrayList;

/**
 * <h1>Family Object</h1>
 * Object to store a representation of a person in the tree.
 * @author argonboron - Corrie McGregor
 * @version 1.0
 * @since 2018-12-23
 */
public class Family {
  private int year;
  private int id;
  private boolean marriage;
  private ArrayList<Person> children = new ArrayList<>();
  private ArrayList<Person> parents = new ArrayList<>();

  /**
   * Prints out the family details.
   */
  public void printFamily() {
    System.out.print("Parents: ");
    for (Person parent : parents) {
      System.out.print(parent.getName() + ", ");
    }
    System.out.println();
    System.out.println("Children: ");
    for (Person aChildren : children) {
      System.out.println("----" + aChildren.getName());
    }
    System.out.println("Year Adopted: " + year);
    System.out.println("Parents Married? " + marriage);
  }

  /**
   * Getter.
   * @return the year the family was formed.
   */
  int getYear() {
    return year;
  }

  /**
   * Add parent to family.
   * @param parent the parent.
   */
  void addParent(Person parent) {
    parents.add(parent);
  }

  /**
   * Return all members of this family.
   * @return All members of the family.
   */
  ArrayList<Person> getAllMembers() {
    ArrayList<Person> all = new ArrayList<>();
    all.addAll(parents);
    all.addAll(children);
    return all;
  }

  /**
   * Getter.
   * @return if the parents of this family are married.
   */
  boolean hasMarriage() {
    return marriage;
  }

  /**
   * Getter.
   * @return The children in this family.
   */
  ArrayList<Person> getChildren() {
    return children;
  }

  /**
   * Set the marriage to true and adds all parents to each others spouses list.
   */
  void setMarriage() {
    marriage = true;
    for(Person parent: parents) {
      for (Person parent1 : parents) {
        if (!parent1.getName().equals(parent.getName())) {
          parent.addMarriage(parent1);
        }
      }
    }
  }

  /**
   * Getter.
   * @return The id of this family.
   */
  int getID() {
    return id;
  }

  /**
   * Getter.
   * @return The children in this family.
   */
  ArrayList<Person> getParents() {
    return parents;
  }

  /**
   * Add a new child to the family.
   * @param child the child.
   */
  void addChild(Person child) {
    children.add(child);
  }

  /**
   * Setter.
   * @param year year the family was formed.
   */
  void setYear(int year) {
    this.year = year;
  }

  /**
   * Constructor.
   * @param id the id of the family.
   */
  Family(int id){
    this.id = id;
    marriage = false;
  }

}
