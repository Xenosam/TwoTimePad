package project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import com.aliasi.lm.NGramProcessLM;
import com.aliasi.lm.TrieCharSeqCounter;

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
		NGramProcessLM model = null;
		TrieCharSeqCounter counter = null;
		boolean fail = false;
		boolean newItem = false;
		boolean TCSC = false;
		String s = "";
		Scanner ui = new Scanner(System.in);
		// Create new ciphertext
		System.out.println("Create new Ciphertext? <Y/N>");
		s = ui.next();
		if (s.equals("Y") || s.equals("y")) {
			String a, b;
			System.out.println("Enter String 1: <string>");
			ui.nextLine();
			a = ui.nextLine();
			System.out.println("Enter String 2: <string>");
			b = ui.nextLine();
			System.out.println("Enter Filename: <string>");
			s = ui.nextLine();
			try {
				fail = createCiphertext(a.toCharArray(), b.toCharArray(), s);
			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else if (s.equals("N") || s.equals("n")) {
			System.out.println("continue");
		} else {
			System.out.println("Invalid Input");
			fail = true;
		}
		// Create Model
		System.out.println("Use Exisiting Model? <Y/N>");
		s = ui.next();
		int n = 0;
		int i;
		// Choose Load or New Model
		if (s.equals("Y") || s.equals("y")) {
			System.out.println("Enter Filename: <string>");
			s = String.valueOf(ui.next());
			File f = new File("./resources/models/" + s + ".txt");
			if (f.exists()) {
				model = loadFromFile(s);
				n = model.maxNGram();
				counter = loadCounter(s);
			} else {
				System.out.println("File Does Not Exist");
				fail = true;
			}
		} else if (s.equals("N") || s.equals("n")) {
			newItem = true;
			System.out.println("Creating New Model...");
			System.out.println("Enter n value: <int>");
			n = Integer.valueOf(ui.next());
			model = createModel(n);
			// Train the Model
			final File file = new File("./resources/corpus/");
			for (final File child : file.listFiles()) {
				// Calls train method for each file
				System.out.println("Training: " + child.getName());
				model = train(model, file.toString() + "/" + child.getName());
				counter = trainTCSC(counter, n, file.toString() + "/" + child.getName());
			}
			// Choose Language Smoothing
			System.out.println("Use Smoothing? <Y/N>");
			s = ui.next();
			if (s.equals("Y") || s.equals("y")) {
				System.out.println(
						"Select Method: <1/2/3>\n1: Laplace \n2: Good-Turing \n3: Witten-Bell \n4: Witten-Bell(LingPipe)");
				i = Integer.valueOf(ui.next());
				if (i == 1) {
					TCSC = true;
					counter = smoothingLaplace(counter, n);
				} else if (i == 2) {
					TCSC = true;
					counter = smoothingGoodTuring(counter, n);
				} else if (i == 3) {
					TCSC = true;
					counter = smoothingWittenBell(counter, n);
				} else if (i == 4) {
					// Automated Execution
					System.out.println("Simple or Complex Solver? <S/C>");
					s = ui.next();
					if (s.equals("C") || s.equals("c")) {
						System.out.println("Enter Ciphertext Filename: <string>");
						s = "./resources/ciphertext/" + ui.next() + ".txt";
						if (new File(s).exists()) {
							int x;
							File f = new File(s);
							System.out.println("Enter The Amount Of Results To Keep Each Pass: <int>");
							x = Integer.valueOf(ui.next());
							String[] output = solver(f, model, x);
							for (int j = 0; j < (output.length / 2); j++) {
								System.out.println("i: " + j);
								System.out.println("A: " + output[j]);
								System.out.println("B: " + output[j + (output.length / 2)]);
							}
						} else {
							System.out.println("File Does Not Exist");
							fail = true;
						}
					} else if (s.equals("S") || s.equals("s")) {
						System.out.println("Enter Ciphertext Message: <string>");
						s = ui.next();
						String[] output = simpleSolver(s.toCharArray(), model);
						System.out.println("A: " + output[0] + "\nB: " + output[1]);
					} else {
						System.out.println("Invalid Input");
						fail = true;
					}
				} else {
					System.out.println("Invalid Input");
					fail = true;
				}
			} else if (s.equals("N") || s.equals("n")) {
				model.setLambdaFactor(0);
				System.out.println("Continuing");
			} else {
				System.out.println("Invalid Input");
				fail = true;
			}
		} else {
			System.out.println("Invalid Input");
			fail = true;
		}
		if (!fail && TCSC) {
			// Implemented Execution
			System.out.println("Enter Ciphertext Filename: <string>");
			s = "./resources/ciphertext/" + ui.next() + ".txt";
			if (new File(s).exists()) {
				int x;
				File f = new File(s);
				System.out.println("Enter The Amount Of Results To Keep Each Pass: <int>");
				x = Integer.valueOf(ui.next());
				String[] output = TCSCSolver(f, counter, n, x);
				for (int j = 0; j < (output.length / 2); j++) {
					System.out.println("i: " + j);
					System.out.println("A: " + output[j]);
					System.out.println("B: " + output[j + (output.length / 2)]);
				}
			} else {
				System.out.println("File Does Not Exist");
				fail = true;
			}
		}
		// Ask to Save model
		if (!fail && newItem) {
			System.out.println("Save Model To Resources? <Y/N>");
			s = ui.next();
			if (s.equals("Y") || s.equals("y")) {
				System.out.println("Enter Filename: <string>");
				s = ui.next();
				File f = new File("./resources/models/" + s + ".txt");
				if (f.exists()) {
					System.out.println("File Exists, Delete and Recreate? <Y/N>");
					s = ui.next();
					if (s.equals("Y") || s.equals("y")) {
						f.delete();
						saveToFile((s), model);
						saveCounter(counter, s);
					} else if (s.equals("N") || s.equals("n")) {
						System.out.println("Continuing");
					} else {
						System.out.println("Invalid Input");
						fail = true;
					}
				} else {
					saveToFile((s), model);
				}
			} else if (s.equals("N") || s.equals("n")) {
				System.out.println("Continuing");
			} else {
				System.out.println("Invalid Input");
				fail = true;
			}
		}
		ui.close();
	}

	/**
	 * 
	 * @param counter
	 * @param n
	 * @return
	 */
	public static String[] TCSCSolver(File message, TrieCharSeqCounter counter, int n, int x) {
		String[] output = new String[x * 2];
		int currentChar;
		char c;
		char[] cXOR;
		boolean b = false;
		char[] strA = new char[n];
		char[] strB = new char[n];
		double prob = 0.0;
		long total = counter.totalSequenceCount();
		AnalysisPair[] workQueue = new AnalysisPair[x];
		AnalysisPair[] input = new AnalysisPair[x * x];
		try {
			// Open File
			BufferedReader br = new BufferedReader(new FileReader(message));
			// Read until file is closed
			int loop = 0;
			while ((currentChar = br.read()) != -1) {
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
					prob = Math.log((counter.count(s) / total) / (counter.count(t) / total));
					workQueue[0] = new AnalysisPair(s, t, prob, s, t);
					b = true;
					loop++;
					continue;
				} else {
					// Act for each item in the workqueue
					for (int i = 0; i < workQueue.length; i++) {
						if (workQueue[i] == null) {
							continue;
						} else {
							if (loop < n - 1) {
								// SKIP TRIM
								// Make NGRM model for loop vals
								TrieCharSeqCounter tempCounter = null;
								AnalysisPair[] temp = new AnalysisPair[256];
								// Train the Model
								final File file = new File("./resources/corpus/");
								for (final File child : file.listFiles()) {
									// Calls train method for each file
									System.out.println("Training: " + child.getName());
									tempCounter = trainTCSC(tempCounter, loop, file.toString() + "/" + child.getName());
								}
								// EXPAND
								for (int j = 0; j < 256; j++) {
									AnalysisPair aP = workQueue[i];
									strA = aP.getNGram().toCharArray();
									strB = aP.getNGram2().toCharArray();
									char d = (char) j;
									char e = cXOR[j];
									strA[loop] = d;
									strB[loop] = e;
									String s = new String(strA);
									String t = new String(strB);
									prob = Math.log((tempCounter.count(s) / total) / (tempCounter.count(t) / total));
									temp[j] = new AnalysisPair(new String(strA), new String(strB), prob, aP.getData1(),
											aP.getData2());
									temp[j].addData(d, e);
								}
								// SORT
								temp = quickSort(0, 255, temp);
								// FILTER
								for (int j = 0; j < x; j++) {
									input[index] = temp[255 - j];
									index++;
								}
							} else {
								if (loop != n - 1) {
									// TRIM (if neccessary)
									strA = stringTrim(workQueue[i].getNGram().toCharArray());
									strB = stringTrim(workQueue[i].getNGram2().toCharArray());
								}
								// EXPAND
								AnalysisPair[] temp = new AnalysisPair[256];
								for (int j = 0; j < 256; j++) {
									char d = (char) j;
									char e = cXOR[j];
									strA[n - 1] = d;
									strB[n - 1] = e;
									String s = new String(strA);
									String t = new String(strB);
									prob = Math.log((counter.count(s) / total) / (counter.count(t) / total));
									temp[j] = new AnalysisPair(new String(strA), new String(strB), prob,
											workQueue[i].getData1(), workQueue[i].getData2());
									temp[j].addData(d, e);
								}
								// SORT
								temp = quickSort(0, 255, temp);
								// FILTER
								for (int j = 0; j < x; j++) {
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
					input = quickSort(0, x - 1, input);
					for (int j = 0; j < x; j++) {
						workQueue[j] = input[(x - 1) - j];
					}
				} else {
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
		for (int i = 0; i < workQueue.length; i++) {
			output[i] = workQueue[i].getData1();
		}
		for (int i = 0; i < workQueue.length; i++) {
			output[i + workQueue.length] = workQueue[i].getData2();
		}
		return output;
	}

	/**
	 * Method for turning to character arrays into an XOR'd ciphertext
	 * 
	 * @param a
	 *            String for our "Plaintext a"
	 * @param b
	 *            String for our "Plaintext b"
	 * @throws IOException
	 *             Error for File IO
	 */
	public static boolean createCiphertext(char[] a, char[] b, String filename) throws IOException {
		if (a.length != b.length) {
			System.out.println("Strings must be the same length");
			System.out.println(new String(a) + "\nLength: " + a.length);
			System.out.println(new String(b) + "\nLength: " + b.length);
			return true;
		}
		FileWriter fw = new FileWriter("./resources/ciphertext/" + filename + ".txt");
		for (int i = 0; i < a.length; i++) {
			fw.append((char) ((int) a[i] ^ (int) b[i]));
		}
		fw.close();
		return false;
	}

	/**
	 * The complex solver method for using a given NGramProcessLM model to
	 * assess the shortest path through the hidden markov model in the
	 * ciphertext
	 * 
	 * @param message
	 *            the file containing the ciphertext
	 * @param model
	 *            the model to use for the ngram probabilities
	 * @param x
	 *            the amount of results to keep and extend each cycle
	 * @return
	 */
	public static String[] solver(File message, NGramProcessLM model, int x) {
		String[] output = new String[x * 2];
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
					workQueue[0] = new AnalysisPair(s, t, Math.log(model.prob(s) * model.prob(t)), s, t);
					b = true;
					loop++;
					continue;
				} else {
					// Act for each item in the workqueue
					for (int i = 0; i < workQueue.length; i++) {
						if (workQueue[i] == null) {
							continue;
						} else {
							if (loop < model.maxNGram() - 1) {
								// SKIP TRIM
								// Make NGRM model for loop vals
								NGramProcessLM tempModel = createModel(loop + 1);
								AnalysisPair[] temp = new AnalysisPair[256];
								// Train the Model
								final File file = new File("./resources/corpus/");
								for (final File child : file.listFiles()) {
									// Calls train method for each file
									System.out.println("Training: " + child.getName());
									tempModel = train(tempModel, file.toString() + "/" + child.getName());
								}
								// EXPAND
								for (int j = 0; j < 256; j++) {
									AnalysisPair aP = workQueue[i];
									strA = aP.getNGram().toCharArray();
									strB = aP.getNGram2().toCharArray();
									char d = (char) j;
									char e = cXOR[j];
									strA[loop] = d;
									strB[loop] = e;
									double prob = Math
											.log(tempModel.prob(new String(strA)) * tempModel.prob(new String(strB)));
									temp[j] = new AnalysisPair(new String(strA), new String(strB), prob, aP.getData1(),
											aP.getData2());
									temp[j].addData(d, e);
								}
								// SORT
								temp = quickSort(0, 255, temp);
								// FILTER
								for (int j = 0; j < x; j++) {
									input[index] = temp[255 - j];
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
									char d = (char) j;
									char e = cXOR[j];
									strA[model.maxNGram() - 1] = d;
									strB[model.maxNGram() - 1] = e;
									double prob = Math.log(model.prob(new String(strA)) * model.prob(new String(strB)));
									temp[j] = new AnalysisPair(new String(strA), new String(strB), prob,
											workQueue[i].getData1(), workQueue[i].getData2());
									temp[j].addData(d, e);
								}
								// SORT
								temp = quickSort(0, 255, temp);
								// FILTER
								for (int j = 0; j < x; j++) {
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
					input = quickSort(0, x - 1, input);
					for (int j = 0; j < x; j++) {
						workQueue[j] = input[(x - 1) - j];
					}
				} else {
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
		for (int i = 0; i < workQueue.length; i++) {
			output[i] = workQueue[i].getData1();
		}
		for (int i = 0; i < workQueue.length; i++) {
			output[i + workQueue.length] = workQueue[i].getData2();
		}
		return output;
	}

	/**
	 * Moves each character back an index, dropping the character that was at
	 * index 0 and clears the character at index length -1
	 * 
	 * @param strA
	 *            the string to be trimmed
	 * @return the trimmed string
	 */
	public static char[] stringTrim(char[] strA) {
		for (int j = 1; j < strA.length; j++) {
			// Remove least significant character
			strA[j - 1] = strA[j];
		}
		strA[strA.length - 1] = 0;
		return strA;
	}

	/**
	 * Solution to the problem that assumes the simplest state in that the
	 * ciphertext is very short and that our value for the amount of items kept
	 * each cycle is 1
	 * 
	 * @param message
	 *            ciphertext string
	 * @param model
	 *            model for ngram probabilities
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
			if (i <= model.maxNGram() - 2) {
				if (i == 0) {
					// File Load: First read character is ASCII - 02
					strA[i] = (char) 2;
					strB[i] = (char) 2;
					// Update output string
					strSA += strA[i];
					strSB += strB[i];

				} else {
					NGramProcessLM temp = createModel(i);
					// EXPAND
					for (int k = 0; k < 256; k++) {
						strA[i] = (char) k;
						strB[i] = cXOR[k];
						String s = (new String(strA.toString()) + new String(strB));
						double p = Math.log(temp.prob(new String(strA)) * temp.prob(new String(strB)));
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
					double p = Math.log(model.prob(new String(strA)) * model.prob(new String(strB)));
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
			}
		}

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
	 * TODO: JAVADOC
	 * 
	 * @param tcsc
	 * @param n
	 * @return
	 */
	public static AnalysisPair[] createTCSCAPModel(TrieCharSeqCounter tcsc, int n) {
		// TODO Finish Method
		return null;
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
	 * Method for training a TrieCharSeqCounter for the counts of ngrams
	 * appearing in our model for the given value of n
	 * 
	 * @param tcsc
	 *            the TrieCharSeqCounter object we're training
	 * @param n
	 *            the size of the ngram
	 * @param filename
	 *            the file we are training from
	 * @return the trained TrieCharSeqCounter object
	 */
	public static TrieCharSeqCounter trainTCSC(TrieCharSeqCounter tcsc, int n, String filename) {
		File f = new File(filename);
		int loop = 0;
		int c;
		char currentchar;
		char[] ngram = new char[n];
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			// Read file character by character
			while ((c = br.read()) != -1) {
				currentchar = (char) c;
				if (loop < n - 1) {
					// Fill ngram until almost full
					ngram[loop] = currentchar;
				} else if (loop == n - 1) {
					// Fill final spot (No Trim) then count
					ngram[loop] = currentchar;
					tcsc.incrementSubstrings(new String(ngram));
				} else {
					// Trim then Fill then count
					stringTrim(ngram);
					ngram[n - 1] = currentchar;
					tcsc.incrementSubstrings(new String(ngram));
				}
				// Count Loops
				loop++;
			}
			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		// Return
		return tcsc;
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
	public static TrieCharSeqCounter smoothingLaplace(TrieCharSeqCounter counter, int n) {
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
			counter.incrementSubstrings(tempS);
			// Moves back to least significant character
			j = n - 1;
		}
		// Returns the smoothed model
		return counter;
	}

	/**
	 * TODO: JAVADOC
	 * 
	 * @param model
	 * @param n
	 * @return
	 */
	public static TrieCharSeqCounter smoothingGoodTuring(TrieCharSeqCounter counter, int n) {
		// Follows the same process as the AP model production
		char[] cSeq = new char[n];
		int temp;
		String tempS;
		int val = (int) Math.pow(256, n);
		AnalysisPair[] aP = new AnalysisPair[val];
		// Fill string with ASCII(0) characters
		for (int i = 0; i < n; i++) {
			cSeq[i] = Character.valueOf((char) 0);
		}
		int j = n - 1;
		for (int i = 0; i < val; i++) {
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
			aP[i] = new AnalysisPair(tempS, counter.count(tempS));
			// Moves back to least significant character
			j = n - 1;
		}
		// Returns the smoothed model
		return counter;
	}

	/**
	 * TODO: JAVADOC
	 * 
	 * @param counter
	 * @param n
	 * @return
	 */
	public static TrieCharSeqCounter smoothingWittenBell(TrieCharSeqCounter counter, int n) {
		// Follows the same process as the AP model production
		char[] cSeq = new char[n];
		int temp;
		int count = 0;
		String tempS;
		int val = (int) Math.pow(256, n);
		AnalysisPair[] aP = new AnalysisPair[val];
		// Fill string with ASCII(0) characters
		for (int i = 0; i < n; i++) {
			cSeq[i] = Character.valueOf((char) 0);
		}
		int j = n - 1;
		for (int i = 0; i < val; i++) {
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
			aP[i] = new AnalysisPair(tempS, counter.count(tempS));
			// Moves back to least significant character
			j = n - 1;
			// Check for smooth
			if(counter.count(tempS) > 1) {
				count++;
			}
		}
		double offset = count / counter.totalSequenceCount();
		System.out.println("OFFSET: " + offset);
		for(int i = 0; i < val; i++) {
			if(aP[i].getProbability() == 0) {
				//
			}
		}
		// Returns the smoothed model
		return counter;
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
		File f = new File("./resources/models/" + filename + ".txt");
		System.out.println("SAVING: " + f.getAbsolutePath());
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
		File f = new File("./resources/models/" + filename + ".txt");
		System.out.println("LOADING: " + f.getName());
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

	/**
	 * This method is for saving a TrieCharSeqCounter object to the disc
	 * 
	 * @param filename
	 *            The name of the file to be created
	 * @param model
	 *            The counter being exported
	 */
	public static void saveCounter(TrieCharSeqCounter tcsc, String filename) {
		// Create File
		File f = new File("./resources/counters/" + filename + ".txt");
		System.out.println("SAVING: " + f.getAbsolutePath());
		OutputStream out;
		try {
			// Establish Output Stream
			out = new FileOutputStream(f);
			tcsc.writeTo(out);
			BufferedOutputStream bOutput = new BufferedOutputStream(out);
			// Dump model to Output Stream
			tcsc.writeTo(bOutput);
			// Close Stream
			bOutput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Method for loading an TrieCharSeqCounter object from the disc
	 * 
	 * @param filename
	 *            The file to be loaded
	 * @return The counter containing the data from the loaded file
	 */
	public static TrieCharSeqCounter loadCounter(String filename) {
		TrieCharSeqCounter tcsc = null;
		// Create File
		File f = new File("./resources/counters/" + filename + ".txt");
		System.out.println("LOADING: " + f.getName());
		try {
			// Establish Input Stream
			InputStream input;
			input = new FileInputStream(f);
			BufferedInputStream bInput = new BufferedInputStream(input);
			// Read model from Input Stream
			tcsc = TrieCharSeqCounter.readFrom(bInput);
			// Close Stream
			bInput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return tcsc;
	}
}