
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

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

//import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import ilog.concert.IloException;

/**
 * @author minh-n
 *
 */

public class GUI implements ActionListener, ChangeListener{


	public boolean verbose = false;
	
	public DataTSP data;	
	//ce Panneau citiesPanel permettrait d'afficher les villes, il calculerait les coordonnees etc
	private GUICitiesPanel citiesPanel;


	private static JFileChooser fileChooser;
	private static JCheckBox checkGetTSP;
	private static boolean getTSP = false;

	private static JButton openButton;
	private static String currentFilename;
	private static JLabel whatFile;
	private static JButton startButton;
	
	//parameters for the Annealing
	private static JSlider sliderTempCoef;
	private static JSlider sliderAcceptRate;
	private static JSlider sliderFailureThreshold;
	private static JSlider sliderInitTemp;

	
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
	private static JLabel recap;
	
	//radiobuttons
	private static JRadioButton buttonStocha;
	private static JRadioButton buttonDeter;
	private static boolean isDeter = true;

	private static JComboBox<String> CplexOrAnnealingCombo;
 
	/**
	 * The GUI constructor
	 */
	public GUI() {
		citiesPanel =  new GUICitiesPanel();
		data = new DataTSP();
		
		//TODO: set the correct values for the sliders
		sliderTempCoef = new JSlider(950, 999, 990);
		sliderAcceptRate = new JSlider(1, 100, 10);
		sliderFailureThreshold = new JSlider(1, 100, 20);
		sliderInitTemp = new JSlider(1, 10, 1);

	}

	
	//**************************************FRAME**************************************//
	
	
	/**
	 *  Main function for the GUI. Contains every elements, Jpanels, buttons...
	 */
	@SuppressWarnings("unchecked")
	public void createNewJFrame() {

		System.out.println("\n---WARNING: GUI standing by.");

		//Main window frame, panel and menu
		frame = new JFrame("TSP Solver");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setPreferredSize(new Dimension(1200, 900));
	    panel = new JPanel(new BorderLayout());
		menu = new JPanel();
		menu.setPreferredSize(new Dimension(320, 180));
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
			
		checkGetTSP = new JCheckBox("Get cities position from a TSP file");
		checkGetTSP.setSelected(false);
		checkGetTSP.addActionListener(this);

		//openbutton
		fcBox.add(openButton);
		fcBox.add(whatFile);
		fcBox.add(checkGetTSP);

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
		buttonDeter.setSelected(true);

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
		listCplexOrAnnealing.add("CPLEX");
		listCplexOrAnnealing.add("Simulated Annealing");
		listCplexOrAnnealing.add("No solving");

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
		sliderFailureThreshold.setPaintLabels(true);
		sliderInitTemp.setPaintLabels(true);

		sliderFailureThreshold.addChangeListener(this);
		sliderAcceptRate.addChangeListener(this);
		sliderTempCoef.addChangeListener(this);
		sliderInitTemp.addChangeListener(this);

		@SuppressWarnings("rawtypes")
		Hashtable labelTableTemp = new Hashtable();
		labelTableTemp.put( new Integer( 950 ), new JLabel("0.950") );
		labelTableTemp.put( new Integer( 999 ), new JLabel("0.999") );
		labelTableTemp.put( new Integer( 990 ), new JLabel("0.99") );
		sliderTempCoef.setLabelTable( labelTableTemp );

		@SuppressWarnings("rawtypes")
		Hashtable labelTable = new Hashtable();
		labelTable.put( new Integer( 1 ), new JLabel("0.1%") );
		labelTable.put( new Integer( 50 ), new JLabel("5%") );
		labelTable.put( new Integer( 100 ), new JLabel("10%") );
		sliderAcceptRate.setLabelTable( labelTable );
		
		@SuppressWarnings("rawtypes")
		Hashtable labelTable2 = new Hashtable();
		labelTable2.put( new Integer( 1 ), new JLabel("1") );
		labelTable2.put( new Integer( 50 ), new JLabel("50") );
		labelTable2.put( new Integer( 100 ), new JLabel("100") );
		sliderFailureThreshold.setLabelTable( labelTable2 );
		
		@SuppressWarnings("rawtypes")
		Hashtable labelTableInit = new Hashtable();
		labelTableInit.put( new Integer( 1 ), new JLabel("1") );
		labelTableInit.put( new Integer( 5 ), new JLabel("5") );
		labelTableInit.put( new Integer( 10 ), new JLabel("10") );
		sliderInitTemp.setLabelTable( labelTableInit );
		
		sliders.add(new JLabel("Temperature coefficient: "));
		sliders.add(sliderTempCoef);

		
		sliders.add(new JLabel("Acceptance rate: "));
		sliders.add(sliderAcceptRate);
		
		sliders.add(new JLabel("Failure threshold: "));
		sliders.add(sliderFailureThreshold);
		
		sliders.add(new JLabel("Initial temp. multiplier: "));
		sliders.add(sliderInitTemp);


		// ********************************************************
		// Infos
		
		JPanel informations = new JPanel(new GridLayout(0, 1));
		Border border3 = BorderFactory.createTitledBorder("Information");
		informations.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0), border3));

		timeTaken = new JLabel("The time taken to compute is " + time);
		totalCost = new JLabel("The total cost of this route is " + cost);

		initialTemp = new JLabel("The initial temperature is " + getInitTemp());
		initialCost  = new JLabel("The initial cost of this route is " + getInitCost());
		
		recap = new JLabel("");

		informations.add(recap);
		informations.add(timeTaken);
		informations.add(totalCost);
		
		informations.add(initialTemp);
		informations.add(initialCost);
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
	
  
    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
        	if(e.getSource() == sliderAcceptRate)
        	{  
        		double value = source.getValue()/1000.;
          
        		System.out.println("Slider Accept rate = " + value);

        	}
        	else if(e.getSource() == sliderTempCoef)
        	{
        		double value = source.getValue()/1000.;

        		System.out.println("Slider temp coef =" + value );

        	}
        	else if(e.getSource() == sliderInitTemp)
        	{
        		double value = source.getValue();

        		System.out.println("Slider init temp =" + value );

        	}
        	else
        	{
        		int value = (int)source.getValue();

        		System.out.println("Slider nb Ite =" + value );

        	}
            
        }
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkGetTSP) {
           if(getTSP)
        	{
        		getTSP = false;
        	}
        	else
        	{
        		getTSP = true;
        	}
        } 
    	
    	
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

			//data.displayMatrix();

			
			//Index 0 is CPLEX
			if(CplexOrAnnealingCombo.getSelectedIndex() == 0)
			{
				System.out.println("                              \r\n" + 
						"  ____ ____  _     _______  __\r\n" + 
						" / ___|  _ \\| |   | ____\\ \\/ /\r\n" + 
						"| |   | |_) | |   |  _|  \\  / \r\n" + 
						"| |___|  __/| |___| |___ /  \\ \r\n" + 
						" \\____|_|   |_____|_____/_/\\_\\\r\n" + 
						"                              ");
				if(isDeter)
				{
					recap.setText("Solving the deter. prob. with CPLEX.");
					TSP problem = new TSP(data, false, false);
					try {
						CPLEXTSP solver = new CPLEXTSP(problem, false);
						solver.solve();
						cost = problem.getCost();
						setInitTemp(-1);
						setInitCost(-1);
					} catch (IloException e1) {
						e1.printStackTrace();
					}
				}
				else
				{
					recap.setText("Stochastic CPLEX not yet implemented!");
					setInitTemp(-1);
					setInitCost(-1);
				}

				
			}
			//Index 1 is the simulated annealing
			else if (CplexOrAnnealingCombo.getSelectedIndex() == 1)
			{
				System.out.println(" .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------. \r\n" + 
						"| .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |\r\n" + 
						"| |  _______     | || |  _________   | || |     ______   | || | _____  _____ | || |     _____    | || |  _________   | |\r\n" + 
						"| | |_   __ \\    | || | |_   ___  |  | || |   .' ___  |  | || ||_   _||_   _|| || |    |_   _|   | || | |  _   _  |  | |\r\n" + 
						"| |   | |__) |   | || |   | |_  \\_|  | || |  / .'   \\_|  | || |  | |    | |  | || |      | |     | || | |_/ | | \\_|  | |\r\n" + 
						"| |   |  __ /    | || |   |  _|  _   | || |  | |         | || |  | '    ' |  | || |      | |     | || |     | |      | |\r\n" + 
						"| |  _| |  \\ \\_  | || |  _| |___/ |  | || |  \\ `.___.'\\  | || |   \\ `--' /   | || |     _| |_    | || |    _| |_     | |\r\n" + 
						"| | |____| |___| | || | |_________|  | || |   `._____.'  | || |    `.__.'    | || |    |_____|   | || |   |_____|    | |\r\n" + 
						"| |              | || |              | || |              | || |              | || |              | || |              | |\r\n" + 
						"| '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |\r\n" + 
						" '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------' ");
			
			
				if(isDeter)
				{
					recap.setText("Solving the deter. prob. with the annealing.");

					//**************************************************************************
					//main Annealing call
					TSP pb = new TSP(data, false, false);
					
					SimulatedAnnealingTSPD solv = new SimulatedAnnealingTSPD(pb,
							(int)Math.pow(data.getNbCity(), 1), 
							sliderFailureThreshold.getValue(), 
							sliderAcceptRate.getValue()/1000., 4, sliderTempCoef.getValue()/1000., sliderInitTemp.getValue());
					solv.solve();
					//**************************************************************************
					
					setInitTemp(solv.initialTemperature);
					setInitCost(solv.initCost);
					cost = LinearProblem.getSol().getAssociatedValue();	
					
				}
				else
				{
					// TODO 

					recap.setText("Solving the stocha. prob. with the annealing.");

					setInitTemp(-991);
					setInitCost(911);
					
					
					
					
					
					
					
					
					
					
				}

			
			
			
			
			}
			else
			{
				System.out.println("No solving! Only displaying.");
				recap.setText("Displaying a file without solving the problem.");

				setInitTemp(-1);
				setInitCost(-1);
			}


			//creating the panel in which the cities will be displayed
			citiesPanel.getData(data);
			
			 long endTime = System.currentTimeMillis();
		       
			System.out.println("--GUI Debug: It took " + (endTime - startTime) + " milliseconds");
			
			time = endTime - startTime;

			
			if(time > 1000){
				time /= 1000;
				timeTaken.setText("The time taken to compute is " + time + " s.");
			}
			else if(time > 60000){
				time /= 60000;
				timeTaken.setText("The time taken to compute is " + time + " mn.");
			}
			else{
				timeTaken.setText("The time taken to compute is " + time + " ms.");
			}
			
			if (initTemp == -1 && initCost == -1)
			{
				initialTemp.setText("");
				initialCost.setText("");
				
			}
			else
			{
				initialTemp.setText("The initial temperature is " + getInitTemp());
				initialCost.setText("The initial cost of this route is " + (int) Math.round(getInitCost()));
				
			}
			
			totalCost.setText("The total cost of this route is " +  Math.round((float)cost) +".");

		
			
			citiesPanel.repaint();

			
        	}
        	else
        	{
        		System.out.println("GUI: No file selected!");
        	}
        } 
        
	
 
        if (e.getSource() == buttonStocha) {
 
			System.out.println("GUI: Solving mode: STOCHASTIC");
			isDeter = false;
			
        } else if (e.getSource() == buttonDeter) {
 
			System.out.println("GUI: Solving mode: DETERMINISTIC");
			isDeter = true;
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
		return sliderFailureThreshold;
	}


	public static void setSliderIteNumber(JSlider sliderIteNumber) {
		GUI.sliderFailureThreshold = sliderIteNumber;
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


	public static boolean isGetTSP() {
		return getTSP;
	}



	public static JCheckBox getCheckGetTSP() {
		return checkGetTSP;
	}


	public static void setCheckGetTSP(JCheckBox checkGetTSP) {
		GUI.checkGetTSP = checkGetTSP;
	}


	public static JSlider getSliderInitTemp() {
		return sliderInitTemp;
	}


	public static void setSliderInitTemp(JSlider sliderInitTemp) {
		GUI.sliderInitTemp = sliderInitTemp;
	}

}