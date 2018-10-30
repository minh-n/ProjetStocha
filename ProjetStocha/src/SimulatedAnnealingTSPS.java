
public class SimulatedAnnealingTSPS extends SimulatedAnnealing{
	
	//k comme k-opt
	private int k;
	
	
	//-------- CONST SET GET --------------------
	
	public SimulatedAnnealingTSPS(LinearProblem pb, int nbIteration, int nbPalierMax, int k)
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
	public void generateNeighbor()
	{
		int changes[] = new int[k];
		for (int i = 0 ; i<k ; i++)
		{
			changes[i] = (int)Math.random()*((DataTSP)this.pb.getData()).getNbCity()-1;
		}
		//choisir k villes parmis les nb villes.
		
		//changer la dest de l'arrette qui part de cette ville puis lui donner
		//la dest de la ville choisie suivante
	}
}