
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
import java.util.Hashtable;
import java.util.Timer;

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import ilog.concert.IloException;

public class GUI implements ActionListener, ChangeListener{


	public boolean verbose = false;
	
	public DataTSP data;	
	//ce Panneau citiesPanel permettrait d'afficher les villes, il calculerait les coordonnees etc
	private GUICitiesPanel citiesPanel;

	private static JFileChooser fileChooser;
	private static JButton openButton;
	private static String currentFilename;
	private static JLabel whatFile;
	private static JButton startButton;
	
	//parameters for the Annealing
	private static JSlider sliderTempCoef;
	private static JSlider sliderAcceptRate;
	private static JSlider sliderIteNumber;

	
	//infos
	private static JLabel timeTaken;
	private static JLabel totalCost;
	private static JLabel initialTemp;
	private static JLabel initialCost;

	private static double time = 0;
	private static double cost = 0;
	private static double initTemp = 0.;
	private static double initCost = 0.;
	
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
		
		//TODO: set the correct values for the sliders
		sliderTempCoef = new JSlider(0, 10, 9);
		sliderAcceptRate = new JSlider(0, 10);
		sliderIteNumber = new JSlider(0, 1000);
		
	}

	
	//**************************************FRAME**************************************//
	
	
	@SuppressWarnings("unchecked")
	public void createNewJFrame() {

		System.out.println("\n---WARNING: GUI standing by.");

		//Main window frame, panel and menu
		frame = new JFrame("TSP Solver");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setPreferredSize(new Dimension(980, 755));
	    panel = new JPanel(new BorderLayout());
		menu = new JPanel();
		menu.setPreferredSize(new Dimension(300, 100));
		menu.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
		Dimension dim = new Dimension(300, 170);

        
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
		whatFile = new JLabel("Please select a file.");
			
		//openbutton
		fcBox.add(openButton);
		fcBox.add(whatFile);
		fcBox.setMaximumSize(dim);

        
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
		checkOptions.setMaximumSize(dim);
		

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
		comboOptions.setMaximumSize(dim);

		
		
		
		
		
		
		
		
		// ********************************************************
		// Sliders
		
		JPanel sliders = new JPanel(new GridLayout(0, 1));
		Border border6 = BorderFactory.createTitledBorder("Annealing parameters");
		sliders.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0), border6));


		 
		sliderTempCoef.setPaintLabels(true);
		sliderAcceptRate.setPaintLabels(true);
		sliderIteNumber.setPaintLabels(true);
		
		sliderIteNumber.addChangeListener(this);
		sliderAcceptRate.addChangeListener(this);
		sliderTempCoef.addChangeListener(this);

		
		@SuppressWarnings("rawtypes")
		Hashtable labelTable = new Hashtable();
		labelTable.put( new Integer( 0 ), new JLabel("0.0") );
		labelTable.put( new Integer( 5 ), new JLabel("0.5") );
		labelTable.put( new Integer( 10 ), new JLabel("1.0") );
		sliderTempCoef.setLabelTable( labelTable );
		sliderAcceptRate.setLabelTable( labelTable );
		
		@SuppressWarnings("rawtypes")
		Hashtable labelTable2 = new Hashtable();
		labelTable2.put( new Integer( 0 ), new JLabel("0.0") );
		labelTable2.put( new Integer( 500 ), new JLabel("500") );
		labelTable2.put( new Integer( 1000 ), new JLabel("1000") );
		sliderIteNumber.setLabelTable( labelTable2 );

		
        // Add change listener to the slider
//		sliderIteNumber.addChangeListener(new ChangeListener() {
//
//            public void stateChanged(ChangeEvent e) {
//
//                status.setText("Value of the slider is: " + ((JSlider)e.getSource()).getValue());
//
//            }
//
//        });

		
		sliders.add(new JLabel("Temperature coefficient: "));
		sliders.add(sliderTempCoef);

		
		sliders.add(new JLabel("Acceptance rate: "));
		sliders.add(sliderAcceptRate);
		
		sliders.add(new JLabel("Iteration number: "));
		sliders.add(sliderIteNumber);

		// ********************************************************
		// Infos
		
		JPanel informations = new JPanel(new GridLayout(0, 1));
		Border border3 = BorderFactory.createTitledBorder("Informations");
		informations.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0), border3));


		timeTaken = new JLabel("The time taken to compute is " + time);
		totalCost = new JLabel("The total cost of this route is " + cost);

		initialTemp = new JLabel("The initial temperature is " + getInitTemp());
		initialCost  = new JLabel("The initial cost of this route is " + getInitCost());
		
		informations.add(initialTemp);
		informations.add(initialCost);
		informations.add(totalCost);
		informations.add(timeTaken);

		informations.setMaximumSize(dim);

		
		
		// ********************************************************
		// Panel et menu generaux

		menu.add(fcBox);
		menu.add(checkOptions);
		menu.add(comboOptions);
		menu.add(sliders);
		menu.add(informations);
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
	
    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
        	if(e.getSource() == sliderAcceptRate)
        	{  
        		double value = source.getValue()/10.;
          
        		System.out.println("Slider Accept rate = " + value);

        	}
        	else if(e.getSource() == sliderTempCoef)
        	{
        		double value = source.getValue()/10.;

        		System.out.println("Slider temp coef =" + value );

        	}
        	else
        	{
        		int value = (int)source.getValue();

        		System.out.println("Slider nb Ite =" + value );

        	}
            
        }
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
            
           long startTime = System.currentTimeMillis();


        	if (currentFilename != null)
        	{
            System.out.println("\nGUI: Starting computation!! Please wait...");
			data.readInputFile(currentFilename, verbose);
			
			//TODO faire en fct du choix de l'utilisateur			
			TSP problem = new TSP(data, false, false);
			try {
				CPLEXTSP solver = new CPLEXTSP(problem);
				//solver.solve();
				cost = problem.getCost();
				setInitTemp(-1);
				setInitCost(-1);
			} catch (IloException e1) {
				e1.printStackTrace();
			}
			
			//data.displayMatrix();
			//creating the panel in which the cities will be displayed
			citiesPanel.getData(data);
			
			 long endTime = System.currentTimeMillis();
		       
			System.out.println("--GUI Debug: It took " + (endTime - startTime) + " milliseconds");
			
			//TODO link time and cost, initial temp and cost
			time = endTime - startTime;
			//cost = 9000.;
			//setInitTemp(999.);
			//setInitCost(8999.);
			
			if(time > 1000){
				time /= 1000;
				timeTaken.setText("The time taken to compute is " + time + " seconds.");
			}
			else if(time > 60000){
				time /= 60000;
				timeTaken.setText("The time taken to compute is " + time + " minutes.");
			}
			else{
				timeTaken.setText("The time taken to compute is " + time + " milliseconds.");
			}
			
			if (initTemp == -1 && initCost == -1)
			{
				initialTemp.setText("");
				initialCost.setText("");
				
			}
			else
			{
				initialTemp.setText("The initial temperature is " + getInitTemp());
				initialCost.setText("The initial cost of this route is " + getInitCost());
				
			}
			
			totalCost.setText("The total cost of this route is " + cost +".");

		
			
			citiesPanel.repaint();

			
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


	public static JSlider getSliderTempCoef() {
		return sliderTempCoef;
	}


	public static void setSliderTempCoef(JSlider sliderTempCoef) {
		GUI.sliderTempCoef = sliderTempCoef;
	}


	public static JSlider getSliderAcceptRate() {
		return sliderAcceptRate;
	}


	public static void setSliderAcceptRate(JSlider sliderAcceptRate) {
		GUI.sliderAcceptRate = sliderAcceptRate;
	}


	public static JSlider getSliderIteNumber() {
		return sliderIteNumber;
	}


	public static void setSliderIteNumber(JSlider sliderIteNumber) {
		GUI.sliderIteNumber = sliderIteNumber;
	}


	public static JLabel getInitialTemp() {
		return initialTemp;
	}


	public static void setInitialTemp(JLabel initialTemp) {
		GUI.initialTemp = initialTemp;
	}


	public static JLabel getInitialCost() {
		return initialCost;
	}


	public static void setInitialCost(JLabel initialCost) {
		GUI.initialCost = initialCost;
	}


	public static double getInitTemp() {
		return initTemp;
	}


	public static void setInitTemp(double initTemp) {
		GUI.initTemp = initTemp;
	}


	public static double getInitCost() {
		return initCost;
	}


	public static void setInitCost(double initCost) {
		GUI.initCost = initCost;
	}

}