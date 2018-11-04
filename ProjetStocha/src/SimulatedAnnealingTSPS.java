public class SimulatedAnnealingTSPS extends SimulatedAnnealing{
	
	//k comme k-opt
	private int k;
	
	
	//-------- CONST SET GET --------------------
	
	public SimulatedAnnealingTSPS(LinearProblem pb, int nbIteration, int nbPalierMax, double acceptationThreshold, int k)
	{
		
		super(pb, nbIteration, nbPalierMax, acceptationThreshold);
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
		
		
		public void changeTemp()
		{
			this.currentTemperature *= 0.9;
		}
		
		
		//K-OPT
		public SolutionTSP generateNeighbor()
		{
			do
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
			}while(false/*TODO tant que la condition probabiliste n'est pas respectée*/);
					
		}
		
		
		//chemin initial : ville 1 puuis ville 2 puis ville 3 etc...
		public SolutionTSP initSol()
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
			
			return sol;
		}
		
		
		public SolutionTSP opt2()
		{
			SolutionTSP neighbor = new SolutionTSP(((SolutionTSP)this.currentSol).getSol());
			
			int nbCity = ((DataTSP)this.pb.getData()).getNbCity();
			int city1, city2;
			int city1Dest = -1; 
			int city2Dest = -1;
			
			int temp;
			
			do
			{
				temp = 0;
				city1 = (int)Math.random()*(nbCity-1);
				city2 = (int)Math.random()*(nbCity-1);
				
				//on regarde quelle ville est atteinte en premier dans le parcours
				while (temp != city1 & temp != city2)
				{
					temp = neighbor.nextCity(temp);
				}
				
				//si 2 est atteint avant 1, on échange les deux
				if (temp == city2)
				{
					city2 = city1;//on regarde quelle ville est atteinte en premier dans le parcours
					city1 = temp;
				}
			}while(city2 != neighbor.nextCity(city1));
			//2opt ne marche pas si c'est deux villes consécutives qui sont choisies
			
			city1Dest = neighbor.nextCity(city1);
			city2Dest = neighbor.nextCity(city2);
			
			//on change le sens de parcours d'une partie du chemin
			neighbor.reverseTrack(city1Dest, city2);
			
			//et on fait notre raccord croisé
			
			
			
			
			
			
			
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
	
}