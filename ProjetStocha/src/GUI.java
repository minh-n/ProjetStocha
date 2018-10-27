
//import com.jme3.system.AppSettings;
//import com.jme3.system.JmeCanvasContext;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI implements ActionListener{


	public boolean verbose = false;
	public Data data;
	
	private GUICitiesPanel citiesPanel;
	private static String currentFilename;
	private static JLabel whatFile;
	private static JButton startButton;
	
	private static JFrame frame;
	private static JPanel panel;
	private static JPanel menu;
	private static JFileChooser fileChooser;
	private static JButton openButton;
	private static JCheckBox checkView;
	private static JCheckBox checkAeroport;
	private static JCheckBox checkTrajectoire;

	private static JComboBox<String> CplexOrAnnealingCombo;
	private static JComboBox<String> aeroCombo;

	private static JList<String> listVol;

	private static JLabel idVol;
	private static JLabel depart;
	private static JLabel arrivee;
	private static JLabel type;
	private static JLabel vitesse;
	private static JLabel altitude;
 
	private static int intervalle = 6000;
	private static Integer speedCount = 1;

	//private static LectureFichier data = new LectureFichier();

	public GUI(GUICitiesPanel citiesPanel) {
		this.setCitiesPanel(citiesPanel);
		idVol = new JLabel();
		depart = new JLabel();
		arrivee = new JLabel();
		type = new JLabel();
		vitesse = new JLabel();
		altitude = new JLabel();
		data = new Data();
		
	}

	
	//**************************************FRAME**************************************//
	
	
	public void createNewJFrame() {

		//Main window frame
		frame = new JFrame("TSP Solver");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setPreferredSize(new Dimension(900, 750));
	  

        //Create the log first, because the action listeners
        //need to refer to it.
	    JTextArea log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        //Create a file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        openButton = new JButton("Open a File...");
        openButton.addActionListener(this);
       
        
	    this.getCitiesPanel().setBackground(new java.awt.Color(50, 0, 50));
		JLabel test = new JLabel("<html><font color='white'>this is the main jpanel for displaying the cities</font></html>");
		this.getCitiesPanel().add(test);

		// ********************************************************
		// Checkboxes

		JPanel checkOptions = new JPanel(new GridLayout(0, 1));
		Border border = BorderFactory.createTitledBorder("File selection");
		checkOptions.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0), border));

		whatFile = new JLabel("The selected file is " + currentFilename);
		
		//openbutton
        checkOptions.add(openButton);
        checkOptions.add(whatFile);

        
        
        
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

		// CPLEX OU ANNEALING
		final ArrayList<String> listCplexOrAnnealing = new ArrayList<String>();
		Collections.sort(listCplexOrAnnealing);
		listCplexOrAnnealing.add(0, "Simulated Annealing");
		listCplexOrAnnealing.add(0, "CPLEX");
		DefaultComboBoxModel<String> modelBoxCplexOrAnnealing = new DefaultComboBoxModel<String>();
		for (String str : listCplexOrAnnealing) {
			modelBoxCplexOrAnnealing.addElement(str);
		}
		
		CplexOrAnnealingCombo = new JComboBox<String>(modelBoxCplexOrAnnealing);
		CplexOrAnnealingCombo.setEditable(true);
		CplexOrAnnealingCombo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				String newText = (String) CplexOrAnnealingCombo.getEditor().getItem();

				DefaultComboBoxModel<String> newModel = new DefaultComboBoxModel<String>();

				for (String name : listCplexOrAnnealing) {
					if (name.contains("" + newText)) {
						newModel.addElement(name);
					}
				}

				CplexOrAnnealingCombo.setModel(newModel);
				CplexOrAnnealingCombo.getEditor().setItem(newText);
				CplexOrAnnealingCombo.setPopupVisible(true);
			}
		});

		comboOptions.add(CplexOrAnnealingCombo);


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
		menu.add(infosVol);
		menu.add(buttonVitesse);
		
		
		//Start button to start the computation
		startButton = new JButton("Start the TSP");
        startButton.addActionListener(this);
        
		JPanel commande = new JPanel();
		commande.setLayout(new BoxLayout(commande, BoxLayout.X_AXIS));
		
		commande.add(startButton);
		menu.add(commande);

		// Add the canvas to the panel
		//panel.add(canvas, BorderLayout.CENTER);
		panel.add(menu, BorderLayout.WEST);
		
		panel.add(this.getCitiesPanel(), BorderLayout.CENTER);

		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

//	
//	public static void main(String[] args) {
//		
//		// create new JME appsettings
//		//AppSettings settings = new AppSettings(true);
//		//settings.setResolution(1080, 800);
//		//settings.setSamples(8);
//
//		// canvasApplication = new ...
//		//canvasApplication = new Panel3D();
//		//canvasApplication.setPauseOnLostFocus(false);
//
//		// in the same way than in the "public static void main()" method from
//		// SimpleApplication
//		//settings.setFrameRate(60);
//		//settings.setVSync(true);
//
//		// NB : this line is used instead of the app.start();
//		//canvasApplication.createCanvas(); // create canvas!
//
//		//JmeCanvasContext ctx = (JmeCanvasContext) canvasApplication.getContext();
//		//canvas = ctx.getCanvas();
//		//Dimension dim = new Dimension(settings.getWidth(), settings.getHeight());
//		//canvas.setPreferredSize(dim);
//
//		// Create the JFrame with the Canvas on the middle
//		(static) createNewJFrame();
//	}

	public JPanel getCitiesPanel() {
		return this.citiesPanel;
	}

	public void setCitiesPanel(GUICitiesPanel citiesPanel) {
		this.citiesPanel = citiesPanel;
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


	public static JFileChooser getFileChooser() {
		return fileChooser;
	}


	public static void setFileChooser(JFileChooser fileChooser) {
		GUI.fileChooser = fileChooser;
	}

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fileChooser.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Filename : " + file.getName());
				
				if(file.getName().contains(".xml"))
				{
					whatFile.setText("The selected file is " + file.getName());
					currentFilename = file.getPath();
	
				}
            } 
        } 
        //Handle start of calculations
        
        if (e.getSource() == startButton) {
            int returnVal = 0;
            System.out.println("######################################Program starting!!!!!!!!!!!!!################\n\n\n");
			System.out.println("Starting computation...");
			data.readInputFile(currentFilename, verbose);
			
			data.displayMatrix();
        } 
    }


	public static String getCurrentFilename() {
		return currentFilename;
	}


	public static void setCurrentFilename(String currentFilename) {
		GUI.currentFilename = currentFilename;
	}



}