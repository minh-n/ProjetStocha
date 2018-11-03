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
		switch (this.k)
		{
			case 3 :
				return opt3();
			case 4 :
				return opt4();
			default:
				return opt2();
		}
				
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
	
	
	public SolutionTSP opt2()
	{
		//TODO GOFAIRE
		return new SolutionTSP();
	}
	
	public SolutionTSP opt3()
	{
		SolutionTSP neighbor = new SolutionTSP(((SolutionTSP)this.currentSol).getSol());
		boolean newval = true;
		int changes[] = new int[3];
		int destChanges[] = new int[3];
		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		
		for (int i = 0 ; i<3 ; i++)
		{
			newval = true;
			do
			{
				//on choisi une ville
				changes[i] = (int)Math.random()*(nbCity-1);
				//on rechoisis si elle existe deja dans les villes choisies
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
		for (int i= 0 ; i<3 ; i++)
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
	
		for (int i= 0 ; i<3 ; i++)
		{
			if (i+1 == 3)
				neighbor.getSol()[changes[i]][destChanges[0]] = 1;
			else
				neighbor.getSol()[changes[i]][destChanges[i+1]] = 1;
		}

		return neighbor;
	}
	
	public SolutionTSP opt4()
	{
		SolutionTSP neighbor = new SolutionTSP(((SolutionTSP)this.currentSol).getSol());
		boolean newval = true;
		int changes[] = new int[4];
		int destChanges[] = new int[4];
		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		
		for (int i = 0 ; i<4 ; i++)
		{
			newval = true;
			do
			{
				//on choisi une ville
				changes[i] = (int)Math.random()*(nbCity-1);
				//on rechoisis si elle existe deja dans les villes choisies
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
		for (int i= 0 ; i<4 ; i++)
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
	
		for (int i= 0 ; i<4 ; i++)
		{
			if (i+1 == 4)
				neighbor.getSol()[changes[i]][destChanges[1]] = 1;
			else if (i+2 == 4)
				neighbor.getSol()[changes[i]][destChanges[0]] = 1;
			else
				neighbor.getSol()[changes[i]][destChanges[i+2]] = 1;
		}

		return neighbor;
	}
	
	
	//NE PAS OUBLIER DE RACCORDER CE CHEMIN INVERSé AVEC LE RESTE DU TOUT
	//car en fin de fonction, city1 ne va nulle part, et rien ne se dirige vers city2.
	public void reverseTrack(int city1, int city2)
	{
		int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
		
		//-1 n'a aucune importance
		int temp = -1;
		int currentCity = city1;
		
		while (temp != city2)
		{
			
			for (int i=0 ; i<nbCity ; i++)
			{
				if (((SolutionTSP)this.currentSol).getSol()[currentCity][i] == 1)
				{
					((SolutionTSP)this.currentSol).getSol()[currentCity][i] = 0;
						
					if (currentCity != city1)
						((SolutionTSP)this.currentSol).getSol()[currentCity][temp] = 1;
					temp = currentCity;
					currentCity = i;
					break;
				}
				
				if (i+1 == nbCity)
					System.out.println("ERROR : no track found");
			}
			
		}
	}
	
	
	
	
}