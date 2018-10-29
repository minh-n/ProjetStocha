
//import com.jme3.system.AppSettings;
//import com.jme3.system.JmeCanvasContext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI implements ActionListener{


	public boolean verbose = false;
	
	public DataTSP data;	
	//ce Panneau citiesPanel permettrait d'afficher les villes, il calculerait les coordonnees etc
	private GUICitiesPanel citiesPanel;

	private static JFileChooser fileChooser;
	private static JButton openButton;
	private static String currentFilename;
	private static JLabel whatFile;
	private static JButton startButton;
	
	private static JFrame frame;
	private static JPanel panel;
	private static JPanel menu;
	
	//radiobuttons
	private static JRadioButton buttonStocha;
	private static JRadioButton buttonDeter;

	private static JComboBox<String> CplexOrAnnealingCombo;
 
	public GUI() {
		citiesPanel =  new GUICitiesPanel();

		data = new DataTSP();
		
	}

	
	//**************************************FRAME**************************************//
	
	
	public void createNewJFrame() {

		System.out.println("\n---WARNING: GUI standing by.");

		//Main window frame
		frame = new JFrame("TSP Solver");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setPreferredSize(new Dimension(1032, 755));


        
        // ********************************************************
	    // City panel
	    
	    this.getCitiesPanel().setBackground(Color.LIGHT_GRAY);
		//JLabel test = new JLabel("<html><font color='white'>this is the main jpanel for displaying the cities</font></html>");
		//this.getCitiesPanel().add(test);

		
		
        // ********************************************************
	    // File chooser
	    
        //Create the log first, because the action listeners
        //need to refer to it.
	    JTextArea log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        //JScrollPane logScrollPane = new JScrollPane(log);
		
        //Create a file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML", "xml");
        fileChooser.setFileFilter(filter);

        openButton = new JButton("Open a file...");
        openButton.setMaximumSize(openButton.getPreferredSize() );

        openButton.addActionListener(this);
     
		JPanel fcBox = new JPanel(new GridLayout(0, 1));
		Border border0 = BorderFactory.createTitledBorder("File selection");
		fcBox.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0), border0));
		whatFile = new JLabel("The selected file is " + currentFilename);
			
		//openbutton
		fcBox.add(openButton);
		fcBox.add(whatFile);

        
		// ********************************************************
		// Stochastic or Deterministic radio buttons

		JPanel checkOptions = new JPanel(new GridLayout(0, 1));
		Border border = BorderFactory.createTitledBorder("Type of problem");
		checkOptions.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0), border));


		buttonStocha = new JRadioButton("Stochastic Problem");
		buttonDeter = new JRadioButton("Deterministic Problem");

		buttonStocha.addActionListener(this);
		buttonDeter.addActionListener(this);
		
		ButtonGroup bgroup = new ButtonGroup();
		
		bgroup.add(buttonDeter);
		bgroup.add(buttonStocha);

		checkOptions.add(buttonStocha);
		checkOptions.add(buttonDeter);	
		
		

		// ********************************************************
		// Select between algorithms

		JPanel comboOptions = new JPanel(new GridLayout(0, 1));
		Border border2 = BorderFactory.createTitledBorder("Solve the problem with:");
		comboOptions.setBorder(border2);

		comboOptions.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0), border2));

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
		CplexOrAnnealingCombo.setMaximumSize(CplexOrAnnealingCombo.getPreferredSize() );
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
		
		JPanel wrapper = new JPanel();
		wrapper.add( CplexOrAnnealingCombo );
		comboOptions.add( wrapper );
		
		// ********************************************************
		// Panel et menu generaux

		panel = new JPanel(new BorderLayout());

		menu = new JPanel();
		menu.setPreferredSize(new Dimension(300, 100));
		menu.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

		menu.add(fcBox);
		menu.add(checkOptions);
		menu.add(comboOptions);
		menu.add(new JSeparator(JSeparator.HORIZONTAL));
		
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
		
		//set cities panel (right panel where cities are visible)
		panel.add(this.getCitiesPanel(), BorderLayout.CENTER);

		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fileChooser.showOpenDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                System.out.println("GUI: Filename : " + file.getName());
				
				if(file.getName().contains(".xml"))
				{
					whatFile.setText("The selected file is " + file.getName());
					currentFilename = file.getPath();
	
				}
            } 
        } 
        //Handle start of calculations
        
        if (e.getSource() == startButton) {
            
        	if (currentFilename != null)
        	{
            System.out.println("\nGUI: Starting computation!! Please wait...");
			data.readInputFile(currentFilename, verbose);
			
			//data.displayMatrix();
			//creating the panel in which the cities will be displayed
			citiesPanel.getData(data);

        	}
        	else
        	{
        		System.out.println("GUI: No file selected!");
        	}
        } 
        
	
 
        if (e.getSource() == buttonStocha) {
 
			System.out.println("GUI: Solving mode: STOCHASTIC");
			//TODO : put this information into a boolean
 
        } else if (e.getSource() == buttonDeter) {
 
			System.out.println("GUI: Solving mode: DETERMINISTIC");
			//TODO : put this information into a boolean

        }

    
    }


	public static String getCurrentFilename() {
		return currentFilename;
	}


	public static void setCurrentFilename(String currentFilename) {
		GUI.currentFilename = currentFilename;
	}

	public JPanel getCitiesPanel() {
		return this.citiesPanel;
	}

	public void setCitiesPanel(GUICitiesPanel citiesPanel) {
		this.citiesPanel = citiesPanel;
	}
	

	public static JFileChooser getFileChooser() {
		return fileChooser;
	}


	public static void setFileChooser(JFileChooser fileChooser) {
		GUI.fileChooser = fileChooser;
	}

}