
//import com.jme3.system.AppSettings;
//import com.jme3.system.JmeCanvasContext;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI {

	//private static Panel3D canvasApplication;

	//private static Canvas canvas; // JAVA Swing Canvas
	private static JPanel citiesPanel;

	private static JFrame frame;
	private static JPanel panel;
	private static JPanel menu;

	private static JCheckBox checkView;
	private static JCheckBox checkAeroport;
	private static JCheckBox checkTrajectoire;

	private static JComboBox<String> paysCombo;
	private static JComboBox<String> aeroCombo;

	private static JList<String> listVol;

	private static JButton buttonPause;

	private static JLabel idVol;
	private static JLabel depart;
	private static JLabel arrivee;
	private static JLabel type;
	private static JLabel vitesse;
	private static JLabel altitude;

	private static int intervalle = 6000;
	private static Integer speedCount = 1;

	//private static LectureFichier data = new LectureFichier();

	public GUI() {
		idVol = new JLabel();
		depart = new JLabel();
		arrivee = new JLabel();
		type = new JLabel();
		vitesse = new JLabel();
		altitude = new JLabel();
	}

	public JLabel getIdVol() {
		return idVol;
	}
	
	public JList<String> getListVol()
	{
		return listVol;
	}

	public void setidVol(String str) {
		idVol.setText(("Id Vol : ") + str);
	}

	public void setDepart(String str) {
		depart.setText(("Depart : ") + str);
	}

	public void setArrivee(String str) {
		arrivee.setText(("Arrivee : ") + str);
	}

	public void setType(String str) {
		type.setText(("Type : ") + str);
	}

	public void setVitesse(String str) {
		vitesse.setText(("Vitesse : ") + str);
	}

	public void setAltitude(String str) {
		altitude.setText(("Altitude : ") + str);
	}

	public static int getIntervalle() {
		return intervalle;
	}

	public static void setIntervalle(int intervalle) {
		GUI.intervalle = intervalle;
	}

	public JCheckBox getCheckTrajectoire() {
		return checkTrajectoire;
	}

	public JCheckBox getCheckView() {
		return checkView;
	}

	
	
	//**************************************FRAME**************************************//
	
	
	private static void createNewJFrame() {

		frame = new JFrame("Flight Live Info");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setPreferredSize(new Dimension(900, 750));
		
	    
	    //????
//	    canvas = new Canvas();
//	    canvas.setPreferredSize(new Dimension(500,500));
//		canvas.setMaximumSize(new Dimension(500, 500));
//		canvas.setMinimumSize(new Dimension(500, 500));
	    
	    citiesPanel =  new JPanel(new GridLayout(0, 1));
	    citiesPanel.setBackground(new java.awt.Color(50, 0, 50));
		JLabel test = new JLabel("<html><font color='white'>this is the main jpanel for displaying the cities</font></html>");
		citiesPanel.add(test);

		// ********************************************************
		// Checkboxes

		JPanel checkOptions = new JPanel(new GridLayout(0, 1));
		Border border = BorderFactory.createTitledBorder("Affichage");
		checkOptions.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0), border));

		// Vue 3D
		checkView = new JCheckBox("Probleme Stocha");
		checkView.setSelected(false);
		checkOptions.add(checkView);


		// Aeroport
		checkAeroport = new JCheckBox("Pb deterministe");
		checkAeroport.setSelected(false);
		checkOptions.add(checkAeroport);


		// Trajectoire
		checkTrajectoire = new JCheckBox("Charger un fichier");
		checkTrajectoire.setSelected(false);
		checkOptions.add(checkTrajectoire);

	

		// ********************************************************
		// Limiter la vue

		JPanel comboOptions = new JPanel(new GridLayout(0, 1));
		Border border2 = BorderFactory.createTitledBorder("Limiter la vue a:");
		comboOptions.setBorder(border2);

		comboOptions.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0), border2));

		// Pays
		ArrayList<String> listPays = new ArrayList<String>(
				Arrays.asList("Pratap", "Peter", "Harsh"));;
		Collections.sort(listPays);
		listPays.add(0, "Tous les pays");

		DefaultComboBoxModel<String> modelBoxPays = new DefaultComboBoxModel<String>();

		for (String str : listPays) {
			modelBoxPays.addElement(str);
		}

		paysCombo = new JComboBox<String>(modelBoxPays);
		paysCombo.setEditable(true);

		paysCombo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				String newText = (String) paysCombo.getEditor().getItem();

				DefaultComboBoxModel<String> newModel = new DefaultComboBoxModel<String>();

				for (String name : listPays) {
					if (name.contains("" + newText)) {
						newModel.addElement(name);
					}
				}

				paysCombo.setModel(newModel);
				paysCombo.getEditor().setItem(newText);
				paysCombo.setPopupVisible(true);
			}
		});

		// Aeroport
		ArrayList<String> listAeroport = new ArrayList<String>(
				Arrays.asList("aero1", "effdsPeter", "fggggggggggHarsh", "fnggfsd"));;
	
		
		Collections.sort(listAeroport);
		listAeroport.add(0, "Tous les aeroports");

		DefaultComboBoxModel<String> modelBoxAeroport = new DefaultComboBoxModel<String>();

		for (String str : listAeroport) {
			modelBoxAeroport.addElement(str);
		}

		aeroCombo = new JComboBox<String>(modelBoxAeroport);
		aeroCombo.setEditable(true);

		aeroCombo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				String newText = (String) aeroCombo.getEditor().getItem();

				DefaultComboBoxModel<String> newModel = new DefaultComboBoxModel<String>();

				for (String str : listAeroport) {
					if (str.contains("" + newText)) {
						newModel.addElement(str);
					}
				}

				aeroCombo.setModel(newModel);
				aeroCombo.getEditor().setItem(newText);
				aeroCombo.setPopupVisible(true);
			}
		});

		comboOptions.add(paysCombo);
		comboOptions.add(aeroCombo);

		// ********************************************************
		// Liste des vols

		JPanel listeVols = new JPanel(new BorderLayout());
		Border border3 = BorderFactory.createTitledBorder("Liste des vols ");
		listeVols.setBorder(border3);
		listeVols.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0), border3));
		listeVols.setPreferredSize(new Dimension(70, 30));

		Collections.sort(listAeroport);

		DefaultListModel<String> listModel = new DefaultListModel<String>();

		for (String str : listAeroport) {
			listModel.addElement(str);
		}

		listVol = new JList<String>(listModel);

		listVol.setVisibleRowCount(1);
		listVol.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listVol.setSelectedIndex(0);
		listVol.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(listVol);

		listeVols.add(listScrollPane, BorderLayout.CENTER);

		// ****************************************************************
		// Information sur un vol

		JPanel infosVol = new JPanel(new GridLayout(0, 1));

		Border border4 = BorderFactory.createTitledBorder("Informations ");

		infosVol.setBorder(new CompoundBorder(border4, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		infosVol.setBorder(border4);
		// infosVol.setLayout(new BoxLayout(infosVol, BoxLayout.PAGE_AXIS));

		idVol = new JLabel("Id Vol :");
		infosVol.add(idVol);
		depart = new JLabel("Depart : ");
		infosVol.add(depart);
		arrivee = new JLabel("Arrivee : ");
		infosVol.add(arrivee);
		type = new JLabel("Type : ");
		infosVol.add(type);
		vitesse = new JLabel("Vitesse : ");
		infosVol.add(vitesse);
		altitude = new JLabel("Altitude : ");
		infosVol.add(altitude);		
				
		// ********************************************************

		// SpeedTxt
		JLabel texteVitesse = new JLabel("Vitesse x" + speedCount.toString());

		// Boutons

	
		JPanel buttonVitesse = new JPanel();
		buttonVitesse.setLayout(new BoxLayout(buttonVitesse, BoxLayout.X_AXIS));
		buttonVitesse.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonVitesse.add(Box.createRigidArea(new Dimension(10, 0)));

		// ********************************************************
		// Panel et menu generaux

		panel = new JPanel(new BorderLayout());

		menu = new JPanel();
		menu.setPreferredSize(new Dimension(300, 100));
		menu.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

		menu.add(checkOptions);
		menu.add(comboOptions);
		menu.add(new JSeparator(JSeparator.HORIZONTAL));
		menu.add(listeVols);
		menu.add(infosVol);
		menu.add(buttonVitesse);
		
		JPanel commande = new JPanel();
		commande.setLayout(new BoxLayout(commande, BoxLayout.X_AXIS));
		
		commande.add(texteVitesse);
		menu.add(commande);

		// Add the canvas to the panel
		//panel.add(canvas, BorderLayout.CENTER);
		panel.add(menu, BorderLayout.WEST);
		panel.add(citiesPanel, BorderLayout.CENTER);

		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	
	public static void main(String[] args) {
		
		// create new JME appsettings
		//AppSettings settings = new AppSettings(true);
		//settings.setResolution(1080, 800);
		//settings.setSamples(8);

		// canvasApplication = new ...
		//canvasApplication = new Panel3D();
		//canvasApplication.setPauseOnLostFocus(false);

		// in the same way than in the "public static void main()" method from
		// SimpleApplication
		//settings.setFrameRate(60);
		//settings.setVSync(true);

		// NB : this line is used instead of the app.start();
		//canvasApplication.createCanvas(); // create canvas!

		//JmeCanvasContext ctx = (JmeCanvasContext) canvasApplication.getContext();
		//canvas = ctx.getCanvas();
		//Dimension dim = new Dimension(settings.getWidth(), settings.getHeight());
		//canvas.setPreferredSize(dim);

		// Create the JFrame with the Canvas on the middle
		createNewJFrame();
	}

	public static JPanel getCitiesPanel() {
		return citiesPanel;
	}

	public static void setCitiesPanel(JPanel citiesPanel) {
		GUI.citiesPanel = citiesPanel;
	}
}