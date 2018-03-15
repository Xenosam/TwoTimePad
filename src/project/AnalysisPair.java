/**
 * 
 */
package project;

/**
 * @author Andrew
 *
 */
public class AnalysisPair {

	String ngram;
	double probability;

	/**
	 * 
	 */
	public AnalysisPair() {
		this.ngram = null;
		this.probability = 0.0;
	}

	/**
	 * 
	 * @param cSeq
	 * @param prob
	 */
	public AnalysisPair(String cSeq, double prob) {
		this.ngram = cSeq;
		this.probability = prob;
	}

	/**
	 * 
	 * @return
	 */
	public String getNGram() {
		return this.ngram;
	}
	
	/**
	 * 
	 * @param newNGram
	 */
	public void setNGram(String newNGram) {
		this.ngram = newNGram;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getProbability() {
		return this.probability;
	}

	public void setProbability(double newProb) {
		this.probability = newProb;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return this.getNGram() + ", " + this.getProbability();
	}
}
