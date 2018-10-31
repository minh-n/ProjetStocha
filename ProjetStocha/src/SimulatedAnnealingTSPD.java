public class SimulatedAnnealingTSPD extends SimulatedAnnealing{
	
	//k comme k-opt
	private int k;
	
	
	//-------- CONST SET GET --------------------
	
	public SimulatedAnnealingTSPD(LinearProblem pb, int nbIteration, int nbPalierMax, int k)
	{
		
		super(pb, nbIteration, nbPalierMax);
		this.k = k;
		
	}
	
	
	
	//------------- METHODS -----------------------
	
	//KirkPatrick
	public void initTemp ()
	{
		//TODO ptetre changer cette valeur
		int init = 10;
		do
		{
			processTier();
			init *= 2;
		}while (this.acceptationRate < 0.8);
		
		
		if (this.acceptationRate > 0.95)
			init /= 2;
		
		this.initialTemperature = init;
	}
	

	
	
	//K-OPT
	public SolutionTSP generateNeighbor()
	{
		SolutionTSP neighbor = new SolutionTSP(((SolutionTSP)this.currentSol).getSol());
		boolean newval = true;
		int changes[] = new int[k];
		int destChanges[] = new int[k];
		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		
		for (int i = 0 ; i<k ; i++)
		{
			newval = true;
			do
			{
				//on choisi une ville
				changes[i] = (int)Math.random()*(nbCity-1);
				//on rechoisis si elle existe déjà dans les villes choisies
				for (int j = 0 ; j<i ; j++)
				{
					if (changes[i] == changes[j])
					{
						newval = false;
						break;
					}
						
				}
			}while (newval == false);
			
		}
		
		//on récupère le lien partant de chacune des villes choisies
		for (int i= 0 ; i<k ; i++)
		{
			for (int j= 0 ; j<nbCity ; j++)
			{
				if (neighbor.getSol()[changes[i]][j] == 1)
				{
					//on l'enregistre dans un truc tampon
					destChanges[i] = j;
					//on le supprime de la solution
					neighbor.getSol()[changes[i]][j] = 0;
				}
			}
		}
	
		for (int i= 0 ; i<k ; i++)
		{
			if (i+1 == k)
				neighbor.getSol()[changes[i]][destChanges[0]] = 1;
			else
				neighbor.getSol()[changes[i]][destChanges[i+1]] = 1;
		}

		return neighbor;
	}
	
	
	//chemin initial : ville 1 puuis ville 2 puis ville 3 etc...
	public void initSol()
	{
		SolutionTSP sol = new SolutionTSP();
		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		int solMat[][] = new int[nbCity][nbCity];
		
		for (int i= 0 ; i<nbCity ; i++)
		{
			for (int j= 0 ; j<nbCity ; j++)
			{
				if (i+1 == nbCity && j == 0)
					solMat[i][j] = 1;
				
				else
				{
					if (j == i+1 && i+1 < nbCity)
						solMat[i][j] = 1;
				
					else
						solMat[i][j] = 0;
				}
			}
		}
		
		sol.setSol(solMat);
		
		this.currentSol = sol;
	}
	
}