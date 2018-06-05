package matrix;

import java.util.List;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({"vaadin://js/d3.v4.min.js", "periodic_connector.js"})
public class PeriodicTable extends AbstractJavaScriptComponent {

  @Override
  public PeriodicTableState getState() {
    return (PeriodicTableState) super.getState();
  }

  public void setElements(final List<ChemElement> elements) {
    getState().setElements(elements);
  }

  public PeriodicTable(final MatrixStep layout) {
//    getState().setElements(elements);

    registerRpc(new ElementClickRpc() {

      public void onElementClick(ChemElement element) {
        layout.useSelectedElement(element);
      }

    });
  }

}
