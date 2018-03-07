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
	
	public AnalysisPair() {
		this.ngram = null;
		this.probability = 0.0;
	}
	
	public AnalysisPair(String cSeq, double prob) {
		this.ngram = cSeq;
		this.probability = prob;
	}
	
	public String getNgram() {
		return this.ngram;
	}
	
	public double getProbability(){
		return this.probability;
	}
	
	public String toString() {
		return this.getNgram() + ", " + this.getProbability();
	}
}
