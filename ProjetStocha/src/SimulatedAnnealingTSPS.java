import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * @author cmans
 *
 */
public class SimulatedAnnealingTSPS extends SimulatedAnnealing{
	
	//k comme k-opt
	private int k;
	private double coefTemp;
	private int initTempCoef;
	public NormalDistribution normal;
	
	//alpha is the value associated to the probability constraint
	private double alpha;
	private double varCovar[];
	//solution to the deterministic problem
	private double z;
	public long accepted;
	public long rejected;
	
	//-------- CONST SET GET --------------------
	
	public SimulatedAnnealingTSPS(LinearProblem pb, int nbIteration, int nbPalierMax, double acceptationThreshold, int k, double coefTemp, int initTempCoef, double alpha)
	{
		
	
		super(pb, nbIteration, nbPalierMax, acceptationThreshold);
		this.k = k;
		normal = new NormalDistribution();
		this.alpha = alpha;
		this.accepted = 0;
		this.rejected = 0;
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
		do
		{
			switch (this.k)
			{
				case 3 :
					neighbor = opt3();
				case 4 :
					neighbor = opt4();
				default:
					neighbor = opt2();
			}
		}while(!testNeighbor(neighbor));
			
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
	
	
	
	/**
	 * BuildVarCovar generates a covariance matrix from scratch
	 * We use the hypothesis that all the random variables are independant
	 * Therefore, all the coefficients other than the diagonal ones
	 * are null, which allow us to represent the matrix as a vector
	 * each ark is given a random variance
	 * 
	 */
	public void buildVarCovar()
	{
		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		double varCovar[] = new double[nbCity*nbCity];
		
		for (int i = 0 ; i < nbCity*nbCity ; i++)
		{
			varCovar[i] = Math.random()*100+46;
		}
		
		this.varCovar = varCovar;
	}
	
	
	
	/**
	 * @return : returns the sum of the variances of the arks used in the solution
	 */
	public double sumVariance()
	{
		int track[] = ((SolutionTSP)this.currentSol).getTrack();
		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		double sumVar = 0;
		
		for (int i = 0 ; i < nbCity ; i++)
		{
			sumVar += this.varCovar[track[i]*nbCity+((SolutionTSP)this.currentSol).nextCity(track[i])];
		}
		
		//System.out.println(sumVar);
		return sumVar;
	}
	
	
	/**
	 * Returns true if the neighbor solution satisfy the probabilistic constraint
	 */
	public boolean testNeighbor(SolutionTSP neighbor)
	{
		double quantil = normal.inverseCumulativeProbability(alpha);
		buildVarCovar();
		
		double rst = this.currentSol.associatedValue+Math.sqrt(sumVariance())*quantil;
		this.z = currentSol.associatedValue;
		
		if (rst < z*1.01)
		{
			this.accepted+=1;
			return true;
		}
		else
		{
			this.rejected+=1;
			return false;
		}
		
	}
	
	
	
}