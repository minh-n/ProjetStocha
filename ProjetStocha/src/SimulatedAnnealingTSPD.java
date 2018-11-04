import java.util.Arrays;
import java.util.ArrayList;

public class SimulatedAnnealingTSPD extends SimulatedAnnealing{
	
	//k comme k-opt
	private int k;
	private double coefTemp;
	private int initTempCoef;
	//-------- CONST SET GET --------------------
	
	
	/**
	 * @param k : will decide which k-opt to use (currently, it can be 2, 3 or 4)
	 */
	public SimulatedAnnealingTSPD(LinearProblem pb, int nbIteration, int failureThreshold, double acceptationThreshold, int k, double coefTemp, int initTempCoef)
	{
		
		super(pb, nbIteration, failureThreshold, acceptationThreshold);
		this.k = k;
		this.coefTemp = coefTemp;
		this.initTempCoef = initTempCoef;

	}
	
	
	
	//------------- METHODS -----------------------

	/* (non-Javadoc)
	 * @see SimulatedAnnealing#initTemp()
	 * We chose to implement the KirkPatrick algorithm
	 * to set an initial temperature
	 */
	public void initTemp ()
	{
		//TODO ptetre changer cette valeur
		this.currentTemperature = 10;
		do
		{
			//System.out.println(this.acceptationRate);
			processTier();
			this.currentTemperature *= 2;
		}while (this.acceptationRate < 0.8);
		
		
		if (this.acceptationRate > 0.95)
			this.currentTemperature /= 2;
		
		this.initialTemperature = this.currentTemperature*initTempCoef;
	}
	
	
	/* (non-Javadoc)
	 * @see SimulatedAnnealing#changeTemp()
	 */
	public void changeTemp()
	{
		this.currentTemperature *= coefTemp;
	}
	
	public void changeTemp(double val)
	{
		this.currentTemperature = val;
	}
	
	
	
	/* (non-Javadoc)
	 * @see SimulatedAnnealing#initSol()
	 * Here was implemented the algorithm of the
	 * nearest neighbor
	 */
	public SolutionTSP initSol()
	{
		
		// VILLE 1 PUIS VILLE 2 PUIS VILLE 3 ETC...
		SolutionTSP sol = new SolutionTSP();
		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		int track[] = new int[nbCity];
		
		for (int i=0 ; i<nbCity ; i++)
			track[i] = i;
		
		sol.setTrack(track);
		sol.setAssociatedValue(this.pb.objectiveFunction(sol));
		initCost = sol.getAssociatedValue();

		return sol;
		
		/*//PLUS PROCHE VOISIN
		SolutionTSP sol = new SolutionTSP();
		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		int solVal[] = new int[nbCity];
		ArrayList<Integer> track = new ArrayList<Integer>();
		double lowestCost = 10000000;
		int idxLowest = -1;
		
		track.add(0);
		solVal[0] = 0;
		
		double matCost[][] = ((DataTSP)this.pb.getData()).getMatrixCost();
		
		for (int i=0 ; i<nbCity-1 ; i++)
		{
			lowestCost = 1000000;
			idxLowest = -1;
			for (int j=0 ; j<nbCity ; j++)
			{
				if (!track.contains(j) & (matCost[track.get(i)][j] < lowestCost))
				{
					lowestCost = matCost[track.get(i)][j];
					idxLowest = j;
				}
			}
			
			track.add(idxLowest);
			solVal[i+1] = idxLowest;
			
		}
		
		sol.setTrack(solVal);
		sol.setAssociatedValue(this.pb.objectiveFunction(sol));
		initCost = sol.getAssociatedValue();

		return sol;
*/
	}
	
	
	
	/* (non-Javadoc)
	 * @see SimulatedAnnealing#generateNeighbor()
	 */
	public SolutionTSP generateNeighbor()
	{
		SolutionTSP neighbor;
		switch (this.k)
		{
			case 3 :
				neighbor = opt3();
			case 4 :
				neighbor = opt4();
			default:
				neighbor = opt2();
		}
			
		neighbor.setAssociatedValue(this.pb.objectiveFunction(neighbor));
		return neighbor;
		
	}
	
	
	
	/**
	 * @return : returns a new SolutionTSP found via a 2-opt exchange
	 */
	public SolutionTSP opt2()
	{
		SolutionTSP neighbor = ((SolutionTSP)this.currentSol).cloneSol();
		
		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		int city1, city2;
		
		city1 = (int)(Math.random()*(nbCity));
		do
		{
			city2 = (int)(Math.random()*(nbCity));
		}while(city2 == city1);
		
		//on change le sens de parcours d'une partie du chemin
		neighbor.reverseTrack(city1, city2);
	
		//et on fait notre raccord croie
		
		return neighbor;
	}
	
	/**
	 * @return : returns a new SolutionTSP found via a 3-opt exchange
	 */
	public SolutionTSP opt3()
	{
		SolutionTSP neighbor = ((SolutionTSP)this.currentSol).cloneSol();
		int track[] = neighbor.getTrack();
		ArrayList<Integer> subtrack1 = new ArrayList<Integer>();
		ArrayList<Integer> subtrack2 = new ArrayList<Integer>();

		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		int cities[] = new int[3];
		int idx;
		
		cities[0] = (int)(Math.random()*(nbCity));
		
		do
		{
			cities[1] = (int)(Math.random()*(nbCity));
		}while(cities[1] == cities[0]);
		
		do
		{
			cities[2] = (int)(Math.random()*(nbCity));
		}while(cities[2] == cities[0] | cities[2] == cities[1]);
		
		Arrays.sort(cities);
		for (idx = cities[0]+1 ; idx <= cities[1] ; idx++)
			subtrack1.add(track[idx]);
		
		for (idx = cities[1]+1 ; idx <= cities[2] ; idx++)
			subtrack2.add(track[idx]);
		
		subtrack2.addAll(subtrack1);
		
		idx = cities[0]+1;
		while (subtrack2.size() != 0)
		{
			track[idx] = subtrack2.get(0);
			subtrack2.remove(0);
			idx++;
		}	
		
		return neighbor;
	}
	
	/**
	 * @return : returns a new SolutionTSP found via a 3-opt exchange
	 */
	public SolutionTSP opt4()
	{
		SolutionTSP neighbor = ((SolutionTSP)this.currentSol).cloneSol();
		int track[] = neighbor.getTrack();
		ArrayList<Integer> subtrack1 = new ArrayList<Integer>();
		ArrayList<Integer> subtrack2 = new ArrayList<Integer>();
		ArrayList<Integer> subtrack3 = new ArrayList<Integer>();

		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		int cities[] = new int[4];
		int idx;
		
		cities[0] = (int)(Math.random()*(nbCity));
		
		do
		{
			cities[1] = (int)(Math.random()*(nbCity));
		}while(cities[1] == cities[0]);
		
		do
		{
			cities[2] = (int)(Math.random()*(nbCity));
		}while(cities[2] == cities[0] | cities[2] == cities[1]);
		
		do
		{
			cities[3] = (int)(Math.random()*(nbCity));
		}while(cities[3] == cities[0] | cities[3] == cities[1] | cities[3] == cities[2]);
		
		Arrays.sort(cities);
		for (idx = cities[0]+1 ; idx <= cities[1] ; idx++)
			subtrack1.add(track[idx]);
		
		for (idx = cities[1]+1 ; idx <= cities[2] ; idx++)
			subtrack2.add(track[idx]);
		
		for (idx = cities[2]+1 ; idx <= cities[3] ; idx++)
			subtrack3.add(track[idx]);
		
		subtrack3.addAll(subtrack2);
		subtrack3.addAll(subtrack1);
		
		idx = cities[0]+1;
		while (subtrack3.size() != 0)
		{
			track[idx] = subtrack3.get(0);
			subtrack3.remove(0);
			idx++;
		}	
		
		return neighbor;
	}
	
}