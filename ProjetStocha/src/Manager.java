import java.util.Timer;

public class Manager {
	
	private static GUICitiesPanel citiesPanel;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("\n---WARNING: Manager standing by.");
		

		GUI gui = new GUI();
		gui.createNewJFrame();
	}
	
	public static GUICitiesPanel getCitiesPanel() {
		return citiesPanel;
	}
	public static void setCitiesPanel(GUICitiesPanel citiesPanel) {
		Manager.citiesPanel = citiesPanel;
	}
}
