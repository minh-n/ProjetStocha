import java.util.ArrayList;


public class SimulatedAnnealingTSPD extends SimulatedAnnealing
{
	
	private ArrayList<ArrayList<Boolean>> neighbor;
	//for k-opt
	private int k;
	
	
	public SimulatedAnnealingTSPD()
	{
		super();
	}
	
	public void initTemp()
	{
		//coder l'algo voulu pour le choix de la temp
		this.initialTemperature = 50;
	}
	
	public void generateNeighbor()
	{
		//TODO stuff : get the tempSol from the problem, and use K-opt to generate a new sol
		ArrayList<ArrayList<Boolean>> sol = new ArrayList<ArrayList<Boolean>>();
		sol.add(new ArrayList<Boolean>());
		sol.add(new ArrayList<Boolean>());
		sol.get(0).add(true);
		sol.get(0).add(false);
		sol.get(1).add(false);
		sol.get(1).add(true);
		
		this.neighbor = sol;
	}
	
	
	
}