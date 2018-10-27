public class Manager {
	
	private static GUICitiesPanel citiesPanel;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Hello world");
		
		//ce Panneau citiesPanel permettrait d'afficher les villes, il calculerait les coordonnees etc
	    GUICitiesPanel citiesPanel =  new GUICitiesPanel();
		
		GUI gui = new GUI(citiesPanel);
		gui.createNewJFrame();

		
		

	}
	
	
	public static GUICitiesPanel getCitiesPanel() {
		return citiesPanel;
	}
	public static void setCitiesPanel(GUICitiesPanel citiesPanel) {
		Manager.citiesPanel = citiesPanel;
	}


}
