package matrix;

import com.vaadin.shared.communication.ServerRpc;

public interface ElementClickRpc extends ServerRpc {
  public void onElementClick(ChemElement element);
}
