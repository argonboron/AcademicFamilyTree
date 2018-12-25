import java.util.ArrayList;

public class Family {
  private int year;
  private int id;
  private boolean marriage;
  private ArrayList<Person> children = new ArrayList<>();
  private ArrayList<Person> parents = new ArrayList<>();

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

  int getYear() {
    return year;
  }

  void addParent(Person parent) {
    parents.add(parent);
  }

  ArrayList<Person> getAllMembers() {
    ArrayList<Person> all = new ArrayList<>();
    all.addAll(parents);
    all.addAll(children);
    return all;
  }

  boolean hasMarriage() {
    return marriage;
  }

  ArrayList<Person> getChildren() {
    return children;
  }

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

  ArrayList<Person> getParents() {
    return parents;
  }

  void addChild(Person child) {
    children.add(child);
  }

  void setYear(int year) {
    this.year = year;
  }

  Family(int id){
    this.id = id;
    marriage = false;
  }

}
