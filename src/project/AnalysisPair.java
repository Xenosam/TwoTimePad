/**
 * 
 */
package project;

/**
 * @author Andrew
 *
 */
public class AnalysisPair {

	private String ngram;
	private String ngram2;
	private double probability;
	private String data1;
	private String data2;

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
	 * @param cSeq
	 * @param prob
	 */
	public AnalysisPair(String cSeq, String cSeq2, double prob) {
		this.ngram = cSeq;
		this.ngram2 = cSeq2;
		this.probability = prob;
	}
	
	/**
	 * 
	 * @param cSeq
	 * @param prob
	 * @param f
	 */
	public AnalysisPair(String cSeq, String cSeq2, double prob, String d1, String d2) {
		this.ngram = cSeq;
		this.ngram2 = cSeq2;
		this.probability = prob;
		this.data1 = d1;
		this.data2 = d2;
	}

	public String getData1() {
		return this.data1;
	}
	
	public String getData2() {
		return this.data2;
	}
	
	public void setData1(String d) {
		this.data1 = d;
	}
	
	public void setData2(String d) {
		this.data2 = d;
	}
	
	/**
	 * 
	 * @param c1
	 * @param c2
	 */
	public void addData(char c1, char c2) {
		this.data1 += c1;
		this.data2 += c2;
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getData() {
		return new String[] {data1, data2};
	}
	
	/**
	 * 
	 * @param f
	 */
	public void setData(String d1, String d2) {
		this.data1 = d1;
		this.data2 = d2;
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
	public String getNGram2() {
		return this.ngram2;
	}
	
	/**
	 * 
	 * @param newNGram
	 */
	public void setNGram2(String newNGram) {
		this.ngram2 = newNGram;
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
		String out = "";
		out += "NGRAM A: " + this.getNGram();
		out += "\nNGRAM B: " + this.getNGram2();
		out += "\nDATA A: " + this.getData()[0];
		out += "\nDATA B: " + this.getData()[1];
		out += "\nPROB: " + this.getProbability();
		return out;
	}
}
