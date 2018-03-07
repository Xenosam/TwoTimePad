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
	public String getNgram() {
		return this.ngram;
	}

	/**
	 * 
	 * @return
	 */
	public double getProbability() {
		return this.probability;
	}

	/**
	 * 
	 */
	public String toString() {
		return this.getNgram() + ", " + this.getProbability();
	}
}
