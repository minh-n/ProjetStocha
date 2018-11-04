
public abstract class IterativeAlgorithm
{
	public IterativeAlgorithm() {}
	
	/**
	 * resout une fois un probleme lineaire avec une contrainte supplementaire
	 * @return
	 */
	protected abstract boolean oracle();
	
	/**
	 * appel la methode oracle jusqu'a trouver la solution optimale au probleme 
	 * @return
	 */
	protected abstract Solution loop();
}