package matrix;

import java.util.List;

import com.vaadin.shared.ui.JavaScriptComponentState;

public class PeriodicTableState extends JavaScriptComponentState {

  private List<ChemElement> elements;

  public List<ChemElement> getElements() {
    return elements;
  }

  public void setElements(List<ChemElement> elements) {
    this.elements = elements;
  }
}
