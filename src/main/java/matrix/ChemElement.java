package matrix;

public class ChemElement {

  private String abbreviation;
  private String name;
  private int row;
  private int col;
  private int p;
  
  public ChemElement() {
    // TODO Auto-generated constructor stub
  }

  public ChemElement(String abbreviation, String name, int row, int col, int p) {
    super();
    this.abbreviation = abbreviation;
    this.name = name;
    this.p = p;
    this.row = row;
    this.col = col;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public void setCol(int col) {
    this.col = col;
  }

  public void setP(int p) {
    this.p = p;
  }
  
  public String getAbbreviation() {
    return abbreviation;
  }

  public String getName() {
    return name;
  }

  public int getP() {
    return p;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

}
