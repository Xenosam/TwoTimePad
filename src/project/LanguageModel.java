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

	public static String[] solver(File message, NGramProcessLM model, int x) {
		String[] output = new String[x*2];
		int currentChar;
		char c;
		char[] cXOR;
		boolean b = false;
		char[] strA = new char[model.maxNGram()];
		char[] strB = new char[model.maxNGram()];
		AnalysisPair[] workQueue = new AnalysisPair[x];
		AnalysisPair[] input = new AnalysisPair[x * x];
		try {
			// Open File
			BufferedReader br = new BufferedReader(new FileReader(message));
			// Read until file is closed
			int loop = 0;
			while ((currentChar = br.read()) != -1) {
				System.out.println("LOOP: " + loop);
				int index = 0;
				// Create XOR Map for input character
				c = (char) currentChar;
				cXOR = XORHandler(c);
				// Handle First Character
				if (b == false) {
					strA[0] = 2;
					strB[0] = 2;
					String s = new String(strA);
					String t = new String(strB);
					workQueue[0] = new AnalysisPair(s, t, model.prob(s) + model.prob(t), s, t);
					b = true;
					loop++;
					continue;
				} else {
					// Act for each item in the workqueue
					for (int i = 0; i < workQueue.length; i++) {
						System.out.println("i: " + i);
						if (workQueue[i] == null) {
							System.out.println("CONTINUE");
							continue;
						} else {
							if (loop < model.maxNGram() - 1) {
								// SKIP TRIM
								// Make NGRM model for loop vals
								NGramProcessLM tempModel = createModel(loop + 1);
								AnalysisPair[] temp = new AnalysisPair[256];
								// EXPAND
								for (int j = 0; j < 256; j++) {
									System.out.println("J1: " + j);
									AnalysisPair aP = workQueue[i];
									strA = aP.getNGram().toCharArray();
									strB = aP.getNGram2().toCharArray();
									char d = (char) j;
									char e = cXOR[j];
									strA[loop] = d;
									strB[loop] = e;
									// May have to edit to make sure that
									// probability checks that correct ngram
									// i.e. only [0] - [loop]
									double prob = tempModel.prob(new String(strA)) + tempModel.prob(new String(strB));
									temp[j] = new AnalysisPair(new String(strA), new String(strB), prob, aP.getData1(),
											aP.getData2());
									temp[j].addData(d, e);
								}
								// SORT
								temp = quickSort(0, 255, temp);
								// FILTER
								for (int j = 0; j < x; j++) {
									input[index] = temp[255 - j];
									System.out.println("INDEX: " + index);
									index++;
								}
							} else {
								if (loop != model.maxNGram() - 1) {
									// TRIM (if neccessary)
									strA = stringTrim(workQueue[i].getNGram().toCharArray());
									strB = stringTrim(workQueue[i].getNGram2().toCharArray());
								}
								// EXPAND
								AnalysisPair[] temp = new AnalysisPair[256];
								for (int j = 0; j < 256; j++) {
									System.out.println("J2: " + j);
									char d = (char) j;
									char e = cXOR[j];
									strA[model.maxNGram() - 1] = d;
									strB[model.maxNGram() - 1] = e;
									double prob = model.prob(new String(strA)) + model.prob(new String(strB));
									temp[j] = new AnalysisPair(new String(strA), new String(strB), prob,
											workQueue[i].getData1(), workQueue[i].getData2());
									temp[j].addData(d, e);
								}
								// SORT
								temp = quickSort(0, 255, temp);
								// FILTER
								for (int j = 0; j < x; j++) {
									System.out.println("INDEX: " + index);
									input[index] = temp[255 - j];
									index++;
								}
							}
						}
					}
				}
				// SORT INPUT
				workQueue = new AnalysisPair[x];
				if (loop == 1) {
					System.out.println("FLAG 1");
					input = quickSort(0, x - 1, input);
					for (int j = 0; j < x; j++) {
						workQueue[j] = input[(x - 1) - j];
					}
				} else {
					System.out.println("FLAG 2");
					input = quickSort(0, x * x - 1, input);
					// FILTER
					for (int j = 0; j < x; j++) {
						workQueue[j] = input[(input.length - 1) - j];
					}
				}
				input = new AnalysisPair[x * x];
				loop++;
			}
			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		for(int i = 0 ; i < workQueue.length; i++) {
			output[i] = workQueue[i].getData1();
		}
		for(int i = 0; i < workQueue.length; i++) {
			output[i + workQueue.length] = workQueue[i].getData2();
		}
		return output;
	}

	public static char[] stringTrim(char[] strA) {
		for (int j = 1; j < strA.length; j++) {
			// Remove least significant character
			strA[j - 1] = strA[j];
		}
		strA[strA.length - 1] = 0;
		return strA;
	}

	/**
	 * 
	 * @param message
	 * @param model
	 */
	public static String[] simpleSolver(char[] message, NGramProcessLM model) {
		// Strings for current NGram
		char[] strA = new char[model.maxNGram()];
		char[] strB = new char[model.maxNGram()];
		// Strings for building plaintext outputs
		String strSA = "";
		String strSB = "";
		// Variables for reading in each character from file
		char c;
		char[] cXOR;
		// Array for sorting/scoring and filtering
		AnalysisPair[] score = new AnalysisPair[256];
		// Output
		String[] output = new String[2];
		for (int i = 0; i < message.length; i++) {
			// Update c
			c = message[i];
			// Create XOR map
			cXOR = XORHandler(c);
			// Code until ngram size is reached
			System.out.println("int i: " + i + "\nchar c: " + c + "\nint c: " + Integer.valueOf((char) c));
			if (i <= model.maxNGram() - 2) {
				if (i == 0) {
					// File Load: First read character is ASCII - 02
					strA[i] = (char) 2;
					strB[i] = (char) 2;
					// Update output string
					strSA += strA[i];
					strSB += strB[i];
					System.out.println("A: " + strSA);
					System.out.println("B: " + strSB);

				} else {
					NGramProcessLM temp = createModel(i);
					// EXPAND
					for (int k = 0; k < 256; k++) {
						strA[i] = (char) k;
						strB[i] = cXOR[k];
						String s = (new String(strA.toString()) + new String(strB));
						double p = temp.prob(new String(strA)) + temp.prob(new String(strB));
						score[k] = new AnalysisPair(s, p);
					}
					// SORT
					score = quickSort(0, score.length - 1, score);
					// FILTER
					// Rest current ngram
					strA[i] = score[255].getNGram().toCharArray()[i];
					strB[i] = score[255].getNGram().toCharArray()[i * 2 + 1];
					// Update output string
					strSA += strA[i];
					strSB += strB[i];
					System.out.println("A: " + strSA);
					System.out.println("B: " + strSB);
				}
			} else {
				if (i != model.maxNGram() - 1) {
					strA = stringTrim(strA);
					strB = stringTrim(strB);
				}
				// EXPAND
				for (int j = 0; j < 256; j++) {
					strA[model.maxNGram() - 1] = (char) j;
					strB[model.maxNGram() - 1] = cXOR[j];
					// SCORE Strings: score[i] -> aPA[i] + aPB[i]
					String s = new String(strA) + new String(strB);
					double p = (model.prob(new String(strA)) + model.prob(new String(strB)));
					score[j] = new AnalysisPair(s, p);
				}
				// SORT
				score = quickSort(0, score.length - 1, score);
				// FILTER
				// Rest current ngram
				strA[model.maxNGram() - 1] = score[255].getNGram().toCharArray()[model.maxNGram() - 1];
				strB[model.maxNGram() - 1] = score[255].getNGram().toCharArray()[model.maxNGram() * 2 - 1];
				// Update output string
				strSA += strA[model.maxNGram() - 1];
				strSB += strB[model.maxNGram() - 1];
				System.out.println("A: " + strSA);
				System.out.println("B: " + strSB);
			}
		}
		System.out.println("OUTPUT A: " + strSA);
		System.out.println("OUTPUT B: " + strSB);

		output[0] = strSA;
		output[1] = strSB;

		return output;
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
	public static AnalysisPair[] quickSort(int low, int high, AnalysisPair[] output) {
		// Establish Low, High and Middle(The Pivot)
		int i = low;
		int j = high;
		// Take Value from the middle
		AnalysisPair pivot = output[low + (high - low) / 2];

		// Sort Loop
		while (i <= j) {
			// Increase low pointer
			while (output[i].getProbability() < pivot.getProbability()) {
				i++;
			}

			// Decrease high pointer
			while (output[j].getProbability() > pivot.getProbability()) {
				j--;
			}

			// Swap the positions of the high and low
			if (i <= j) {
				AnalysisPair temp = output[i];
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
			output[i] = new AnalysisPair(aP[i].getNGram(), aP[i].getProbability());
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