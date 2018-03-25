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
	 * Constructor for an empty ngram-probability pair
	 */
	public AnalysisPair() {
		this.ngram = null;
		this.probability = 0.0;
	}

	/**
	 * Constructor for a single ngram-probability pair
	 * 
	 * @param cSeq
	 *            the ngram
	 * @param prob
	 *            the probability of the ngram
	 */
	public AnalysisPair(String cSeq, double prob) {
		this.ngram = cSeq;
		this.probability = prob;
	}

	/**
	 * Constructor for an ngram pair and their combined probability
	 * 
	 * @param cSeq
	 *            the first ngram
	 * @param cSeq2
	 *            the second ngram
	 * @param prob
	 *            the total probability
	 */
	public AnalysisPair(String cSeq, String cSeq2, double prob) {
		this.ngram = cSeq;
		this.ngram2 = cSeq2;
		this.probability = prob;
	}

	/**
	 * Constructor for an ngram pair, their combined probability and 2 data
	 * strings for holding current decryptions
	 * 
	 * @param cSeq
	 *            the first ngram
	 * @param cSeq2
	 *            the second ngram
	 * @param prob
	 *            the total probability
	 * @param d1
	 *            the current output string for plaintext a
	 * @param d2
	 *            the current output string for plaintext b
	 */
	public AnalysisPair(String cSeq, String cSeq2, double prob, String d1, String d2) {
		this.ngram = cSeq;
		this.ngram2 = cSeq2;
		this.probability = prob;
		this.data1 = d1;
		this.data2 = d2;
	}

	/**
	 * returns the value of the objects data1
	 * 
	 * @return the output string for plaintext a
	 */
	public String getData1() {
		return this.data1;
	}

	/**
	 * returns the value of the objects data2
	 * 
	 * @return the output string for plaintext b
	 */
	public String getData2() {
		return this.data2;
	}

	/**
	 * sets a new value of the objects data1
	 * 
	 * @param d
	 *            the new plaintext a value
	 */
	public void setData1(String d) {
		this.data1 = d;
	}

	/**
	 * sets a new value of the objects data2
	 * 
	 * @param d
	 *            the new plaintext b value
	 */
	public void setData2(String d) {
		this.data2 = d;
	}

	/**
	 * Adds a character to the end of both data strings held by the object to
	 * reflect the current ngram
	 * 
	 * @param c1
	 *            new data1 character
	 * @param c2
	 *            new data2 character
	 */
	public void addData(char c1, char c2) {
		this.data1 += c1;
		this.data2 += c2;
	}

	/**
	 * Returns both data strings in an array
	 * 
	 * @return length 2 array wher [0] = data1 and [1] = data2
	 */
	public String[] getData() {
		return new String[] { data1, data2 };
	}

	/**
	 * Sets both data strings at once
	 * 
	 * @param d1
	 *            new data1 value
	 * @param d2
	 *            new data2 value
	 */
	public void setData(String d1, String d2) {
		this.data1 = d1;
		this.data2 = d2;
	}

	/**
	 * Returns the current string for ngram1
	 * 
	 * @return current plaintext a ngram
	 */
	public String getNGram() {
		return this.ngram;
	}

	/**
	 * Sets ngram1 to the value given as input
	 * 
	 * @param newNGram
	 *            the new value for ngram1
	 */
	public void setNGram(String newNGram) {
		this.ngram = newNGram;
	}

	/**
	 * Returns the current string for ngram2
	 * 
	 * @return current plaintext b ngram
	 */
	public String getNGram2() {
		return this.ngram2;
	}

	/**
	 * Sets ngram2 to the value given as input
	 * 
	 * @param newNGram
	 *            the new value for ngram2
	 */
	public void setNGram2(String newNGram) {
		this.ngram2 = newNGram;
	}

	/**
	 * Returns the probability of the object
	 * 
	 * @return the probability of the object
	 */
	public double getProbability() {
		return this.probability;
	}

	/**
	 * sets the probability of the object to the value given as input
	 * 
	 * @param newProb
	 *            the new value for the probability
	 */
	public void setProbability(double newProb) {
		this.probability = newProb;
	}

	/**
	 * Returns a string representation of the object
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
