package matrix;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import life.qbic.portal.Styles;
import life.qbic.portal.Styles.NotificationType;

public class MatrixStep implements WizardStep {

  private VerticalLayout main;

  public MatrixStep() {
    main = new VerticalLayout();
    main.setSpacing(true);
    main.setMargin(true);
    Button testButton = new Button("Click!");
    // main.addComponent(testButton);
    testButton.addClickListener(new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        Styles.notification("test", "hello", NotificationType.DEFAULT);
      }
    });
    List<ChemElement> elements = new ArrayList<ChemElement>();
    elements.add(new ChemElement("H", "Hydrogen", 1, 1, 1));
    elements.add(new ChemElement("He", "Helium", 1, 18, 2));
    elements.add(new ChemElement("Li", "Lithium", 2, 1, 3));
    elements.add(new ChemElement("Be", "Beryllium", 2, 2, 4));
    elements.add(new ChemElement("B", "Boron", 2, 13, 5));
    elements.add(new ChemElement("C", "Carbon", 2, 14, 6));
    elements.add(new ChemElement("N", "Nitrogen", 2, 15, 7));
    elements.add(new ChemElement("O", "Oxygen", 2, 16, 8));
    elements.add(new ChemElement("F", "Fluorine", 2, 17, 9));
    elements.add(new ChemElement("Ne", "Neon", 2, 18, 10));
    elements.add(new ChemElement("Na", "Sodium", 3, 1, 11));
    elements.add(new ChemElement("Mg", "Magnesium", 3, 2, 12));
    elements.add(new ChemElement("Al", "Aluminium", 3, 13, 13));
    elements.add(new ChemElement("Si", "Silicon", 3, 14, 14));
    elements.add(new ChemElement("P", "Phosphorus", 3, 15, 15));
    elements.add(new ChemElement("S", "Sulfur", 3, 16, 16));
    elements.add(new ChemElement("Cl", "Chlorine", 3, 17, 17));
    elements.add(new ChemElement("Ar", "Argon", 3, 18, 18));
    PeriodicTable table = new PeriodicTable(this);
    table.setElements(elements);
    main.addComponent(table);
    main.addComponent(new Label("test component after js"));
  }

  @Override
  public String getCaption() {
    return "Matrix Input Step";
  }

  @Override
  public Component getContent() {
    return main;
  }

  @Override
  public boolean onAdvance() {
    return true;
  }

  @Override
  public boolean onBack() {
    // TODO Auto-generated method stub
    return false;
  }

  public void useSelectedElement(ChemElement element) {
    Styles.notification("element clicked", element.getName() + " clicked",
        NotificationType.SUCCESS);
  }

}
