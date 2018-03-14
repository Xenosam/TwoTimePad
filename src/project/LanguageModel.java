package project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.aliasi.lm.NGramProcessLM;

/**
 * @author Andrew
 *
 */
public class LanguageModel {

	/**
	 * Main method for controlling execution flow
	 * 
	 * @param args
	 *            command line arguements fed to the program
	 */
	public static void main(String[] args) {
		// TODO Change n declaration to command line arguement
		int n = 3;
		if (n < 3) {
			if (n == 0) {
				// TODO Properly implement load from file
				loadFromFile(args[2]);
			}
			// TODO implement failsafe for impossible n
		}

		// Creates java lingpipe model form NGram Language Modelling
		NGramProcessLM model = createModel(n);
		NGramProcessLM model2 = createModel(n - 1);
		NGramProcessLM smoothing;
		NGramProcessLM smoothing2;

		// Searches Directory for each file
		final File file = new File("C:/Users/Andrew/workspace/TwoTimeNLM/resources/corpus/");
		for (final File child : file.listFiles()) {
			// Calls train method for each file
			System.out.println(child.getName());
			model = train(model, file.toString() + "/" + child.getName());
			model2 = train(model2, file.toString() + "/" + child.getName());
		}

		// Initialize models for smoothing
		smoothing = model;
		smoothing2 = model2;

		// Laplace smoothing
		smoothing = smoothingLaplace(smoothing, n);
		smoothing = smoothingLaplace(smoothing2, (n - 1));

		if (args[3].toString() == "save") {
			// TODO Properly implement save
			saveToFile("3gramModel", model);
			saveToFile("2gramModel", model2);
			saveToFile("3gramLaplace", smoothing);
			saveToFile("2gramLaplace", smoothing2);
		}

	}

	/**
	 * Takes a given file that represents the XOR of two plaintexts and uses a
	 * language model to produce a walk through the hidden markov model and
	 * solve for the original plaintexts
	 * 
	 * @param message
	 *            the input file to be decrypted
	 * @param model
	 *            the language model being used
	 * @param L
	 *            the amount of potential paths to remember
	 */
	public static void solver(File message, NGramProcessLM model, int L) {
		// Create strings for each plaintext
		char[] strA = new char[model.maxNGram()];
		char[] strB = new char[model.maxNGram()];
		// Create file reader for the given input and step through each line
		int currentChar;
		char c;
		char[] cXOR;
		double[] score = new double[256];
		AnalysisPair[] aPA = new AnalysisPair[256];
		AnalysisPair[] aPB = new AnalysisPair[256];
		// Try / Catch IOException
		try {
			BufferedReader br = new BufferedReader(new FileReader(message));
			while ((currentChar = br.read()) != -1) {
				// TODO Implement Process
				// Blind walk from 0 - n
				if (strA[model.maxNGram() - 1] == 0) {
					if (strA[model.maxNGram() - 2] != 0) {
						// END OF BLIND
					} else {
						// BLIND WALK
						continue;
					}
				} else {
					// If string is full then trim
					for (int i = model.maxNGram() - 2; i >= 0; i--) {
						// Remove least significant character
						strA[i + 1] = strA[i];
						strB[i + 1] = strB[i];
					}
				}
				// update c
				c = (char) currentChar;
				// create XOR map
				cXOR = XORHandler(c);
				// Add each to a running string
				for (int i = 0; i < 256; i++) {
					// Add relevant letter to string
					strA[model.maxNGram() - 1] = (char) i;
					strB[model.maxNGram() - 1] = cXOR[i];
					// Add statistics to model
					AnalysisPair temp;
					temp = new AnalysisPair(strA.toString(), model.prob(strA.toString()));
					aPA[i] = temp;
					temp = new AnalysisPair(strB.toString(), model.prob(strB.toString()));
					aPB[i] = temp;
					// Score Strings: score[i] -> aPA[i] + aPB[i]
					score[i] = model.prob(strA.toString()) + model.prob(strB.toString());

				}
				// Sort Score
				score = quickSort(0, score.length - 1, score);
				// Filter top L results
			}
			br.close();
		} catch (IOException e) {
			// Handler for IOException
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		// Store plaintexts to file
		// TODO decide output format
		// return output;
	}

	/**
	 * Performs a recursive QuickSort over a given array of doubles
	 * 
	 * @param low
	 *            the lowest index of the division (initially should be 0)
	 * @param high
	 *            the highest index of the division (initially should be length
	 *            - 1)
	 * @param output
	 *            the array to be sorted
	 * @return the sorted array
	 */
	public static double[] quickSort(int low, int high, double[] output) {
		// Establish Low, High and Middle(The Pivot)
		int i = low;
		int j = high;
		// Take Value from the middle
		double pivot = output[low + (high - low) / 2];

		// Sort Loop
		while (i <= j) {
			// Increase low pointer
			while (output[i] < pivot) {
				i++;
			}

			// Decrease high pointer
			while (output[j] > pivot) {
				j--;
			}

			// Swap the positions of the high and low
			if (i <= j) {
				double temp = output[i];
				output[i] = output[j];
				output[j] = temp;
				i++;
				j--;
			}
		}

		// Recursion
		if (low < j) {
			// Create new division from low to j
			quickSort(low, j, output);
		}
		if (i < high) {
			// Create new division from i to high
			quickSort(i, high, output);
		}

		// Return
		return output;
	}

	/**
	 * Returns a character array with every possible combination of characters
	 * the XOR to the character given as the input
	 * 
	 * @param input
	 *            the XOR character to be split
	 * @return a character array of each possible character combination
	 */
	public static char[] XORHandler(char input) {
		// char[x][0] = ptA, char[x][1] = ptB
		char[] output = new char[256];
		char ptB;
		// attempt all 256 possible for output = i ^ ptB
		for (int i = 0; i < 256; i++) {
			// c = a ^ b, a = c ^ b, b = c ^ a
			ptB = (char) (input ^ i);
			// ptA = i
			output[i] = ptB;
		}
		return output;
	}

	/**
	 * Method for creation and initialization of new NGramProcessLM models
	 * 
	 * @param n
	 *            The length of the n-grams created for the model
	 * @return An initialized NGramProcessLM model
	 */
	public static NGramProcessLM createModel(int n) {
		NGramProcessLM model = new NGramProcessLM(n);
		return model;
	}

	/**
	 * Creates a model using the AnalysisPair class based off of a preexistant
	 * NGramProcessLM model
	 * 
	 * @param model
	 *            The NGramProcessLM model to take the information from
	 * @param n
	 *            The length of the NGram
	 * @return An array of String,Double pairs that represents each ngram and
	 *         its percentage chance of appearance
	 */
	public static AnalysisPair[] createAPModel(NGramProcessLM model, int n) {
		// Will require a lot of memory when n > 4
		if (n > 3) {
			System.out.println("This may require allocating more memory");
		}
		char[] cSeq = new char[n];
		int temp;
		String tempS;
		// Fill string with ASCII(0) characters
		for (int i = 0; i < n; i++) {
			cSeq[i] = Character.valueOf((char) 0);
		}
		// Create neccessary amount of space
		AnalysisPair[] aP = new AnalysisPair[(int) Math.pow(256, n)];
		int j = n - 1;
		for (int i = 0; i < aP.length; i++) {
			// Moves onto the next significant character if it's 256
			while (Integer.valueOf((int) cSeq[j]) == 256) {
				cSeq[j] = Character.valueOf((char) 0);
				j--;
			}
			// Steps the character
			temp = Integer.valueOf((int) cSeq[j]);
			cSeq[j] = Character.valueOf((char) (temp + 1));
			tempS = String.valueOf(cSeq);
			// Adds result to array and checks probability
			aP[i] = new AnalysisPair(tempS, model.prob(tempS));
			// Moves back to least significant character
			j = n - 1;

		}
		return aP;
	}

	/**
	 * Method for training a given model by reading each line of the given file
	 * and constructing probabilities for each possible ngram
	 * 
	 * @param model
	 *            The model to be trained
	 * @param filename
	 *            The filename of the file to be read
	 * @return A trained NGramProcessLM model
	 */
	public static NGramProcessLM train(NGramProcessLM model, String filename) {
		File f = new File(filename);

		// Attempts to create a Buffered Reader to parse file
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			// For each line of the file train the model and output to console
			for (String line; (line = br.readLine()) != null;) {
				model.train(line);
				// System.out.println(line);
			}
			br.close();
			return model;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method for returning the topX most likely NGrams for a given N-1Gram for
	 * a given model. Currently the model looks at letters between 32 and 126
	 * for testing
	 * 
	 * @param input
	 *            The N-1Gram to be expanded
	 * @param model
	 *            The model for the expected probabilities
	 * @param topX
	 *            The amount of results to be returned
	 * @return An array of pairs of length topX that contains a given string and
	 *         its associated probability
	 */
	public static AnalysisPair[] likelyNGrams(String input, NGramProcessLM model, int topX) {
		AnalysisPair[] aP = new AnalysisPair[256];
		if (input.length() != model.maxNGram() - 1) {
			System.out.println("Input string has the wrong amount of characters: Required - " + (model.maxNGram() - 1)
					+ ", Entered - " + input.length());
			return null;
		}
		// Read all possible results for NGram extension (n-1 to n)
		for (int i = 0; i < 256; i++) {
			String s = input + (char) (i);
			aP[i] = new AnalysisPair(s, model.prob(s));
		}
		// Sort in order of probability (Bubble Sort)
		AnalysisPair temp;
		for (int i = 0; i < 256; i++) {
			for (int j = 1; j < (256 - i); j++) {

				if (aP[j - 1].getProbability() < aP[j].getProbability()) {
					temp = aP[j - 1];
					aP[j - 1] = aP[j];
					aP[j] = temp;
				}

			}
		}
		// Print results
		AnalysisPair[] output = new AnalysisPair[topX];
		for (int i = 0; i < topX; i++) {
			output[i] = new AnalysisPair(aP[i].getNgram(), aP[i].getProbability());
			// OUTPUT: System.out.println("Rank " + (i + 1) + ": " +
			// aP[i].toString());
		}
		// Return Sorted NGram information
		return output;
	}

	/**
	 * Adds a single appearance to every item in the model
	 * 
	 * @param prob
	 *            The double to be smoothed
	 * @param count
	 *            The total amount of results from the model
	 * @return The smoothed double
	 */
	public static NGramProcessLM smoothingLaplace(NGramProcessLM model, int n) {
		// Follows the same process as the AP model production
		char[] cSeq = new char[n];
		int temp;
		String tempS;
		// Fill string with ASCII(0) characters
		for (int i = 0; i < n; i++) {
			cSeq[i] = Character.valueOf((char) 0);
		}
		int j = n - 1;
		for (int i = 0; i < Math.pow(256, n); i++) {
			// Moves onto the next significant character if it's 256
			while (Integer.valueOf((int) cSeq[j]) == 256) {
				cSeq[j] = Character.valueOf((char) 0);
				j--;
			}
			// Steps the character
			temp = Integer.valueOf((int) cSeq[j]);
			cSeq[j] = Character.valueOf((char) (temp + 1));
			tempS = String.valueOf(cSeq);
			// Trains for an extra result
			model.train(tempS);
			// Moves back to least significant character
			j = n - 1;
		}
		// Returns the smoothed model
		return model;
	}

	/**
	 * This method is for saving an NGramProcessLM model to the disc
	 * 
	 * @param filename
	 *            The name of the file to be created
	 * @param model
	 *            The model being exported
	 */
	public static void saveToFile(String filename, NGramProcessLM model) {
		// Create File
		File f = new File("C:/Users/Andrew/workspace/TwoTimeNLM/resources/models/" + filename + ".txt");
		OutputStream out;
		try {
			// Establish Output Stream
			out = new FileOutputStream(f);
			model.writeTo(out);
			BufferedOutputStream bOutput = new BufferedOutputStream(out);
			// Dump model to Output Stream
			model.writeTo(bOutput);
			// Close Stream
			bOutput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Method for loading an NGramProcessLM model from the disc
	 * 
	 * @param filename
	 *            The file to be loaded
	 * @return The model containing the data from the loaded file
	 */
	public static NGramProcessLM loadFromFile(String filename) {
		NGramProcessLM model = null;
		// Create File
		File f = new File("C:/Users/Andrew/workspace/TwoTimeNLM/resources/models/" + filename + ".txt");
		try {
			// Establish Input Stream
			InputStream input;
			input = new FileInputStream(f);
			BufferedInputStream bInput = new BufferedInputStream(input);
			// Read model from Input Stream
			model = NGramProcessLM.readFrom(bInput);
			// Close Stream
			bInput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return model;
	}
}